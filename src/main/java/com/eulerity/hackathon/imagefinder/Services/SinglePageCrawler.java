package com.eulerity.hackathon.imagefinder.Services;

import com.eulerity.hackathon.imagefinder.Exceptions.InvalidUrlException;
import com.eulerity.hackathon.imagefinder.Exceptions.UrlConnectionError;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class SinglePageCrawler implements Callable<List<String>> {

    private static final int MAX_DEPTH = 2;
    private static final RegexHandler regexHandler = new RegexHandler();

    private final String url;
    private final int depth;
    private final ExecutorService executorService;
    private final Set<String> visited;

    public SinglePageCrawler(String url, int depth, ExecutorService executorService, Set<String> visited) {

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
    public List<String> call() throws Exception {

        Document document;

        try {
            document = Jsoup.connect(this.url).followRedirects(true).get();
        } catch (IOException e) {
            throw new UrlConnectionError("Cannot connect to " + this.url);
        }

        String domain = regexHandler.getDomain(url);
        List<Future<List<String>>> futures = new ArrayList<>();

        if (depth < MAX_DEPTH && this.visited.size() < 20) {
            Elements elems = document.select("a[href]");

            for (Element elem : elems) {
                String internalLink = elem.attr("abs:href");

                if (internalLink.isEmpty() || internalLink.replace(" ", "").isEmpty())
                    continue;

                String tempDomain;
                String tempSubDomain;

                try {
                    tempDomain = regexHandler.getDomain(internalLink);
                    tempSubDomain = regexHandler.getSubDomain(internalLink);
                } catch (InvalidUrlException e) {
                    System.out.println("Invalid URL: " + internalLink);
                    continue;
                }
                if (tempSubDomain.startsWith("#")) {
                    internalLink = tempDomain;
                }

                System.out.println("For " + internalLink + ", domain: " + tempDomain + " and subdomain: " + tempSubDomain);

                if (tempDomain.equals(domain) && !tempSubDomain.startsWith("#")) {
                    synchronized (visited) {
                        if (!visited.contains(internalLink)) {
                            visited.add(internalLink);

                            System.out.println("Processing " + internalLink);

                            SinglePageCrawler sgp;

                            sgp = new SinglePageCrawler(internalLink, depth + 1, executorService, visited);

                            Future<List<String>> ft = executorService.submit(sgp);

                            futures.add(ft);
                        }
                    }
                }
            }
        }

        CopyOnWriteArrayList<String> images = new CopyOnWriteArrayList<>();

        Elements imgElems = document.select("img[src]");

        for (Element elem : imgElems) {

            String src = elem.attr("src");

            if (src.startsWith("https://"))
                images.add(src);
            else if (src.startsWith("./"))
                images.add(domain + src.substring(1));

        }

        for (Future<List<String>> future : futures)
            images.addAllAbsent(future.get());

        return images;
    }

}
