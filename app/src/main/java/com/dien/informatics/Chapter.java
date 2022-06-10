package com.dien.informatics;

import java.util.List;

public class Chapter {
    private String name;
    private List<String> Lessons;
    private List<String> Documents;

    public Chapter() {
    }

    public Chapter(String name, List<String> lessons, List<String> documents) {
        this.name = name;
        Lessons = lessons;
        Documents = documents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLessons() {
        return Lessons;
    }

    public void setLessons(List<String> lessons) {
        Lessons = lessons;
    }

    public List<String> getDocuments() {
        return Documents;
    }

    public void setDocuments(List<String> documents) {
        Documents = documents;
    }
}
