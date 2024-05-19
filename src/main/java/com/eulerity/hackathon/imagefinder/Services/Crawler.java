package com.eulerity.hackathon.imagefinder.Services;

import com.eulerity.hackathon.imagefinder.Exceptions.ExplorationMaxedOut;
import com.eulerity.hackathon.imagefinder.Exceptions.InvalidUrlException;
import com.eulerity.hackathon.imagefinder.Exceptions.UrlConnectionError;

import com.eulerity.hackathon.imagefinder.Utils.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import java.security.KeyStore;
import java.util.*;
import java.util.concurrent.*;

public class Crawler implements Callable<ConcurrentHashMap<String, CopyOnWriteArrayList<String>>> {

    private static final int MAX_DEPTH = 2;

    private final String url;
    private final int depth;
    private final ExecutorService executorService;
    private final Set<String> visited;

    public Crawler(String url, int depth, ExecutorService executorService, Set<String> visited) {

        System.out.println("Created new Crawler with url: " + url + " and depth: " + depth + " and visited worth: " + visited.size());
        this.visited = Collections.synchronizedSet(visited);
        visited.add(url);
        this.url = url;

        this.depth = depth;

        this.executorService = executorService;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public ConcurrentHashMap<String, CopyOnWriteArrayList<String>> call() throws Exception {

        synchronized (visited) {
            if (visited.size() >= 50)
                throw new ExplorationMaxedOut();

        }

        Document document;

        try {
            document = Jsoup.connect(this.url).followRedirects(true).get();
        } catch (IOException e) {
            throw new UrlConnectionError("Cannot connect to " + this.url);
        }

        String domain = Utils.getDomain(url);
        String fullDomain = Utils.getFullDomain(url);
        Map<String, Future<ConcurrentHashMap<String, CopyOnWriteArrayList<String>>>> futures = new HashMap<>();


        if (depth < MAX_DEPTH && this.visited.size() < 20) {
            Elements elems = document.select("a[href]");

            for (Element elem : elems) {
                String internalLink = elem.attr("abs:href");

                if (internalLink.isEmpty() || internalLink.replace(" ", "").isEmpty())
                    continue;

                String tempDomain;
                String tempSubDomain;

                try {
                    tempDomain = Utils.getDomain(internalLink);
                    tempSubDomain = Utils.getSubDomain(internalLink);
                } catch (InvalidUrlException e) {
                    System.out.println("Invalid URL: " + internalLink);
                    continue;
                }
                if (tempSubDomain.startsWith("#")) {
                    internalLink = tempDomain;
                }

//                System.out.println("For " + internalLink + ", domain: " + tempDomain + " and subdomain: " + tempSubDomain);

                if (tempDomain.equals(domain)) {
                    synchronized (visited) {
                        if (!visited.contains(internalLink)) {
                            visited.add(internalLink);

//                            System.out.println("Processing " + internalLink);

                            Crawler sgp;

                            sgp = new Crawler(internalLink, depth + 1, executorService, visited);

                            Future<ConcurrentHashMap<String, CopyOnWriteArrayList<String>>> ft = executorService.submit(sgp);

                            futures.put(internalLink, ft);
                        }
                    }
                }
            }
        }

//        CopyOnWriteArrayList<String> images = new CopyOnWriteArrayList<>();

        ConcurrentHashMap<String, CopyOnWriteArrayList<String>> images = new ConcurrentHashMap<>();
        ConcurrentHashMap.KeySetView<String, Boolean> addedImages = ConcurrentHashMap.newKeySet();
        images.put(url, new CopyOnWriteArrayList<>());

        Elements imgElems = document.select("img[src]");

        for (Element elem : imgElems) {

            String src = elem.attr("src");

            if (src.startsWith("https://")) {
                if (!addedImages.contains(src)) {
                    images.get(url).addIfAbsent(src);
                    addedImages.add(src);
                }
            } else if (src.startsWith("./")) {
                String tImage = fullDomain + src.substring(1);
                if (!addedImages.contains(tImage)) {
                    images.get(url).addIfAbsent(tImage);
                    addedImages.add(tImage);
                }
            }

        }

        Set<String> keys = futures.keySet();

        for (String iUrl : keys) {
            try {

                ConcurrentHashMap<String, CopyOnWriteArrayList<String>> newImages = futures.get(iUrl).get();

                Set<String> ks = newImages.keySet();

                for (String ky : ks) {

                    images.put(ky, new CopyOnWriteArrayList<>());
                    CopyOnWriteArrayList<String> ni = newImages.get(ky);

                    for (String tni : ni) {
                        if (!addedImages.contains(tni)) {
                            addedImages.add(tni);
                            images.get(ky).add(tni);
                        }
                    }

                    if (images.get(ky).isEmpty()) {
                        images.remove(ky);
                    }
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return images;
    }

}
