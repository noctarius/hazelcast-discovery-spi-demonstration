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
    public static void setup() throws Exception{
        SERVER = new Server(12345);

        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages("rest");
        ServletHolder servletHolder = new ServletHolder(new ServletContainer(resourceConfig));

        ServletContextHandler contextHandler = new ServletContextHandler(SERVER, "/*");
        contextHandler.addServlet(servletHolder, "/*");

        SERVER.start();
        SERVER.join();
    }

    @AfterClass
    public static void teardown() throws Exception {
        SERVER.stop();
    }

}
