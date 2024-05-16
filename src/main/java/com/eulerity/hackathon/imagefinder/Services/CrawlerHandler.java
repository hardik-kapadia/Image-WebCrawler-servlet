package com.eulerity.hackathon.imagefinder.Services;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The type Crawler handler.
 */
public class CrawlerHandler {

    /**
     * Explore list.
     *
     * @param url the url
     * @return the list
     */
    public List<String> explore(String url) throws ExecutionException, InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(9);

        Set<String> visited = Collections.synchronizedSet(new HashSet<>());

        Crawler sgp;
        synchronized (visited) {
            sgp = new Crawler(url, 0, executorService, visited);
        }
        Future<List<String>> ft = executorService.submit(sgp);

        List<String> allImages = ft.get();

        return allImages;
    }

}
