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

package org.qi4j.runtime.structure;

import org.qi4j.api.common.MetaInfo;
import org.qi4j.api.structure.ModuleDescriptor;
import org.qi4j.functional.HierarchicalVisitor;
import org.qi4j.functional.VisitableHierarchy;
import org.qi4j.runtime.composite.TransientsModel;
import org.qi4j.runtime.entity.EntitiesModel;
import org.qi4j.runtime.object.ObjectsModel;
import org.qi4j.runtime.service.ImportedServicesModel;
import org.qi4j.runtime.service.ServicesModel;
import org.qi4j.runtime.value.ValuesModel;

/**
 * JAVADOC
 */
public class ModuleModel
    implements ModuleDescriptor, VisitableHierarchy<Object, Object>
{
    private final TransientsModel transientsModel;
    private final EntitiesModel entitiesModel;
    private final ObjectsModel objectsModel;
    private final ValuesModel valuesModel;
    private final ServicesModel servicesModel;
    private final ImportedServicesModel importedServicesModel;

    private final String name;
    private MetaInfo metaInfo;

    public ModuleModel( String name,
                        MetaInfo metaInfo, TransientsModel transientsModel,
                        EntitiesModel entitiesModel,
                        ObjectsModel objectsModel,
                        ValuesModel valuesModel,
                        ServicesModel servicesModel,
                        ImportedServicesModel importedServicesModel
    )
    {
        this.name = name;
        this.metaInfo = metaInfo;
        this.transientsModel = transientsModel;
        this.entitiesModel = entitiesModel;
        this.objectsModel = objectsModel;
        this.valuesModel = valuesModel;
        this.servicesModel = servicesModel;
        this.importedServicesModel = importedServicesModel;
    }

    public String name()
    {
        return name;
    }

    public <T> T metaInfo( Class<T> infoType )
    {
        return metaInfo.get( infoType );
    }

    @Override
    public <ThrowableType extends Throwable> boolean accept( HierarchicalVisitor<? super Object, ? super Object, ThrowableType> modelVisitor )
        throws ThrowableType
    {
        if( modelVisitor.visitEnter( this ) )
        {
            if( transientsModel.accept( modelVisitor ) )
            {
                if( entitiesModel.accept( modelVisitor ) )
                {
                    if( servicesModel.accept( modelVisitor ) )
                    {
                        if( importedServicesModel.accept( modelVisitor ) )
                        {
                            if( objectsModel.accept( modelVisitor ) )
                            {
                                valuesModel.accept( modelVisitor );
                            }
                        }
                    }
                }
            }
        }
        return modelVisitor.visitLeave( this );
    }

    // Context

    public ModuleInstance newInstance( LayerInstance layerInstance )
    {
        return new ModuleInstance( this, layerInstance, transientsModel, entitiesModel, objectsModel, valuesModel, servicesModel, importedServicesModel );
    }

    @Override
    public String toString()
    {
        return name;
    }
}
