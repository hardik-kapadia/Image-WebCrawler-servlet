package com.eulerity.hackathon.imagefinder.Services;


import com.eulerity.hackathon.imagefinder.Exceptions.UrlConnectionError;

import java.util.*;
import java.util.regex.Matcher;

/**
 * The type Web crawler handler.
 */
public class WebCrawlerHandler {

    private static final int MAX_DEPTH = 2;

    private static final WebCrawler webCrawler = new WebCrawler();
    private static final RegexHandler regexHandler = new RegexHandler();


    private final Set<String> visitedUrls;
    private final Set<String> failedUrls;
    private final Set<String> emptyUrls;
    private final Set<String> srcImages;
    private final Set<String> domainImages;
    private final Map<String, List<String>> subDomainImages;

    public WebCrawlerHandler() {

        this.visitedUrls = new HashSet<>();
        this.failedUrls = new HashSet<>();
        this.emptyUrls = new HashSet<>();
        this.srcImages = new HashSet<>();
        this.domainImages = new HashSet<>();
        this.subDomainImages = new HashMap<>();
    }

    /**
     * Explore list.
     *
     * @param url the url
     * @return the list
     */
    public void explore(String url) throws UrlConnectionError {

        // TODO: Implement a method which identifies the domain, combs through the domain for internal links,
        //  and then returns the images from not only the passed url, but from all possible subdomains

        String domain = regexHandler.getDomain(url);

        Queue<String> q = new LinkedList<>();

        // Process the specified url
        try {

            this.visitedUrls.add(url);
            List<String> images = webCrawler.getImagesFromPage(url);
            List<String> links = webCrawler.getInternalLinks(url);

            if (images.isEmpty())
                this.emptyUrls.add(url);
            else
                this.srcImages.addAll(images);

            q.addAll(links);

        } catch (UrlConnectionError e) {
            throw new UrlConnectionError("Cannot connect to source URL");
        }

        // Process the domain
        try {
            this.visitedUrls.add(domain);
            List<String> images = webCrawler.getImagesFromPage(url);
            List<String> links = webCrawler.getInternalLinks(url);

            if (images.isEmpty())
                this.emptyUrls.add(url);
            else
                this.domainImages.addAll(images);

            q.addAll(links);
        } catch (UrlConnectionError e) {
            this.failedUrls.add(domain);
        }

        int depth = 1;

        // Process the remaining internal links
        while (!q.isEmpty() && depth < MAX_DEPTH) {

            int size = q.size();

            for (int i = 0; i < size; i++) {

                String nextUrl = q.poll();

                if (visitedUrls.contains(nextUrl))
                    continue;

                this.visitedUrls.add(nextUrl);

                try {
                    List<String> images = webCrawler.getImagesFromPage(nextUrl);
                    List<String> links = webCrawler.getInternalLinks(nextUrl);

                    q.addAll(links);

                    this.subDomainImages.put(nextUrl, images);

                } catch (UrlConnectionError e) {
                    this.failedUrls.add(nextUrl);
                }

            }

            depth++;

        }


    }

    public Set<String> getEmptyUrls() {
        return emptyUrls;
    }

    public Set<String> getDomainImages() {
        return domainImages;
    }

    public Set<String> getFailedUrls() {
        return failedUrls;
    }

    public Set<String> getSrcImages() {
        return srcImages;
    }

    public Set<String> getVisitedUrls() {
        return visitedUrls;
    }

    public Map<String, List<String>> getSubDomainImages() {
        return subDomainImages;
    }

}
