package com.example.courserascraper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/")
public class CourseController {

    private final Logger log = LoggerFactory.getLogger(CourseController.class);
    private final CourseParser courseParser;

    @Autowired
    public CourseController(CourseParser courseParser) {
        this.courseParser = courseParser;
    }

    @GetMapping
    public String getMainPage(){
        log.info("Called get main page");
        return "index";
    }

    @PostMapping
    public String search(@RequestParam("text") String text,
                         @RequestParam(value = "language", required = false) String[] languages,
                         @RequestParam(value = "duration", required = false) String[] durations,
                         Model model) throws IOException {
        log.info("Search: text={} languages={} durations={}", text, List.of(languages), List.of(durations));
        model.addAttribute("courses", courseParser.parse(text, List.of(languages), List.of(durations)));
        model.addAttribute("durations", List.of(durations));
        model.addAttribute("languages", List.of(languages));
        model.addAttribute("text", text);
        return "index";
    }
}
