package com.conferencelocator.utils;

import java.net.URI;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class TitleCleaner {

    public static String clean(String title, String url) {
        String titleFromUrl = getTitleFromUrl(url);
        String[] arr = { "\\|", "::", "•", "«", "»" ,"<" ,"--"};
        for (String str : arr) {
            String[] parts = title.split(str);
            if (ArrayUtils.isEmpty(parts)) {
                continue;
            }
            if (parts.length != 2) {
                continue;
            }

            String firstPart = parts[0];
            String secondPart = parts[1];
            firstPart = firstPart.trim();
            secondPart = secondPart.trim();

            if (StringUtils.equalsIgnoreCase(firstPart, titleFromUrl)) {
                return firstPart;
            } else if (StringUtils.equalsIgnoreCase(secondPart, titleFromUrl)) {
                return secondPart;
            }
            if(secondPart.length() > firstPart.length()){
                return secondPart;
            }
            return firstPart;
        }
        return title;
    }

    private static String getTitleFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            String[] parts = StringUtils.split(path, "/");
            String titlePart = parts[parts.length - 1];
            return StringUtils.replace(titlePart, "-", " ").trim();
        } catch (Exception e) {
            // ignore
        }
        return null;
    }
}
