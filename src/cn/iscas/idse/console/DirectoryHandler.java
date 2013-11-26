package cn.iscas.idse.console;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
/**
 * this handler is responsible for opening the specific directory.
 * @author Harry Huang
 *
 */
public class DirectoryHandler extends AbstractHandler {
	 @Override
	 public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		 String directory = request.getParameter("directory");
		 System.out.println(directory);
		 directory = directory.replaceAll("//", "/").replaceAll("&amp;", "&");
		 File dir = new File(directory);
		 String message = "true";
		 if(!dir.exists())
			 message = "false";
		 else
			 java.awt.Desktop.getDesktop().open(dir);
		 
		 response.setContentType("text/html;charset=utf-8");
		 response.setStatus(HttpServletResponse.SC_OK);
		 baseRequest.setHandled(true);
		 response.getWriter().println(message);
	 }
}
