/*
 * Copyright (c) 2011, Rickard Öberg. All Rights Reserved.
 * Copyright (c) 2012, Paul Merlin. All Rights Reserved.
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
package org.qi4j.library.sql.assembly;

import org.qi4j.api.common.Visibility;
import org.qi4j.bootstrap.Assembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.library.sql.liquibase.LiquibaseConfiguration;
import org.qi4j.library.sql.liquibase.LiquibaseService;

public class LiquibaseAssembler
        implements Assembler
{

    private final Visibility visibility;

    private ModuleAssembly config;

    private Visibility configVisibility;

    public LiquibaseAssembler( Visibility visibility )
    {
        this.visibility = visibility;
    }

    public LiquibaseAssembler withConfigIn( ModuleAssembly config, Visibility configVisibility )
    {
        this.config = config;
        this.configVisibility = configVisibility;
        return this;
    }

    public void assemble( ModuleAssembly module )
            throws AssemblyException
    {
        module.services( LiquibaseService.class ).visibleIn( visibility ).instantiateOnStartup();
        if ( config != null ) {
            config.entities( LiquibaseConfiguration.class ).visibleIn( configVisibility );
        }
    }

}
