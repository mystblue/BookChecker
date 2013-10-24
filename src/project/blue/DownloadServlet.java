package project.blue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class DownloadServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {

		String param = req.getParameter("url");
		try {
			URL url = new URL(param);
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
			//res.setContentType("image/jpeg");
			ServletOutputStream out = res.getOutputStream();
			
			int contents;
			while ((contents = is.read()) != -1) {
				out.write(contents);
			}
			is.close();
			out.close();
		} catch (Exception ex) {
			
		}
	}
}
