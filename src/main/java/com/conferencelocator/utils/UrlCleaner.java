package com.conferencelocator.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

public abstract class UrlCleaner {

    public static List<String> clean(List<String> urls) {
        List<String> cleanedUrls = new ArrayList<String>();
        for (String url : urls) {
            String cleanedUrl = clean(url);
            if (!StringUtils.endsWith(cleanedUrl, "/")) {
                cleanedUrl += "/";
            }
            cleanedUrls.add(cleanedUrl);
        }
        return cleanedUrls;
    }

    public static String clean(String url) {
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            // ignore
            return url;
        }
        String query = uri.getQuery();
        if (StringUtils.isBlank(query)) {
            return url;
        }
        String[] queryParams = StringUtils.split(query, "&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : queryParams) {
            String[] parts = param.split("=");
            if(!ArrayUtils.isEmpty(parts)){
                if(parts.length == 2){
                    map.put(parts[0], parts[1]);
                }else{
                    map.put(parts[0], null);
                }
            }
        }
        if (map.containsKey("utm_source")) {
            map.remove("utm_source");
        }
        if (map.containsKey("utm_medium")) {
            map.remove("utm_medium");
        }
        if (map.containsKey("utm_campaign")) {
            map.remove("utm_campaign");
        }
        if (map.containsKey("cm_ven")) {
            map.remove("cm_ven");
        }
        StringBuilder urlBuilder = new StringBuilder(getUrlPrefix(uri, url)).append(uri.getHost()).append(uri.getPath());
        if (!CollectionUtils.isEmpty(map)) {
            StringBuilder builder = new StringBuilder();
            builder.append("?");
            for (Entry<String, String> entry : map.entrySet()) {
                builder.append(entry.getKey()).append("=").append(entry.getValue());
                builder.append("&");
            }
            int lastIndexOf = builder.lastIndexOf("&");
            String queryString = builder.substring(0, lastIndexOf);
            urlBuilder.append(queryString);
        }
        return urlBuilder.toString();
    }

    private static String getUrlPrefix(URI uri, String url) {
        StringBuilder urlPrefix = new StringBuilder(uri.getScheme()).append("://");
        // if(StringUtils.contains(url, "//www.")){
        // urlPrefix.append("www.");
        // }
        return urlPrefix.toString();
    }
}
