package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

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



            switch (req.path()) {
                case "/": {
                    res.status(200);
                    res.send("Hello world");
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
