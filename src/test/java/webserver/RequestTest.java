package webserver;

import org.junit.Before;
import org.junit.Test;
import util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by woowahan on 2017. 4. 8..
 */
public class RequestTest {
    private Request req;
    private String method;
    private String path;
    private String url;
    private String querystring;
    private int contentLength = 63;

    @Before
    public void setUp() throws IOException {
        method = "GET";
        path = "/index.html";
        querystring = "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net";
        url = method + " " + path + "?" + querystring + " HTTP/1.1\n" +
                "Content-Length: " + contentLength + "\n";
        BufferedReader br = new BufferedReader(new StringReader(url));
        req = new Request(br);
    }

    @Test
    public void testRequest_문자열을_파싱한뒤_method를_추출한다() {
        String actual = req.method();
        String expected = method;
        assertEquals(actual, expected);
    }

    @Test
    public void testRequest_문자열을_파싱한뒤_path를_추출한다(){
        String actual = req.path();
        String expected = path;
        assertEquals(actual, expected);
    }

    @Test
    public void testMethod_메소드_문자열을_반환한다() {
        String actual = req.method(url.split(" "));
        assertEquals(actual, method);
    }

    @Test
    public void testPath_경로_문자열을_반환한다() {
        String actual = req.path(url.split(" "));
        assertEquals(actual, path);
    }

    @Test
    public void testQuerystring_쿼리문자열을_반환한다() {
        Map<String, String> expected = HttpRequestUtils.parseQueryString(querystring);
        Map<String, String> actual = req.querystring(url.split(" "));
        assertEquals(expected, actual);
    }

    @Test
    public void testContentLength_ContentLength를_반환한다(){
        int expected = contentLength;
        int actual = req.contentLength();
        assertEquals(expected, actual);
    }
}
