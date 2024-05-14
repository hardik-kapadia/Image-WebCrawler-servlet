package com.eulerity.hackathon.imagefinder.Utils;

import com.eulerity.hackathon.imagefinder.Exceptions.InvalidUrlException;
//import org.apache.commons.validator.routines.UrlValidator;

public class Utils {

//    private static final UrlValidator urlValidator = new UrlValidator();

    public static void isValidUrl(String url) throws InvalidUrlException {

        if (url.startsWith("http://"))
            throw new InvalidUrlException("Please provide a secure URL");

        if (!url.startsWith("https://"))
            throw new InvalidUrlException("URL needs to start with https://");

    }
}
