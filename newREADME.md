# Multithreaded web crawler using Java

[![Github](https://img.icons8.com/ios-glyphs/30/github.png)](https://github.com/thecoderenroute/Image-WebCrawler-servlet)

## ImageFinder Goal

The goal of this task is to perform a web crawl on a URL string provided by the user. From the crawl, you will need to
parse out all of the images on that web page and return a JSON array of strings that represent the URLs of all images on
the page. [Jsoup](https://jsoup.org/) is a great basic library for crawling and is already included as a maven
dependency in this project, however you are welcome to use whatever library you would like.

### Functionality

- Web crawler fetches images from the supplied url
- Identifies the subpages and domain of the url and crawls through them too
- Displays the images crawled from all subpages separately

## Features

- Uses multi-threading to optimize the process.
- Synchronized datastructures to ensure application is threadsafe.
- Does not go beyond the domain specified
- Accommodates for the fact that some subpages may have different root domains and subdomains
- Does the crawl the same url twice
- Checks the urls to ensure they're valid
- Accommodates for re-directs to a different section of the same page.
- Appropriate error handling with dedication custom exceptions to handle special cases.

## Tools used

- Java 8
- Java servlets
- Jetty webserver
- JSoup
- HTML, CSS, and Javascript

## Methodology

The core methodology behind this project was recursive job creation to ensure the fastest possible result
while crawling through all possible subpages.

First, a single thread job is created for the source url. The flow of a job is as follows:

- Verify the authenticity of the url
- If authentic, establish a connection and scrape all urls from the webpage
- For each url, check if it belongs to the same domain along with a few other minor checks.
- If the url passes all checks, create a recursive concurrent job for that url and start its execution.
- Once all jobs for the subpages are created, crawl through the url for it's images and add it to an overall set of
  images(to avoid duplication) and add it to a map with the source url as the key.
- Then, the crawler waits for the all sub-jobs it has created to finish and adds the images returned to the map.
- return the map.

This is optimal as it strikes a balance between limiting the number of unnecessary calls and utilizing the full extent
of the hardware available.

## Structure of project

```

├───src
│   ├───main
│   │   ├───java.com.eulerity.hackathon
│   │   │   └───imagefinder
│   │   │       ├───Exceptions: contains all the custom exception defined fro the project
│   │   │       ├───Services
│   │   │       │   ├───CrawlerHandler.java: starts the first job and initializes the necessary variables
│   │   │       │   └───Crawler.java: contains the logic for the recursive jobs
│   │   │       └───Utils
│   │   │            └───Utils.java: conatains regex and String manipulation logic to fetch a url's domain, subdomain and full domain
│   │   └───webapp
│   │       ├───images
│   │       ├───styles.css: CSS for the landing page
│   │       └───index.html: The landing page which also consists of JS to make api calls and process the input
│   └───test
│       └───java.com.eulerity.hackathon
│           └───imagefinder
|               └───ImageFinderTest - contains all the tests
└───target
    
```

## Instructions to test

<code> mvn clean test </code>

## Instructions to run

<code> mvn clean package jetty:run</code>


## Screenshots

![img.png](img.png)

