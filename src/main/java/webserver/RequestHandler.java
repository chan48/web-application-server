package webserver;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import db.DataBase;
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
                    res.send("Hello world 2");
                    break;
                }
                case "/user/create": {
                    Map<String, String> body = req.body();

                    User user = new User(body.get("userId"), body.get("password"), body.get("name"),
                            URLDecoder.decode(body.get("email")));
                    DataBase.addUser(user);

                    log.debug("user created: {}", user.toString());

                    res.status(302, "Redirect");
                    res.redirect("/index.html");
                    break;
                }
                case "/user/login": {
                    Map<String, String> body = req.body();
                    User user = DataBase.findUserById(body.get("userId"));
                    log.debug("user found: {}", user);
                    if (user == null) {
                        res.setCookie("logined=false; Path=/");
                        res.status(302, "Redirect");
                        res.redirect("/user/login_failed.html");
                    } else {
                        res.status(302, "Redirect");
                        res.setCookie("logined=true; Path=/");
                        res.redirect("/index.html");
                    }
                    break;
                }
                case "/user/list": {
                    if (req.isAuthenticated()) {
                        Collection<User> users = DataBase.findAll();
                        StringBuilder result = new StringBuilder();
                        result.append("<!DOCTYPE html>");
                        result.append("<html>");
                        result.append("<body>");
                        result.append("<table>");
                        result.append("<tr><th>userId</th><th>Name</th><th>Email</th>");
                        for (User user : users) {
                            result.append("<tr>");
                            result.append("<td>" + user.getUserId() + "</td>");
                            result.append("<td>" + user.getName() + "</td>");
                            result.append("<td>" + user.getEmail() + "</td>");
                        }
                        result.append("</table>");
                        result.append("</body>");
                        result.append("</html>");
                        result.append("</ul>");

                        res.status(200, "OK");
                        res.sendHtmlText(result.toString());
                    } else {
                        res.status(302, "Redirect");
                        res.redirect("/user/login.html");
                    }
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
