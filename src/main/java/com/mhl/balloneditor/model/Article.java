package com.mhl.balloneditor.model;

public class Article {
    private String content;
    private String selection;
    private Integer version;

    public Article(final String content, final String selection, final Integer version) {
        this.content = content;
        this.selection = selection;
        this.version = version;
    }
}
