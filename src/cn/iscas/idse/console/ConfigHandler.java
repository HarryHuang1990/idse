package cn.iscas.idse.console;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import cn.iscas.idse.config.PropertiesManager;
import cn.iscas.idse.config.SystemConfiguration;

public class ConfigHandler extends AbstractHandler {
	 @Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		 response.setContentType("text/html;charset=utf-8");
		 response.setStatus(HttpServletResponse.SC_OK);
		 baseRequest.setHandled(true);
		 String op = request.getParameter("op");
		 JSONObject jsonObject = null;
		 System.out.println(op);
		 if("load".equals(op)){ // load the parameters
			 jsonObject = SystemConfiguration.getParamsInJSON();
			 response.getWriter().println(jsonObject.toString());
		 }
		 else if("save".equals(op)){ // save the parameters
			 String targetDirectory = request.getParameter("target_directory");
			 String maxSizeAllowed_PDF = request.getParameter("pdf_size");
			 String maxSizeAllowed_TXT = request.getParameter("txt_size");
			 String maxFileCountPreDirectory = request.getParameter("directory_size");	
			 String validViewPeriod = request.getParameter("duration");
			 String intervalTaskPeriod = request.getParameter("interval");
			 String taskSimilarityThreshold = request.getParameter("task_similarity");
			 String klUpbound = request.getParameter("kl");
			 String dMAX_GAMA = request.getParameter("transfer_length");	
			 String topicFactor = request.getParameter("topic_factor");	
			 String taskFactor = request.getParameter("task_factor");	
			 String locationFactor = request.getParameter("location_factor");
			 String topN = request.getParameter("result_number");
			 String step = request.getParameter("recommend_step");
			 String recommendedDocNumber = request.getParameter("recommend_size");
			 String userActivityLogFile = request.getParameter("log_file");
			 
			 targetDirectory = targetDirectory.replaceAll("//", "/").replaceAll("&amp;", "&").replaceAll("\\\\", "/");
			 targetDirectory = targetDirectory.replaceAll(",", "/,").replaceAll("//", "/");
			 if(!targetDirectory.endsWith("/"))
				 targetDirectory += "/";
			 userActivityLogFile = userActivityLogFile.replaceAll("\\\\", "/");
			 
			 PropertiesManager.updateProperties("target.directory", targetDirectory);
			 PropertiesManager.updateProperties("maxSizeAllowed_PDF", maxSizeAllowed_PDF);
			 PropertiesManager.updateProperties("maxSizeAllowed_TXT", maxSizeAllowed_TXT);
			 PropertiesManager.updateProperties("maxFileCountPreDirectory", maxFileCountPreDirectory);
			 PropertiesManager.updateProperties("validViewPeriod", validViewPeriod);
			 PropertiesManager.updateProperties("intervalTaskPeriod", intervalTaskPeriod);
			 PropertiesManager.updateProperties("taskSimilarityThreshold", taskSimilarityThreshold);
			 PropertiesManager.updateProperties("klUpbound", klUpbound);
			 PropertiesManager.updateProperties("dMAX_GAMA", dMAX_GAMA);
			 PropertiesManager.updateProperties("topicFactor", topicFactor);
			 PropertiesManager.updateProperties("taskFactor", taskFactor);
			 PropertiesManager.updateProperties("locationFactor", locationFactor);
			 PropertiesManager.updateProperties("topN", topN);
			 PropertiesManager.updateProperties("step", step);
			 PropertiesManager.updateProperties("recommendedDocNumber", recommendedDocNumber);
			 PropertiesManager.updateProperties("userActivityLogFile", userActivityLogFile);
			 
			 SystemConfiguration.updateParams(
					 targetDirectory, 
					 Short.parseShort(maxSizeAllowed_PDF), 
					 Short.parseShort(maxSizeAllowed_TXT), 
					 Integer.parseInt(maxFileCountPreDirectory), 
					 Integer.parseInt(validViewPeriod), 
					 Integer.parseInt(intervalTaskPeriod), 
					 Double.parseDouble(taskSimilarityThreshold), 
					 Double.parseDouble(klUpbound), 
					 Integer.parseInt(dMAX_GAMA), 
					 Double.parseDouble(topicFactor), 
					 Double.parseDouble(taskFactor), 
					 Double.parseDouble(locationFactor), 
					 Integer.parseInt(topN), 
					 Integer.parseInt(step), 
					 Integer.parseInt(recommendedDocNumber),
					 userActivityLogFile);
			 
			 response.getWriter().println("done");
		 }
		  	 
	 }
}
