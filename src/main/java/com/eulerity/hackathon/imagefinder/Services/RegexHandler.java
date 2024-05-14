package com.eulerity.hackathon.imagefinder.Services;

import com.eulerity.hackathon.imagefinder.Exceptions.InvalidUrlException;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexHandler {

    private final Pattern domainPattern;

    public RegexHandler() {
        this.domainPattern = Pattern.compile("^(?:https?://)?(?:[^@\\n]+@)?(?:www\\.)?([^:/\\n?]+)");
    }

    public String getDomain(String url) throws InvalidUrlException {
        Matcher matcher = this.domainPattern.matcher(url);

        if (matcher.matches())
            return matcher.group(0);

        String[] splits = url.split("/");

        System.out.println("For " + url + ", Splits :" + Arrays.toString(splits));

        if (splits.length >= 4) {
            return splits[0] + "/" + splits[1] + "/" + splits[2];
        } else if (splits.length == 3) {
            return splits[0] + "/" + splits[1] + "/" + splits[2];
        }

        throw new InvalidUrlException("Invalid URL: " + url + " - no domain found");

    }

    public String getSubDomain(String url) throws InvalidUrlException {

        String[] splits = url.split("/");

        if (splits.length <= 3) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 3; i < splits.length; i++) {
            sb.append(splits[i]).append("/");
        }

        return sb.substring(0, sb.length() - 1);

    }
}
