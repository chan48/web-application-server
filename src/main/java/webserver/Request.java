package webserver;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import javax.print.DocFlavor;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by woowahan on 2017. 4. 8..
 */
public class Request {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private String method;
    private String path;
    private Map<String, String> querystring;
    private Map<String, String> body;
    private int contentLength;
    private Map<String, String> cookies;

    public Request(BufferedReader br) throws IOException {
        parse(br);
    }

    private void parse(BufferedReader br) throws IOException {
        String line = br.readLine();

        if (line == null) {
            return;
        }

        while (!"".equals(line) ) {
            if (line == null) {
                return;
            }

            log.debug("Request Raw: {}", line);

            if (line.contains("Content-Length")) {
                contentLength = contentLength(line);
            }
            if (line.contains("Cookie")) {
                cookies = HttpRequestUtils.parseCookies(line);
            }

            String [] tokens = line.split(" ");

            if (isInvalidMethod(tokens[0])) {
                this.method = method(tokens);
                this.path = path(tokens);
                this.querystring = querystring(tokens);
            }

            line = br.readLine();
        }

        body = HttpRequestUtils.parseQueryString(IOUtils.readData(br, contentLength));
    }

    private boolean isInvalidMethod(String token) {
        String [] availableMethods = new String[]{"GET", "POST", "PATCH", "UPDATE", "DELETE"};
        return Arrays.asList(availableMethods).contains(token);
    }

    public String method() {
        return method;
    }

    public String method(String[] tokens) {
        return tokens[0];
    }

    public String path() {
        return path;
    }

    public String path(String [] tokens) {
        return tokens[1].split("\\?")[0];
    }

    public Map<String, String> querystring() {
        return querystring;
    }

    public Map<String, String> querystring(String [] tokens) {
        try {
            return HttpRequestUtils.parseQueryString(tokens[1].split("\\?")[1]);
        } catch (Exception e) {
            return Maps.newHashMap();
        }
    }

    public Map<String, String> body() {
        return body;
    }

    public int contentLength() {
        return contentLength;
    }

    public int contentLength(String line) {
        return Integer.parseInt(line.split(" ")[1]);
    }

    public boolean isAuthenticated() {
        if (cookies == null) return false;
        String logined = cookies.get("logined");
        if (logined == null) return false;
        return logined.equals("true");
    }

    public Map<String, String> cookie(String line) {
        String cookieValue = line.split(" ")[1];
        String[] tokens = cookieValue.split("=");
        Map<String, String> map = new HashMap<>();
        map.put(tokens[0], tokens[1]);
        return map;
    }
}
