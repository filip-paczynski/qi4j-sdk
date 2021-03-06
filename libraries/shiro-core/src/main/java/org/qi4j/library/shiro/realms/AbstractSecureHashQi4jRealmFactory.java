/*
 * Copyright (c) 2010, Paul Merlin. All Rights Reserved.
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
package org.qi4j.library.shiro.realms;

import org.apache.shiro.realm.Realm;
import org.qi4j.library.shiro.authc.SecureHashCredentialsMatcher;

import java.util.Arrays;
import java.util.Collection;

public abstract class AbstractSecureHashQi4jRealmFactory
        extends AbstractQi4jRealmFactory
{

    public final Collection<Realm> getRealms()
    {
        AbstractSecureHashQi4jRealm realm = getSecureHashRealm();
        realm.setCredentialsMatcher( new SecureHashCredentialsMatcher() );
        return Arrays.asList( new Realm[]{ realm } );
    }

    protected abstract AbstractSecureHashQi4jRealm getSecureHashRealm();

}
