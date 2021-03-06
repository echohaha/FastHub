package com.prettifier.pretty.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.NameParser;
import com.fastaccess.helper.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kosh on 25 Dec 2016, 9:12 PM
 */

public class GithubHelper {
    private static Pattern LINK_TAG_MATCHER = Pattern.compile("href=\"(.*?)\"");
    private static Pattern IMAGE_TAG_MATCHER = Pattern.compile("src=\"(.*?)\"");

    @NonNull public static String generateContent(@NonNull String source, @Nullable String baseUrl) {
        if (baseUrl == null) {
            return mergeContent(source);
        } else {
            return mergeContent(validateImageBaseUrl(source, baseUrl));
        }
    }

    @NonNull private static String validateImageBaseUrl(@NonNull String source, @NonNull String baseUrl) {
        NameParser nameParser = new NameParser(baseUrl);
        String owner = nameParser.getUsername();
        String repoName = nameParser.getName();
        Matcher matcher = IMAGE_TAG_MATCHER.matcher(source);
        while (matcher.find()) {
            String src = matcher.group(1).trim();
            if (src.startsWith("http://") || src.startsWith("https://")) {
                continue;
            }
            Logger.e(src);
            String finalSrc = "https://raw.githubusercontent.com/" + owner + "/" + repoName + "/master/" + src;
            source = source.replace("src=\"" + src + "\"", "src=\"" + finalSrc + "\"");
        }
        return validateLinks(source, baseUrl);
    }

    private static String validateLinks(@NonNull String source, @NonNull String baseUrl) {
        NameParser nameParser = new NameParser(baseUrl);
        String owner = nameParser.getUsername();
        String repoName = nameParser.getName();
        Matcher matcher = LINK_TAG_MATCHER.matcher(source);
        while (matcher.find()) {
            String href = matcher.group(1).trim();
            if (href.startsWith("http://") || href.startsWith("https://") || href.startsWith("mailto:")) {
                continue;
            }
            String link = "https://raw.githubusercontent.com/" + owner + "/" + repoName + "/master/" + href;
            source = source.replace("href=\"" + href + "\"", "href=\"" + link + "\"");
        }
        return source;
    }

    private static String mergeContent(@NonNull String source) {
        return "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;\"/>" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"./github.css\">\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                source +
                "\n<script src=\"./intercept-touch.js\"></script>\n" +
                "</body>\n" +
                "\n" +
                "</html>\n";
    }

}
