package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Objects;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf8"));
            String line = br.readLine();

            if (line == null) {
                return;
            }

            log.debug("{}", line);

            String method = "";
            String path = "";

            while (!"".equals(line)) {
                String [] tokens = line.split(" ");

                if (Objects.equals(tokens[0], "GET")) {
                    method = tokens[0];
                    path = tokens[1];
                    break;
                }

                line = br.readLine();
            }

            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            DataOutputStream dos = new DataOutputStream(out);

            byte[] body;
            String type = "plain";

            if (path.equals("/")) {
                body = "Hello World".getBytes();
            } else {
                body = Files.readAllBytes(new File("./webapp" + path).toPath());

                String[] exts = path.split("\\.");
                String ext = exts[exts.length -1];
                log.debug("exts: {}", ext);

                type = ext;
                if (ext == "js") {
                    type = "javascript";
                }
            }

            response200Header(dos, body.length, type);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String type) {
        String contentType = "Content-Type: text/" + type + ";charset=utf-8\r\n";
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes(contentType);
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
