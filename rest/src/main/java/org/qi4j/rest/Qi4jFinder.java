/*
 * Copyright (c) 2008, Niclas Hedhman. All Rights Reserved.
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
package org.qi4j.rest;

import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.injection.scope.Uses;
import org.qi4j.api.object.ObjectBuilder;
import org.qi4j.api.object.ObjectBuilderFactory;
import org.qi4j.library.rdf.entity.EntityStateSerializer;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Finder;
import org.restlet.resource.Handler;

public class Qi4jFinder extends Finder
{
    @Structure
    private ObjectBuilderFactory factory;

    public Qi4jFinder(@Uses Context context,
                      @Uses Class<? extends Handler> targetClass)
    {
        super(context, targetClass);
    }

    protected Handler createTarget(Class<? extends Handler> targetClass, Request request, Response response)
    {
        ObjectBuilder<? extends Handler> builder = factory.newObjectBuilder(targetClass);
        builder.use(request);
        builder.use(response);
        builder.use(getContext().createChildContext());

        EntityStateSerializer serializer = factory.newObject(EntityStateSerializer.class);
        builder.use(serializer);
        return builder.newInstance();
    }
}
