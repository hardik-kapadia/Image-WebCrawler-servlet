package com.eulerity.hackathon.imagefinder.Services;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;


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
    public ConcurrentHashMap<String, CopyOnWriteArrayList<String>> explore(String url) throws ExecutionException, InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(9);

        Set<String> visited = Collections.synchronizedSet(new HashSet<>());

        url = url.trim();
        Crawler sgp;
        synchronized (visited) {
            sgp = new Crawler(url, 0, executorService, visited);
        }
        Future<ConcurrentHashMap<String, CopyOnWriteArrayList<String>>> ft = executorService.submit(sgp);

        ConcurrentHashMap<String, CopyOnWriteArrayList<String>> allImages = ft.get();

        System.out.println("Finished with " + url);
        return allImages;
    }

}
