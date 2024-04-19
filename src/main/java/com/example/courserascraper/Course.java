package com.example.courserascraper;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Course {

    private String name;
    private String author;
    private String cover;
    private double score;
    private String additional;
}
