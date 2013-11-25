package jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
 
public class OneContext {
 
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        ContextHandler context = new ContextHandler();
        context.setContextPath("/");
        context.setResourceBase(".");
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.setHandler(new HelloWorld());
        server.setHandler(context);
        server.start();
        server.join();
    }
}

