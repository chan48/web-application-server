package webserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;

/**
 * Created by woowahan on 2017. 4. 8..
 */
public class Request {
    private String method;
    private String path;
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
                this.method = tokens[0];
                this.path = tokens[1];
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

    public String path() {
        return path;
    }
}
