package project.blue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import project.blue.data.LinkData;
import project.blue.data.PMF;
import project.blue.util.HttpUtil;

@SuppressWarnings("serial")
public class GetMangameServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		// http://mangame26.blog136.fc2.com/
		// http://mangame26.blog136.fc2.com/page-1.html
		
		StringBuffer error = new StringBuffer();
		process("http://mangame26.blog136.fc2.com/", error);
		for (int i = 1; i <= 10; i++) {
			process("http://mangame26.blog136.fc2.com/page-" + i + ".html", error);
		}

        resp.setContentType("text/html;charset=UTF-8");
		resp.getWriter().println("Get data.");
		resp.getWriter().println(error.toString());
	}
	
	private void process(String url, StringBuffer error) {
		boolean ret = get(url);
		if (!ret) {
			ret = get(url);
			if (!ret) {
				ret = get(url);
				if (!ret)
					error.append(url + "<br>");
			}
		}
	}

	public boolean get(String url) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        String str = HttpUtil.download(url);
        if (str == null) {
        	return false;
        }
        
        Pattern pattern = Pattern.compile("<div class=\"entry\">(.+?)</rdf:RDF>");
        Pattern pattern2 = Pattern.compile("<a href=\"(http://mangame26.blog136.fc2.com/[^\"]+)\"[^>]*>([^<]+)</a></h2>");
        Pattern pattern3 = Pattern.compile("<img .*?src=\"([^\"]+amazon[^\"]+)\"");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
        	String block = matcher.group(1);
        	Matcher matcher2 = pattern2.matcher(block);
        	if (matcher2.find()) {
        		String aurl = matcher2.group(1);
        		String title = matcher2.group(2);
        		if (!contains(pm, aurl)) {
        			LinkData data = new LinkData(aurl, title, getTodayString(), "mangame26");
                	Matcher matcher3 = pattern3.matcher(block);
                	if (matcher3.find()) {
                		data.setImagePath(matcher3.group(1));
                	}
            		pm.makePersistent(data);
        		}
        	}
        }
        pm.close();
        return true;
	}

	private String getTodayString() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 9);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(today);
        return date;
	}

	public static boolean contains(PersistenceManager pm, String url) {
		Query query = pm.newQuery(LinkData.class);
	    query.setFilter("url == urlParam");
	    query.declareParameters("String urlParam");

	    try {
	        @SuppressWarnings("unchecked")
			List<LinkData> results = (List<LinkData>) query.execute(url);
	        if (results.isEmpty()) {
	        	return false;
	        } else {
	            return true;
	        }
	    } finally {
	        query.closeAll();
	    }
	}
}
