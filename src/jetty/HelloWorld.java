package jetty;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
 
public class HelloWorld extends AbstractHandler {
 
	String content = "";
	public HelloWorld(String content){
		this.content = content;
	}
	
	public HelloWorld(){}
	
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	String params = request.getParameter("keyWords");
    	System.out.println(params);
    	this.content = params;
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println(this.content);
    }
 
    public static void main(String[] args) throws Exception {
//        Server server = new Server(8080);
//        server.setHandler(new HelloWorld());
//        server.start();
//        server.join();
    	  File file = new File("  ");
    	  java.awt.Desktop.getDesktop().open(new File("D:/My DBank/竞赛/大数据技术创新创业大赛/"));
    	  Runtime.getRuntime().exec("cmd /c start explorer " + "\"D:/My DBank/竞赛/大数据技术创新创业大赛/\"");
    }
}
