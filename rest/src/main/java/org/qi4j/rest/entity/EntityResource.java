/*
 * Copyright 2008 Niclas Hedhman. All rights Reserved.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.qi4j.rest.entity;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.qi4j.api.common.QualifiedName;
import org.qi4j.api.common.TypeName;
import org.qi4j.api.entity.EntityReference;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.injection.scope.Uses;
import org.qi4j.library.rdf.entity.EntityStateSerializer;
import org.qi4j.library.rdf.serializer.RdfXmlSerializer;
import org.qi4j.spi.Qi4jSPI;
import org.qi4j.spi.entity.*;
import org.qi4j.spi.entity.association.AssociationType;
import org.qi4j.spi.entity.association.ManyAssociationType;
import org.qi4j.spi.property.PropertyType;
import org.restlet.Context;
import org.restlet.data.*;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.representation.WriterRepresentation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;

import java.io.*;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class EntityResource extends Resource
{
    public static Object toValue(String stringValue, QualifiedName propertyName, TypeName propertyType)
            throws IllegalArgumentException
    {
        Object newValue = null;
        try
        {
            // TODO A ton of more types need to be added here. Converter registration mechanism needed?
            newValue = null;
            if (propertyType.isClass(String.class))
            {
                newValue = stringValue;
            } else if (propertyType.isClass(Integer.class))
            {
                newValue = Integer.parseInt(stringValue);
            }
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Value '" + stringValue + "' is not of type " + propertyType);
        }

        return newValue;
    }

    public static Object toString(Object newValue)
            throws IllegalArgumentException
    {
        if (newValue == null)
        {
            return "";
        } else
        {
            return newValue.toString();
        }
    }

    @Service
    private EntityStore entityStore;
    @Service
    private EntityTypeRegistry typeRegistry;
    @Structure
    private Qi4jSPI spi;
    @Uses
    EntityStateSerializer entitySerializer;

    private String identity;

    public EntityResource(@Uses Context context, @Uses Request request, @Uses Response response)
            throws ClassNotFoundException
    {
        super(context, request, response);

        // Define the supported variant.
        getVariants().add(new Variant(MediaType.TEXT_HTML));
        getVariants().add(new Variant(MediaType.APPLICATION_RDF_XML));
        getVariants().add(new Variant(MediaType.APPLICATION_JAVA_OBJECT));
        setModifiable(true);

        // /entity/{identity}
        Map<String, Object> attributes = getRequest().getAttributes();
        identity = (String) attributes.get("identity");
    }

    /**
     * Handle DELETE requests.
     */
    @Override
    public void removeRepresentations()
            throws ResourceException
    {
        try
        {
            entityStore.prepare(Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.singleton(EntityReference.parseEntityReference(identity))).commit();
            getResponse().setStatus(Status.SUCCESS_NO_CONTENT);
        }
        catch (EntityNotFoundException e)
        {
            getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
        }
    }

    @Override
    public Representation represent(Variant variant)
            throws ResourceException
    {
        EntityState entityState = getEntityState();

        // Check modification date
        Date lastModified = getRequest().getConditions().getModifiedSince();
        if (lastModified != null)
        {
            if (lastModified.getTime() / 1000 == entityState.lastModified() / 1000)
            {
                throw new ResourceException(Status.REDIRECTION_NOT_MODIFIED);
            }
        }

        // Generate the right representation according to its media type.
        if (MediaType.APPLICATION_RDF_XML.equals(variant.getMediaType()))
        {
            return entityHeaders(representRdfXml(entityState), entityState);
        } else if (MediaType.TEXT_HTML.equals(variant.getMediaType()))
        {
            return entityHeaders(representHtml(entityState), entityState);
        } else if (MediaType.APPLICATION_JAVA_OBJECT.equals(variant.getMediaType()))
        {
            return entityHeaders(representJava(entityState), entityState);
        }

        throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
    }

    private EntityState getEntityState()
            throws ResourceException
    {
        EntityState entityState;
        try
        {
            entityState = entityStore.getEntityState(EntityReference.parseEntityReference(identity));
        }
        catch (EntityNotFoundException e)
        {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
        }
        catch (UnknownEntityTypeException e)
        {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
        }
        return entityState;
    }

    private Representation entityHeaders(Representation representation, EntityState entityState)
    {
        representation.setModificationDate(new Date(entityState.lastModified()));
        representation.setTag(new Tag("" + entityState.version()));
        representation.setCharacterSet(CharacterSet.UTF_8);
        representation.setLanguages(Collections.singletonList(Language.ENGLISH));

        return representation;
    }

    private Representation representHtml(final EntityState entity)
    {
        return new WriterRepresentation(MediaType.TEXT_HTML)
        {
            public void write(Writer writer) throws IOException
            {
                PrintWriter out = new PrintWriter(writer);
                out.println("<html><head><title>" + entity.identity() + "</title><link rel=\"alternate\" type=\"application/rdf+xml\" href=\"" + entity.identity() + ".rdf\"/></head><body>");
                out.println("<h1>" + entity.identity() + "</a>)</h1>");

                out.println("<form method=\"post\" action=\"" + getRequest().getResourceRef().getPath() + "\">\n");
                out.println("<fieldset><legend>Properties</legend>\n<table>");

                for (EntityTypeReference entityTypeReference : entity.entityTypeReferences())
                {
                    final EntityType type = typeRegistry.getEntityType(entityTypeReference);

                    for (PropertyType propertyType : type.properties())
                    {
                        String value = entity.getProperty(propertyType.stateName());
                        if (value == null)
                        {
                            value = "";
                        }
                        out.println("<tr><td>" +
                                "<label for=\"" + propertyType.stateName().qualifiedName() + "\" >" +
                                propertyType.stateName().qualifiedName().name() +
                                "</label></td>\n" +
                                "<td><input " +
                                "type=\"text\" " +
                                (propertyType.propertyType() != PropertyType.PropertyTypeEnum.MUTABLE ? "readonly=\"true\" " : "") +
                                "name=\"" + propertyType.stateName().qualifiedName() + "\" " +
                                "value=\"" + EntityResource.toString(value) + "\"></td></tr>");
                    }
                    out.println("</table></fieldset>\n");

                    out.println("<fieldset><legend>Associations</legend>\n<table>");
                    for (AssociationType associationType : type.associations())
                    {
                        Object value = entity.getAssociation(associationType.stateName());
                        if (value == null)
                        {
                            value = "";
                        }
                        out.println("<tr><td>" +
                                "<label for=\"" + associationType.qualifiedName() + "\" >" +
                                associationType.qualifiedName().name() +
                                "</label></td>\n" +
                                "<td><input " +
                                "type=\"text\" " +
                                "size=\"80\" " +
                                "name=\"" + associationType.qualifiedName() + "\" " +
                                "value=\"" + value + "\"></td></tr>");
                    }
                    out.println("</table></fieldset>\n");

                    out.println("<fieldset><legend>Many manyAssociations</legend>\n<table>");
                    for (ManyAssociationType associationType : type.manyAssociations())
                    {
                        ManyAssociationState identities = entity.getManyAssociation(associationType.stateName());
                        String value = "";
                        for (EntityReference identity : identities)
                        {
                            value += identity.toString() + "\n";
                        }

                        out.println("<tr><td>" +
                                "<label for=\"" + associationType.qualifiedName() + "\" >" +
                                associationType.qualifiedName().name() +
                                "</label></td>\n" +
                                "<td><textarea " +
                                "rows=\"10\" " +
                                "cols=\"80\" " +
                                "name=\"" + associationType.qualifiedName() + "\" >" +
                                value +
                                "</textarea></td></tr>");
                    }
                    out.println("</table></fieldset>\n");
                    out.println("<input type=\"submit\" value=\"Update\"/></form>\n");

                    out.println("</body></html>\n");
                    out.close();
                }
            }
        };
    }

    private Representation representRdfXml(final EntityState entity) throws ResourceException
    {
        Representation representation = new WriterRepresentation(MediaType.APPLICATION_RDF_XML)
        {
            public void write(Writer writer) throws IOException
            {
                try
                {
                    Iterable<Statement> statements = entitySerializer.serialize(entity);
                    new RdfXmlSerializer().serialize(statements, writer);
                }
                catch (RDFHandlerException e)
                {
                    throw (IOException) new IOException().initCause(e);
                }

                writer.close();
            }
        };
        representation.setCharacterSet(CharacterSet.UTF_8);
        return representation;

    }

    private Representation representJava(final EntityState entity) throws ResourceException
    {
        return new OutputRepresentation(MediaType.APPLICATION_JAVA_OBJECT)
        {
            public void write(OutputStream outputStream) throws IOException
            {
                ObjectOutputStream oout = new ObjectOutputStream(outputStream);
                oout.writeObject(entity);
                oout.close();
            }
        };
    }

    @Override
    @SuppressWarnings("unused")
    public void acceptRepresentation(Representation entity) throws ResourceException
    {
        storeRepresentation(entity);
    }

    /**
     * Handle PUT requests.
     */
    @Override
    public void storeRepresentation(Representation entityRepresentation)
            throws ResourceException
    {
        EntityState entity = getEntityState();

        Form form = new Form(entityRepresentation);

        try
        {
            for (EntityTypeReference entityTypeReference : entity.entityTypeReferences())
            {
                final EntityType type = typeRegistry.getEntityType(entityTypeReference);


                for (PropertyType propertyType : type.properties())
                {
                    if (propertyType.propertyType() == PropertyType.PropertyTypeEnum.MUTABLE)
                    {
                        String newStringValue = form.getFirstValue(propertyType.qualifiedName().toString());
                        entity.setProperty(propertyType.stateName(), newStringValue);
                    }
                }
                for (AssociationType associationType : type.associations())
                {
                    String newStringAssociation = form.getFirstValue(associationType.qualifiedName().toString());
                    if (newStringAssociation == null || newStringAssociation.equals(""))
                    {
                        entity.setAssociation(associationType.stateName(), null);
                    } else
                    {
                        entity.setAssociation(associationType.stateName(), EntityReference.parseEntityReference(newStringAssociation));
                    }
                }
                for (ManyAssociationType associationType : type.manyAssociations())
                {
                    String newStringAssociation = form.getFirstValue(associationType.qualifiedName().toString());
                    ManyAssociationState manyAssociation = entity.getManyAssociation(associationType.stateName());
                    if (newStringAssociation == null)
                    {
                        continue;
                    }

                    BufferedReader bufferedReader = new BufferedReader(new StringReader(newStringAssociation));
                    String identity;

                    try
                    {
                        // Synchronize old and new association
                        int index = 0;
                        while ((identity = bufferedReader.readLine()) != null)
                        {
                            EntityReference reference = new EntityReference(identity);

                            if (manyAssociation.count() < index && manyAssociation.get(index).equals(reference))
                                continue;

                            manyAssociation.remove(reference);
                            manyAssociation.add(index++, reference);
                        }

                        // Remove "left-overs"
                        while (manyAssociation.count() > index)
                        {
                            manyAssociation.remove(manyAssociation.get(index + 1));
                        }
                    }
                    catch (IOException e)
                    {
                        // Ignore
                    }
                }
            }
        }

        catch (IllegalArgumentException e)
        {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
        }

        try
        {
            entityStore.prepare(Collections.EMPTY_LIST, Collections.singleton(entity), Collections.EMPTY_LIST).commit();
        }
        catch (ConcurrentEntityStateModificationException e)
        {
            throw new ResourceException(Status.CLIENT_ERROR_CONFLICT);
        }
        catch (EntityNotFoundException e)
        {
            throw new ResourceException(Status.CLIENT_ERROR_GONE);
        }

        getResponse().setStatus(Status.SUCCESS_RESET_CONTENT);
    }

    @Override
    public boolean isModifiable()
    {
        return true;
    }
}