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
            log.debug("METHOD: {}, PATH: {}, QUERYSTRING: {}, BODY: {}",
                    req.method(), req.path(), req.querystring().toString(), req.body().toString());

            switch (req.path()) {
                case "/": {
                    res.status(200, "OK");
                    res.send("Hello world");
                    break;
                }
                case "/user/create": {
                    Map<String, String> body = req.body();

                    User user = new User(body.get("userId"), body.get("password"), body.get("name"),
                            URLDecoder.decode(body.get("email")));

                    log.debug("user created: {}", user.toString());

                    res.status(302, "Redirect");
                    res.redirect("/index.html");
                    break;
                }
                default: {
                    res.status(200, "OK");
                    res.sendFile(path);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
