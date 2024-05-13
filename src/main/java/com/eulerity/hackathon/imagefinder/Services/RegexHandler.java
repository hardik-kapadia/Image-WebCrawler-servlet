package com.eulerity.hackathon.imagefinder.Services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexHandler {

    private final Pattern domainPattern;

    public RegexHandler() {
        this.domainPattern = Pattern.compile("^(?:https?://)?(?:[^@\\n]+@)?(?:www\\.)?([^:/\\n?]+)");
    }

    public String getDomain(String url) {
        Matcher matcher = this.domainPattern.matcher(url);
        return matcher.group(0);
    }
}
