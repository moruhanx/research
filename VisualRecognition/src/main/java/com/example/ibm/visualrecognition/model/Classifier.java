package com.example.ibm.visualrecognition.model;

import lombok.Data;

import java.util.List;

/**
 * @author John
 * @create 2019/7/16 10:47
 */
@Data
public class Classifier {

    private String name;
    private String classifierId;
    private List<String> classes;
}
