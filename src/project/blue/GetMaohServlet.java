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
public class GetMaohServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		//http://maohtorrent.blog101.fc2.com/blog-category-25.html
		// 36
		// 17-24
		// h3 h3 div <!--/article-->
		//String[] list = {"http://maohtorrent.blog101.fc2.com/blog-category-25.html"};
		
		StringBuffer error = new StringBuffer();
		process("http://maohtorrent.blog101.fc2.com/blog-category-25.html", "ÉâÉmÉx", error);
		process("http://maohtorrent.blog101.fc2.com/blog-category-36.html", "ÉRÉ~ÉbÉN", error);
		for (int i = 17; i <= 24; i++ ) {
			process("http://maohtorrent.blog101.fc2.com/blog-category-" + i + ".html", "ñüâÊ", error);
		}

        resp.setContentType("text/html;charset=UTF-8");
		resp.getWriter().println("Get data.");
		resp.getWriter().println(error.toString());
	}
	
	private void process(String url, String category, StringBuffer error) {
		boolean ret = get(url, category);
		if (!ret) {
			ret = get(url, category);
			if (!ret) {
				ret = get(url, category);
				if (!ret)
					error.append(url + "<br>");
			}
		}
	}

	public boolean get(String url, String category) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        String str = HttpUtil.download(url);
        if (str == null) {
        	return false;
        }
        
        Pattern pattern = Pattern.compile("<h3><a[^>]*>([^<]+)</a></h3><div class=\"article\">(.+?)<!--/article-->");
        Pattern pattern2 = Pattern.compile("<a href=\"(http://maohtorrent.blog[^\"]+)\"[^>]*>ÅyDownloadÅz</a>");
        Pattern pattern3 = Pattern.compile("<img .*?src=\"([^\"]+amazon[^\"]+)\"");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
        	//System.out.println(matcher.group(1));
        	String contents = matcher.group(2);
        	Matcher matcher2 = pattern2.matcher(contents);
        	if (matcher2.find()) {
        		String aurl = matcher2.group(1);
        		if (!contains(pm, aurl)) {
            		LinkData data = new LinkData(aurl, matcher.group(1), getTodayString(), "maoh");
            		data.setCategory(category);
                	Matcher matcher3 = pattern3.matcher(contents);
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
