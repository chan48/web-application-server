package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * Created by woowahan on 2017. 4. 8..
 */
public class Response {
    private DataOutputStream dos;
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public Response(OutputStream out) {
        dos = new DataOutputStream(out);
    }

    public void status(int statusCode) throws IOException {
        writeBytes("HTTP/1.1 " + statusCode + " OK");
    }

    public void send(String text) throws IOException {
        writeBytes("Content-Type: text/plain;charset=utf-8");
        writeBytes("Content-Length: " + text.getBytes().length);
        writeBytes("");
        write(text);
        end();
    }

    public void sendFile(String path) throws IOException {
        byte[] fileBytes = Files.readAllBytes(new File("./webapp" + path).toPath());
        writeBytes("Content-Type: " + contentType(path) + ";charset=utf-8");
        writeBytes("Content-Length: " + fileBytes.length);
        writeBytes("");
        write(fileBytes);
        end();
    }

    private void write(String text) throws IOException {
        write(text.getBytes());
    }

    private void write(byte[] bytes) throws IOException {
        dos.write(bytes, 0, bytes.length);
    }

    private void writeBytes(String text) throws IOException {
        this.dos.writeBytes(text + "\r\n");
    }

    private void end() throws IOException {
        this.dos.flush();
    }

    private String contentType(String path) {
        String type = ext(path);
        if (type == "js") {
            type = "javascript";
        }
        log.debug("## text/" + type);
        return "text/" + type;
    }

    private String ext(String path) {
        String[] exts = path.split("\\.");
        return exts[exts.length - 1];
    }
}
