/*
 * Copyright (c) 2012, Niclas Hedhman. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *     You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.qi4j.demo.tenminute;

import org.qi4j.api.concern.ConcernOf;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.elsewhere.inventory.InventoryService;

// START SNIPPET: allClass
public class InventoryConcern extends ConcernOf<Order>
    implements Order
{
    @Service
    private InventoryService inventory;

    public void addLineItem( LineItem item )
    {
        String productCode = item.productCode().get();
        int quantity = item.quantity().get();
        inventory.remove( productCode, quantity );
        next.addLineItem( item );
    }

    public void removeLineItem( LineItem item )
    {
        String productCode = item.productCode().get();
        int quantity = item.quantity().get();
        inventory.add( productCode, quantity );
        next.removeLineItem( item );
    }
}
// END SNIPPET: allClass
