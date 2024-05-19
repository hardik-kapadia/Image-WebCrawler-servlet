package com.eulerity.hackathon.imagefinder;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.concurrent.*;

import com.eulerity.hackathon.imagefinder.Exceptions.InvalidUrlException;
import com.eulerity.hackathon.imagefinder.Exceptions.UrlConnectionError;
import com.eulerity.hackathon.imagefinder.Services.Crawler;
import com.eulerity.hackathon.imagefinder.Utils.Utils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class ImageFinderTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    public HttpServletRequest request;
    public HttpServletResponse response;
    public StringWriter sw;
    public HttpSession session;

    @Before
    public void setUp() throws Exception {

        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);

        sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(request.getRequestURI()).thenReturn("/foo/foo/foo");
        Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/foo/foo/foo"));
        Mockito.when(request.getParameter("url")).thenReturn("https://thecoderenroute.github.io/");
        session = Mockito.mock(HttpSession.class);
        Mockito.when(request.getSession()).thenReturn(session);
    }

//    @Test
//    public void test() throws IOException, ServletException {
//        Mockito.when(request.getServletPath()).thenReturn("/main");
//        new ImageFinder().doPost(request, response);
//        Assert.assertEquals(new Gson().toJson(ImageFinder.testImages), sw.toString());
//    }

    @Test
    public void testValidUrlDomain() {
        String url = "https://www.google.com/foo/foo/foo?q=p";

        try {
            String domain = Utils.getDomain(url);
            Assert.assertEquals("google", domain);
        } catch (InvalidUrlException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testInvalidUrlDomain() {
        String url = "http://google/foo/foo/foo?q=p";

        assertThrows(
                "Invalid URL",
                InvalidUrlException.class,
                () -> {
                    Utils.getDomain(url);
                }
        );

    }

    @Test
    public void InvalidUrlConnection() {

        String url = "https://abcdefhi.buh/";

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        UrlConnectionError uce = assertThrows(UrlConnectionError.class,
                () -> {
                    try {
                        Crawler crawler = new Crawler(url, 0, executorService, new HashSet<>());
                        Future<ConcurrentHashMap<String, CopyOnWriteArrayList<String>>> future = executorService.submit(crawler);
                        future.get();
                    } catch (ExecutionException e) {
                        throw e.getCause();
                    }
                }
        );

        assertEquals("Cannot connect to " + url, uce.getMessage());

    }


}



