package com.example.courserascraper;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@Service
public class CourseParser {

    private static final Logger log = LoggerFactory.getLogger(CourseParser.class);

    private static final String BASE_URL = "https://www.coursera.org/search";

    private static final String TEXT_PARAMETER = "query";
    private static final String LANG_PARAMETER = "language";
    private static final String DURATION_PARAMETER = "productDuration";

    public List<Course> parse(String text, List<String> languages, List<String> durations) throws IOException {
        String url = buildURL(text, languages, durations);
        log.info("URL for search: {}", url);
        return getCourses(url);
    }

    private String buildURL(String text,
                            List<String> languages,
                            List<String> durations) {
        StringBuilder urlString = new StringBuilder(BASE_URL + "?");
        urlString.append(TEXT_PARAMETER).append("=").append(URLEncoder.encode(text, StandardCharsets.UTF_8));
        if (languages != null && !languages.isEmpty()) {
            for (String language : languages) {
                urlString.append("&").append(LANG_PARAMETER).append("=").append(URLEncoder.encode(language, StandardCharsets.UTF_8));
            }
        }
        if (durations != null && !durations.isEmpty()) {
            for (String duration : durations) {
                urlString.append("&").append(DURATION_PARAMETER).append("=").append(URLEncoder.encode(duration, StandardCharsets.UTF_8));
            }
        }
        urlString.append("&sortBy=BEST_MATCH");
        return urlString.toString();
    }

    private List<Course> getCourses(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements variants = doc.select("ul.cds-9 li");
        List<Course> courses = new ArrayList<>();
        for (Element variant : variants) {
            String name = variant.selectFirst("h3.cds-CommonCard-title").text();
            String author = variant.selectFirst("p.cds-ProductCard-partnerNames").text();
            String cover = variant.selectFirst("div.cds-CommonCard-previewImage img").attr("src");
            String additional = variant.selectFirst("div.cds-CommonCard-metadata").text();
            Element ratingElement = variant.selectFirst("div.product-reviews");
            double score = Double.parseDouble(ratingElement.text().split(" ")[0]);

            Course course = Course.builder()
                    .name(name)
                    .author(author)
                    .cover(cover)
                    .score(score)
                    .additional(additional)
                    .build();
            courses.add(course);
        }

        return courses;
    }
}
