package com.demo.searcher.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoveHtmlTags {

    public static String removeHtmlTag(String keyword) {
        return keyword.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }

}
