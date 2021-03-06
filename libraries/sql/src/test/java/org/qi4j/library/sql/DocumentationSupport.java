/*
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
package org.qi4j.library.sql;

import javax.sql.DataSource;

import org.qi4j.api.common.Visibility;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.bootstrap.Assembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.library.circuitbreaker.CircuitBreaker;
import static org.qi4j.library.sql.DocumentationSupport.Constants.*;
import org.qi4j.library.sql.assembly.C3P0DataSourceServiceAssembler;
import org.qi4j.library.sql.assembly.DBCPDataSourceServiceAssembler;
import org.qi4j.library.sql.assembly.DataSourceAssembler;
import static org.qi4j.library.sql.datasource.DataSources.newDataSourceCircuitBreaker;

class DocumentationSupport
{

    interface Constants
    {

        String DS_ID = "datasource";

        String OTHER_DS_ID = "datasource2";

        String DS_SERVICE_ID = "datasource-service";

    }

    class CircuitBreakerDoc
            implements Assembler
    {

        // START SNIPPET: cb-datasource
        @Service
        DataSource dataSource; // Wrapped with a CircuitBreaker proxy
        // END SNIPPET: cb-datasource

        public void assemble( ModuleAssembly module )
                throws AssemblyException
        {
            // START SNIPPET: cb-assembly
            CircuitBreaker circuitBreaker = newDataSourceCircuitBreaker( 5 /* threshold */,
                                                                         1000 * 60 * 5 /* 5min timeout */ );
            new DataSourceAssembler( DS_SERVICE_ID, DS_ID,
                                     Visibility.layer,
                                     circuitBreaker ).assemble( module );
            // END SNIPPET: cb-assembly
        }

    }

    class PoolsDoc
            implements Assembler
    {

        public void assemble( ModuleAssembly module )
                throws AssemblyException
        {
            ModuleAssembly config = module;

            // START SNIPPET: c3p0
            // Assemble the C3P0 based Service Importer
            new C3P0DataSourceServiceAssembler( DS_SERVICE_ID, Visibility.module,
                                                config, Visibility.layer ).assemble( module );
            // END SNIPPET: c3p0

            // START SNIPPET: dbcp
            // Assemble the Apache DBCP based Service Importer
            new DBCPDataSourceServiceAssembler( DS_SERVICE_ID, Visibility.module,
                                                config, Visibility.layer ).assemble( module );
            // END SNIPPET: dbcp

            // START SNIPPET: datasource
            // Assemble a DataSource
            new DataSourceAssembler( DS_SERVICE_ID, DS_ID, Visibility.module ).assemble( module );
            // Another DataSource managed by the same C3P0 connection pool
            new DataSourceAssembler( DS_SERVICE_ID, OTHER_DS_ID, Visibility.module ).assemble( module );
            // END SNIPPET: datasource

        }

    }

}
