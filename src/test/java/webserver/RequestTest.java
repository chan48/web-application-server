package webserver;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

/**
 * Created by woowahan on 2017. 4. 8..
 */
public class RequestTest {
    private Request req;
    private String method;
    private String path;

    @Before
    public void setUp() throws IOException {
        method = "GET";
        path = "/index.html";
        String str = method + " " + path + "\n";
        BufferedReader br = new BufferedReader(new StringReader(str));
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
}
