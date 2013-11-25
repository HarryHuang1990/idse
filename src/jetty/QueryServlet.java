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
//				//ִ�в�ѯ�������ؽ��
//				rsList = tool.getResult(keyWords, sortBy, pageNo);
//				//��������Ƽ�
//				rels = tool.getRecommends(keyWords);
//				session.removeAttribute("rsList");
//				session.setAttribute("rsList", rsList);	//������������浽session
//				session.removeAttribute("rels");
//				session.setAttribute("rels", rels);//�������Ƽ����浽session
//				//�����ѯ��־
//
//			
//			PaginationBean beans = PaginationTool.generatePageBean(rsList, pageNo);
//			rsObj = new JSONObject();
//			rsObj.accumulate("res", beans);
//			rsObj.accumulate("rsize", rels.size());	//���������Ŀ
//			rsObj.accumulate("rels", rels);			//��������б�
//			
//			resp.setContentType("text/xml;charset=utf-8");	//ajax ֻ֧��utf-8
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
			//System.out.println("Something wrong in ArticleClassServlet��"+e.toString());
		}
		return json.toString();
	}
}
