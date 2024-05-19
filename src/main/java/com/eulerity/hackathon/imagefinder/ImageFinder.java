package com.eulerity.hackathon.imagefinder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eulerity.hackathon.imagefinder.Services.CrawlerHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet(
        name = "ImageFinder",
        urlPatterns = {"/main"}
)
public class ImageFinder extends HttpServlet {

    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final long serialVersionUID = 1L;

    protected static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    //This is just a test array
//    public static final String[] testImages = {
//            "https://images.pexels.com/photos/545063/pexels-photo-545063.jpeg?auto=compress&format=tiny",
//            "https://images.pexels.com/photos/464664/pexels-photo-464664.jpeg?auto=compress&format=tiny",
//            "https://images.pexels.com/photos/406014/pexels-photo-406014.jpeg?auto=compress&format=tiny",
//            "https://images.pexels.com/photos/1108099/pexels-photo-1108099.jpeg?auto=compress&format=tiny"
//    };

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        CrawlerHandler crawlerHandler = new CrawlerHandler();

        resp.setContentType("text/json");

        String url = req.getParameter("url");

        try {
            ConcurrentHashMap<String, CopyOnWriteArrayList<String>> images = crawlerHandler.explore(url);
            resp.getWriter().print(GSON.toJson(images));
        } catch (ExecutionException | InterruptedException e) {
            resp.setStatus(500);
            Map<String, String> error = new HashMap<>();
            error.put("Error", "Faced server error " + e.getMessage());
            logger.finer(e.getMessage());
            resp.getWriter().write(GSON.toJson(error));
        }
    }
}
