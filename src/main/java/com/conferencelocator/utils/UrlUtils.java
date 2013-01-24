package com.conferencelocator.utils;

public class UrlUtils {

    public static String convertUrlToAnchorTagWithTargetBlank(String displayURL,String name) {
        return "<a href=\"" + displayURL + "\" target=\"_blank\">" + name + "</a>";
    }
    
    public static String convertUrlToAnchorTag(String displayURL,String name) {
        return "<a href=\"" + displayURL + "\">" + name + "</a>";
    }
}
