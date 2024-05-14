package com.eulerity.hackathon.imagefinder.Exceptions;

public class UrlAlreadyVisitedException extends IllegalAccessException {

    public UrlAlreadyVisitedException() {
        super("Url already visited");
    }

    public UrlAlreadyVisitedException(String message) {
        super(message);
    }

}
