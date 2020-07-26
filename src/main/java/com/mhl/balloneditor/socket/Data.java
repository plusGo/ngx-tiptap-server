package com.mhl.balloneditor.socket;

public class Data {
    private Integer clientID;
    private Integer version;
    private String steps;

    public Data(final Integer clientID) {
        this.clientID = clientID;
    }

    public Integer getClientID() {
        return clientID;
    }

    public void setClientID(final Integer clientID) {
        this.clientID = clientID;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(final Integer version) {
        this.version = version;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(final String steps) {
        this.steps = steps;
    }
}
