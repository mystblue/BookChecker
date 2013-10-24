package project.blue.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class HttpUtil {
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
