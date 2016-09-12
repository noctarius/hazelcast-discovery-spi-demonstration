/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractDiscoveryTest {

    private static Server SERVER;

    @BeforeClass
    public static void setup()
            throws Exception {

        SERVER = new Server(12345);

        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages("rest");
        ServletHolder servletHolder = new ServletHolder(new ServletContainer(resourceConfig));

        ServletContextHandler contextHandler = new ServletContextHandler(SERVER, "/*");
        contextHandler.addServlet(servletHolder, "/*");

        SERVER.start();
    }

    @AfterClass
    public static void teardown()
            throws Exception {

        SERVER.stop();
    }

}
