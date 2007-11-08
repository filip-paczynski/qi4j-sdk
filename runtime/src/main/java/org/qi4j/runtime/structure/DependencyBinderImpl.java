/*
 * Copyright (c) 2007, Rickard �berg. All Rights Reserved.
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

import java.lang.annotation.Annotation;
import org.qi4j.CompositeBuilder;
import org.qi4j.dependency.DependencyResolution;
import org.qi4j.dependency.DependencyResolver;
import org.qi4j.dependency.DependencyResolverRegistry;
import org.qi4j.dependency.InjectionKey;
import org.qi4j.model.DependencyKey;
import org.qi4j.persistence.EntityComposite;
import org.qi4j.query.Query;
import org.qi4j.structure.DependencyBinder;

/**
 * TODO
 */
public class DependencyBinderImpl
    implements DependencyBinder
{
    DependencyResolverRegistry registry;


    public void bind( DependencyKey key, DependencyResolver resolver, Object instance )
    {
    }

    public void bind( Class<? extends Annotation> scope, InjectionKey key, DependencyResolution resolution )
    {
/*
        DependencyResolver resolver = registry.getDependencyResolver( scope);
        if ( Service.class.equals(scope))
        {
            ServiceDependencyResolver serviceResolver = (ServiceDependencyResolver) resolver;
            serviceResolver.
        }
*/
    }

    public <T, I extends T> void serviceSingleton( Class<T> serviceInterface, Class<I> serviceImplementation )
    {
    }

    public <T, I extends T> void serviceSingleton( Class<T> serviceInterface, I instance )
    {
    }

    public <T extends EntityComposite> void entityPrototype( Class<T> entityComposite, CompositeBuilder<T> entityBuilder )
    {
    }

    public <T extends EntityComposite> void entityQuery( Class<T> entityComposite, Query<T> query )
    {
    }
}
