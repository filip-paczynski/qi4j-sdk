/*
 * Copyright (c) 2008, Michael Hunger. All Rights Reserved.
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

package org.qi4j.entitystore.legacy;

import com.ibatis.sqlmap.client.SqlMapClient;
import static com.ibatis.sqlmap.client.SqlMapClientBuilder.buildSqlMapClient;
import org.qi4j.api.entity.EntityReference;
import org.qi4j.spi.entity.EntityStoreException;
import org.qi4j.spi.entity.StateCommitter;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

public class LegacySqlClient
        implements Serializable, StateCommitter
{
    private transient SqlMapClient client;
    private final String sqlMapConfigUrl;
    private Properties configProperties;

    public LegacySqlClient(final String sqlMapConfigURL, final Properties configProperties)
    {
        this.sqlMapConfigUrl = sqlMapConfigURL;
        this.configProperties = configProperties;
    }

    public void checkActive()
    {
        if (client == null)
        {
            throw new EntityStoreException("IBatis Client for " + sqlMapConfigUrl + " was not activated.");
        }
    }

    public Map<String, Object> executeLoad(final EntityReference entityReference)
    {
        checkActive();
        final String statementId = getStatementId(entityReference, "load");
        try
        {
            //noinspection unchecked
            return (Map<String, Object>) client.queryForObject(statementId, entityReference.identity());
        }
        catch (SQLException e)
        {
            throw new EntityStoreException("Error executing Operation " + statementId + " for identity " + entityReference, e);
        }
    }

    private String getStatementId(final EntityReference entityReference, final String suffix)
    {
        // TODO How should this be?
        return suffix;
    }

    public void startTransaction()
    {
        checkActive();

        try
        {
            client.startTransaction();
        }
        catch (SQLException e)
        {
            throw new EntityStoreException("Error starting transaction", e);
        }
    }

    public int executeUpdate(final String operation, final EntityReference reference, final Object params)
    {
        checkActive();

        final String statementId = getStatementId(reference, operation);
        try
        {
            return client.update(statementId, params);
        }
        catch (SQLException e)
        {
            throw new EntityStoreException("Error executing Operation " + statementId + " for reference " + reference + " params " + params, e);
        }
    }

    public void commit()
    {
        checkActive();
        try
        {
            client.commitTransaction();
        }
        catch (SQLException e)
        {
            throw new EntityStoreException("Error commiting transaction", e);
        }
    }

    public void cancel()
    {
        checkActive();
        try
        {
            client.endTransaction();
        }
        catch (SQLException e)
        {
            throw new EntityStoreException("Error canceling transaction", e);
        }
    }

    public void passivate()
    {
        client = null;
    }

    public void activate() throws Exception
    {
        final InputStream configInputStream = new URL(sqlMapConfigUrl).openStream();
        client = buildSqlMapClient(configInputStream, configProperties);
    }

}

