package org.ndnm.diffbot.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class UrlMatcher {
    private static final Logger LOG = LogManager.getLogger(UrlMatcher.class);
    private static final Pattern URL_PATTERN = Pattern.compile("((https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])");


    public static List<String> extractUrls(String text) {
        List<String> urls = new ArrayList<>();

        if (text == null) {
            LOG.warn("Null comment passed in, skipping.");
            return urls;
        }

        Matcher matcher = URL_PATTERN.matcher(text);
        while (matcher.find()) {
            String result = matcher.group(1);
            if (StringUtils.isNotBlank(result)) {
                urls.add(result);
            }
        }

        return urls;
    }
}
