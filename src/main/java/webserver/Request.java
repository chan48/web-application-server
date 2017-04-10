package webserver;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.*;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by woowahan on 2017. 4. 8..
 */
public class Request {
    private String method;
    private String path;
    private Map<String, String> querystring;
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public Request(BufferedReader br) throws IOException {
        parse(br);
    }

    private void parse(BufferedReader br) throws IOException {
        String line = br.readLine();

        if (line == null) {
            return;
        }

        log.debug("{}", line);

        while (!"".equals(line)) {
            String [] tokens = line.split(" ");

            if (isInvalidMethod(tokens[0])) {
                this.method = method(tokens);
                this.path = path(tokens);
                this.querystring = querystring(tokens);
                break;
            }

            line = br.readLine();
        }
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

    public Map<String, String> querystring() { return querystring; }

    public Map<String, String> querystring(String [] tokens) {
        try {
            return HttpRequestUtils.parseQueryString(tokens[1].split("\\?")[1]);
        } catch (Exception e) {
            return Maps.newHashMap();
        }

    }
}
