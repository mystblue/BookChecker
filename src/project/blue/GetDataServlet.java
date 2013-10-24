package project.blue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.*;

import project.blue.data.PMF;
import project.blue.data.URLData;

@SuppressWarnings("serial")
public class GetDataServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		//http://maohtorrent.blog101.fc2.com/blog-category-25.html
		// 36
		// 17-24
		String[] list = {"http://jpddl.com/manga/",
				"http://jpddl.com/manga/page/2/",
				"http://jpddl.com/manga/page/3/",
				"http://jpddl.com/manga/page/4/",
				"http://jpddl.com/manga/page/5/",
				"http://jpddl.com/manga/page/6/",
				"http://jpddl.com/manga/page/7/"};
		
		StringBuffer error = new StringBuffer();
		for (String url : list) {
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

        resp.setContentType("text/html;charset=UTF-8");
		resp.getWriter().println("Get data.");
		resp.getWriter().println(error.toString());
	}

	public boolean get(String url) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        String str = download(url);
        if (str == null) {
        	return false;
        }
        
        // http://jpddl.com/manga/page/2/
        // <h1><a href=""></a>
        // regex "<h1><a href=\"([^\"]+)\">([^<]+)</a>"
        
        // ↓
        
        // <a class="postTitle2" href="/manga/216332-raw-manga-yoshikawa-miki-yamada-kun-and-seven-witches-04-05-volume-no-cover.html">(一般コミック) [吉河美希] 山田くんと7人の魔女 第04-05巻(表紙無し)</a>
        // regex "<a class=\"postTitle2\" href=\"([^\"]+)\">([^<]+)</a>"
        
        Pattern pattern = Pattern.compile("<a class=\"postTitle2\" href=\"([^\"]+)\">([^<]+)</a>");
        Matcher matcher = pattern.matcher(str);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 9);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(today);
        while (matcher.find()) {
        	if (!contains(pm, "http://jpddl.com/" + matcher.group(1))) {
        		URLData data = new URLData("http://jpddl.com/" + matcher.group(1), matcher.group(2), date);
        		pm.makePersistent(data);
        	}
        }
        pm.close();
        return true;
	}

	public static boolean contains(PersistenceManager pm, String url) {
		Query query = pm.newQuery(URLData.class);
	    query.setFilter("url == urlParam");
	    //query.setOrdering("hireDate desc");
	    query.declareParameters("String urlParam");

	    try {
	        @SuppressWarnings("unchecked")
			List<URLData> results = (List<URLData>) query.execute(url);
	        if (results.isEmpty()) {
	        	return false;
	        } else {
	            return true;
	        }
	    } finally {
	        query.closeAll();
	    }
	}

	static public String download(String uri) {
		try {
		    URL url = new URL(uri);
		    URLConnection conn = url.openConnection();
		    InputStream is = conn.getInputStream();
		    
		    List<String> encodings = conn.getHeaderFields().get("Content-Encoding");
		    if (encodings != null) {
		        for (String item : encodings) {
		            if (item.equals("gzip")) {
		                is = new GZIPInputStream(is);
		            }
		        }
		    }
		    
		    BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		    StringBuffer buffer = new StringBuffer();
		    String str = null;
		    while ((str = br.readLine()) != null) {
		        buffer.append(str);
		    }
		    br.close();
		    return buffer.toString();
		} catch (Exception ex) {
			
		}
		return null;
	}
}
