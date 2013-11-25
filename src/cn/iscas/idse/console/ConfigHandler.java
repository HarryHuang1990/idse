package cn.iscas.idse.console;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class ConfigHandler extends AbstractHandler {
	 @Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		 String params = request.getParameter("keyWords");
		 System.out.println(params);
		 response.setContentType("text/html;charset=utf-8");
		 response.setStatus(HttpServletResponse.SC_OK);
		 baseRequest.setHandled(true);
		 response.getWriter().println("");
	 }
}
