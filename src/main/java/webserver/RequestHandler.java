package webserver;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Map;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            Request req = new Request(new BufferedReader(new InputStreamReader(in, "utf8")));
            Response res = new Response(out);

            String path = req.path();
            log.debug("PATH: {}", req.path());

            switch (req.path()) {
                case "/": {
                    res.status(200);
                    res.send("Hello world");
                    break;
                }
                case "/user/create": {
                    res.status(201);
                    res.send("Created");
                    Map<String, String> qs = req.querystring();

                    User user = new User(qs.get("userId"), qs.get("password"), qs.get("name"),
                            URLDecoder.decode(qs.get("email")));
                    log.debug(user.toString());
                    break;
                }
                default: {
                    res.status(200);
                    res.sendFile(path);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
