/*
 * Copyright (c) 2009, Rickard Öberg. All Rights Reserved.
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

package org.qi4j.runtime.value;

import org.qi4j.api.composite.Composite;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.value.Value;
import org.qi4j.api.value.ValueBuilder;
import org.qi4j.api.value.ValueComposite;
import org.qi4j.spi.Qi4jSPI;
import org.qi4j.spi.value.ValueType;

/**
 * Implementation of Value
 */

public class ValueMixin
    implements Value
{
    @This Value thisValue;

    @This Qi4jSPI spi;

    public <T> ValueBuilder<T> buildWith()
    {
        ValueInstance valueInstance = ValueInstance.getValueInstance( (ValueComposite) thisValue );
        Class<Composite> valueType = (Class<Composite>) valueInstance.type();
        return (ValueBuilder<T>) valueInstance.module().valueBuilderFactory().newValueBuilder( valueType ).withPrototype( (Composite) thisValue );
    }

    public String toJSON()
    {
        ValueInstance valueInstance = ValueInstance.getValueInstance( (ValueComposite) thisValue );

        ValueType valueType = ( (ValueModel) valueInstance.compositeModel() ).valueType();
        StringBuilder json = new StringBuilder();
        valueType.toJSON( thisValue, json, spi );
        return json.toString();
    }
}
