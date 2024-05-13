package com.eulerity.hackathon.imagefinder.Services;

import com.eulerity.hackathon.imagefinder.Exceptions.UrlConnectionError;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.*;

/**
 * The type Web crawler.
 */
public class WebCrawler {

    private static final RegexHandler regexHandler = new RegexHandler();
    private final Map<String, Document> documentMap;

    /**
     * Instantiates a new Web crawler.
     */
    public WebCrawler() {
        this.documentMap = new HashMap<>();
    }

    private Document getDocumentFromUrl(String url) throws UrlConnectionError {

        try {
            if (documentMap.containsKey(url)) {
                return documentMap.get(url);
            } else {
                Document doc = Jsoup.connect(url).get();
                documentMap.put(url, doc);
                return doc;
            }
        } catch (IOException e) {
            throw new UrlConnectionError(e.getLocalizedMessage() + " for " + url);
        }
    }

    /**
     * Gets internal links.
     *
     * @param url the url
     * @return the internal links
     */
// TODO: return the internal links on a page and all it's subpages with a depth limitation
    public List<String> getInternalLinks(String url) throws UrlConnectionError {

        List<String> links = new ArrayList<>();
        String domain = regexHandler.getDomain(url);

        Document doc = getDocumentFromUrl(url);

        Elements elems = doc.select("a[href]");

        for (Element elem : elems) {
            String internalLink = elem.attr("abs:href");

            String tempDomain = regexHandler.getDomain(internalLink);

            if (tempDomain.equals(domain))
                links.add(internalLink);
        }

        return links;
    }

    /**
     * Gets images from page.
     *
     * @param url the url
     * @return the images from page
     */
    public List<String> getImagesFromPage(String url) throws UrlConnectionError {

        List<String> images = new ArrayList<>();

        Document doc = getDocumentFromUrl(url);
        String domain = regexHandler.getDomain(url);

        Elements elems = doc.select("img[src]");

        for (Element elem : elems) {
            String src = elem.attr("src");

            if (src.startsWith("https://"))
                images.add(src);
            else if (src.startsWith("./"))
                images.add(domain + src.substring(1));

        }

        return images;
    }

}
