package com.eulerity.hackathon.imagefinder.Exceptions;

public class UrlResponseException extends Exception {

    public UrlResponseException(String message) {
        super(message);
    }

    public UrlResponseException() {
        super("URL did not respond with a 200");
    }
}
