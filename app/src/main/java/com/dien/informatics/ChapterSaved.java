package com.dien.informatics;

public class ChapterSaved {
    private String key = "";
    private int index = 0;
    private String lesson = "";
    private int lessonIndex = 0;

    public ChapterSaved(String key, int index, String lesson, int lessonIndex) {
        this.key = key;
        this.index = index;
        this.lesson = lesson;
        this.lessonIndex = lessonIndex;
    }

    public ChapterSaved() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public int getLessonIndex() {
        return lessonIndex;
    }

    public void setLessonIndex(int lessonIndex) {
        this.lessonIndex = lessonIndex;
    }
}
