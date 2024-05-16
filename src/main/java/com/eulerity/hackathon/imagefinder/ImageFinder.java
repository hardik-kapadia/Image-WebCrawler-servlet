package com.eulerity.hackathon.imagefinder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eulerity.hackathon.imagefinder.Exceptions.InvalidUrlException;
import com.eulerity.hackathon.imagefinder.Services.CrawlerHandler;
import com.eulerity.hackathon.imagefinder.Utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet(
        name = "ImageFinder",
        urlPatterns = {"/main"}
)
public class ImageFinder extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected static final Gson GSON = new GsonBuilder().create();

    //This is just a test array
    public static final String[] testImages = {
            "https://images.pexels.com/photos/545063/pexels-photo-545063.jpeg?auto=compress&format=tiny",
            "https://images.pexels.com/photos/464664/pexels-photo-464664.jpeg?auto=compress&format=tiny",
            "https://images.pexels.com/photos/406014/pexels-photo-406014.jpeg?auto=compress&format=tiny",
            "https://images.pexels.com/photos/1108099/pexels-photo-1108099.jpeg?auto=compress&format=tiny"
    };

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        CrawlerHandler crawlerHandler = new CrawlerHandler();

        resp.setContentType("text/json");
        String path = req.getServletPath();
        String url = req.getParameter("url");
//        resp.getWriter().print(GSON.toJson(testImages));

//         Validating the URL
//        try {
//            Utils.isValidUrl(url);
//        } catch (InvalidUrlException e) {
//            resp.setStatus(400);
//            Map<String, String> error = new HashMap<>();
//            error.put("Error", "Invalid URL " + e.getLocalizedMessage());
//            resp.getWriter().write(GSON.toJson(error));
//        }

        System.out.println("Got request of:" + path + " with query param:" + url);

        try {
            List<String> images = crawlerHandler.explore(url);
            System.out.println("Explored: " + images);
            resp.getWriter().print(GSON.toJson(images));
        } catch (ExecutionException | InterruptedException e) {
            resp.setStatus(500);
            Map<String, String> error = new HashMap<>();
            error.put("Error", "Faced server error " + e.getLocalizedMessage());
            e.printStackTrace();
            resp.getWriter().write(GSON.toJson(error));
        }
    }
}
