# Multithreaded web crawler using Java

[![Github](https://img.icons8.com/ios-glyphs/30/github.png)](https://github.com/thecoderenroute/Image-WebCrawler-servlet)

## ImageFinder Goal
The goal of this task is to perform a web crawl on a URL string provided by the user. From the crawl, you will need to parse out all of the images on that web page and return a JSON array of strings that represent the URLs of all images on the page. [Jsoup](https://jsoup.org/) is a great basic library for crawling and is already included as a maven dependency in this project, however you are welcome to use whatever library you would like.

### Functionality

- Web crawler fetches images from the supplied url
- Identifies the subpages and domain of the url and crawls through them too
- Displays the images crawled from all subpages separately

## Features

- Uses multi-threading to optimize the process
- Does not go beyond the domain specified
- Accommodates for the fact that some subpages may have different root domains and subdomains
- Does the crawl the same url twice
- Checks the urls to ensure they're valid
- Accommodates for re-directs to a different section of the same page.

## Tools used

- Java 8
- Java servlets
- Jetty webserver
- JSoup
- HTML, CSS, and Javascript

## Instructions to test

<code> mvn clean test </code>

## Instructions to run

<code> mvn clean package jetty:run</code>

## Screenshots

![img.png](img.png)

