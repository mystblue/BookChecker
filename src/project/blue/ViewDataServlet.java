package project.blue;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

import project.blue.data.PMF;
import project.blue.data.URLData;

@SuppressWarnings("serial")
public class ViewDataServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		URLData data = new URLData("http://www.google.co.jp", "Google", "2012-09-25");
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(data);
        } finally {
            pm.close();
        }
        
        resp.setContentType("text/plain");
		resp.getWriter().println("Get data.");
	}
}
