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

package org.qi4j.runtime.composite;

import java.lang.reflect.Method;
import org.qi4j.composite.Composite;
import org.qi4j.composite.State;
import org.qi4j.spi.composite.CompositeInstance;

/**
 * TODO
 */
public final class MixinsModel extends AbstractMixinsModel
{
    private StateModel stateModel;

    public MixinsModel( Class<? extends Composite> compositeType, StateModel stateModel )
    {
        super( compositeType );
        this.stateModel = stateModel;
    }

    @Override public MixinModel implementMethod( Method method )
    {
        // Add state
        stateModel.addStateFor( method );

        return super.implementMethod( method );
    }

    // Context
    public void newMixins( CompositeInstance compositeInstance, UsesInstance uses, State state, Object[] mixins )
    {
        int i = 0;
        for( MixinModel mixinModel : mixinModels )
        {
            mixins[ i++ ] = mixinModel.newInstance( compositeInstance, uses, state );
        }
    }
}
