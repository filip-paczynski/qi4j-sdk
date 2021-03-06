/*  Copyright 2008 Edward Yakop.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.library.http;

import javax.servlet.Servlet;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;

// START SNIPPET: servletservice
@Mixins( HelloWorldServlet.class )
public interface HelloWorldServletService extends Servlet, ServiceComposite
{
}
// END SNIPPET: servletservice
