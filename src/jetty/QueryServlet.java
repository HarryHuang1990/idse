package jetty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class QueryServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
//		HttpSession session = req.getSession();
//		JSONObject rsObj = null;
//		ArrayList<Result> rsList = null;
//		ArrayList<RecommendBean> rels = null;
//		
//			String keyWords = req.getParameter("keyWords");
//			keyWords = new String(keyWords.getBytes("ISO8859-1"), "utf-8");  //text
//			
//			System.out.println(keyWords);
//			
//
//				//执行查询，并返回结果
//				rsList = tool.getResult(keyWords, sortBy, pageNo);
//				//获得搜素推荐
//				rels = tool.getRecommends(keyWords);
//				session.removeAttribute("rsList");
//				session.setAttribute("rsList", rsList);	//将搜索结果保存到session
//				session.removeAttribute("rels");
//				session.setAttribute("rels", rels);//将搜索推荐保存到session
//				//保存查询日志
//
//			
//			PaginationBean beans = PaginationTool.generatePageBean(rsList, pageNo);
//			rsObj = new JSONObject();
//			rsObj.accumulate("res", beans);
//			rsObj.accumulate("rsize", rels.size());	//相关搜索数目
//			rsObj.accumulate("rels", rels);			//相关搜索列表
//			
//			resp.setContentType("text/xml;charset=utf-8");	//ajax 只支持utf-8
//			PrintWriter out = resp.getWriter();
//			out.println(rsObj.toString());
//			out.flush();
//			out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private String readJSONString(HttpServletRequest request) {
		StringBuffer json = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while((line = reader.readLine()) != null) {
				json.append(line);
				}
		}catch(Exception e) {
			//System.out.println("Something wrong in ArticleClassServlet："+e.toString());
		}
		return json.toString();
	}
}
