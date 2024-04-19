package com.example.courserascraper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/export")
public class FileController {

    private final Logger log = LoggerFactory.getLogger(CourseController.class);
    private final ExcelBuilder excelBuilder;
    private final CourseParser courseParser;

    @Autowired
    public FileController(ExcelBuilder excelBuilder, CourseParser courseParser) {
        this.excelBuilder = excelBuilder;
        this.courseParser = courseParser;
    }


    @PostMapping
    public ResponseEntity<byte[]> getExcelFile(@RequestParam("text") String text,
                                               @RequestParam(value = "language", required = false) String[] languages,
                                               @RequestParam(value = "duration", required = false) String[] durations) throws IOException {
        List<Course> courseList = courseParser.parse(text, List.of(languages), List.of(durations));
        byte[] excelBytes = excelBuilder.build(courseList);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("filename", "export.xlsx");
        log.info("Called export excel file");
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}
