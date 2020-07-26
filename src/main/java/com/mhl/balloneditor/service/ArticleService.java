package com.mhl.balloneditor.service;

import com.mhl.balloneditor.model.Article;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

    public Article getArticle(final String draftId) {
        return new Article("", "", 0);
    }

}
