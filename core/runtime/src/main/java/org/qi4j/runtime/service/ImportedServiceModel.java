/*
 * Copyright (c) 2008, Rickard Öberg. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.qi4j.runtime.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.qi4j.api.common.MetaInfo;
import org.qi4j.api.common.Visibility;
import org.qi4j.api.service.ImportedServiceDescriptor;
import org.qi4j.api.service.ServiceImporter;
import org.qi4j.api.service.ServiceImporterException;
import org.qi4j.api.structure.Module;
import org.qi4j.functional.Visitable;
import org.qi4j.functional.Visitor;

import static org.qi4j.functional.Iterables.iterable;

/**
 * JAVADOC
 */
public final class ImportedServiceModel
    implements ImportedServiceDescriptor, Visitable<ImportedServiceModel>
{
    private final Class<?> type;
    private final Visibility visibility;
    private final Class<? extends ServiceImporter> serviceImporter;
    private final String identity;
    private final MetaInfo metaInfo;
    private String moduleName;

    public ImportedServiceModel( Class serviceType,
                                 Visibility visibility,
                                 Class<? extends ServiceImporter> serviceImporter,
                                 String identity,
                                 MetaInfo metaInfo, String moduleName
    )
    {
        type = serviceType;
        this.visibility = visibility;
        this.serviceImporter = serviceImporter;
        this.identity = identity;
        this.metaInfo = metaInfo;
        this.moduleName = moduleName;
    }

    public Iterable<Class<?>> types()
    {
        Iterable<? extends Class<?>> iterable = iterable( type );
        return (Iterable<Class<?>>) iterable;
    }

    public Visibility visibility()
    {
        return visibility;
    }

    public <T> T metaInfo( Class<T> infoType )
    {
        return metaInfo.get( infoType );
    }

    public Class<? extends ServiceImporter> serviceImporter()
    {
        return serviceImporter;
    }

    @Override
    public Class<?> type()
    {
        return type;
    }

    public String identity()
    {
        return identity;
    }

    public String moduleName()
    {
        return moduleName;
    }

    @Override
    public boolean isAssignableTo( Class<?> type )
    {
        return this.type.isAssignableFrom( type );
    }

    @Override
    public <ThrowableType extends Throwable> boolean accept( Visitor<? super ImportedServiceModel, ThrowableType> visitor )
        throws ThrowableType
    {
        return visitor.visit( this );
    }

    public <T> ImportedServiceInstance<T> importInstance( Module module )
    {
        ServiceImporter importer = module.newObject( serviceImporter );
        try
        {
            T instance = (T) importer.importService( this );
            return new ImportedServiceInstance<T>( instance, importer );
        }
        catch( ServiceImporterException e )
        {
            throw e;
        }
        catch( Exception e )
        {
            throw new ServiceImporterException( "Could not import service " + identity, e );
        }
    }

    public Object newProxy( InvocationHandler serviceInvocationHandler )
    {
        if( type.isInterface() )
        {
            return Proxy.newProxyInstance( type.getClassLoader(),
                                           new Class[]{ type },
                                           serviceInvocationHandler );
        }
        else
        {
            Class[] interfaces = type.getInterfaces();
            return Proxy.newProxyInstance( type.getClassLoader(),
                                           interfaces,
                                           serviceInvocationHandler );
        }
    }

    @Override
    public String toString()
    {
        return type.getName() + ":" + identity;
    }
}