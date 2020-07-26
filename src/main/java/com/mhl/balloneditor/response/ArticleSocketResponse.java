package com.mhl.balloneditor.response;

import java.util.Objects;

public class ArticleSocketResponse {
    private String action; // init update
    private Object data;

    public String getAction() {
        return action;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    public ArticleSocketResponse(final String action, final Object data) {
        this.action = action;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(final Object data) {
        this.data = data;
    }
}
