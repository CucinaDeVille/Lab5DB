package edu.hsog.db;

import javax.swing.*;

public class DTO {
    //DataTransferObject

    private String url;
    private String email_verkaeufer;
    private String keywords;
    private String description;
    private Icon cover;
    private double averageRating;
    private String comments;

    public DTO (String url, String email_verkaeufer, String keywords, String description, Icon cover, double averageRating, String comments) {
        this.url = url;
        this.email_verkaeufer = email_verkaeufer;
        this.keywords= keywords;
        this.description = description;
        this.cover = cover;
        this.averageRating = averageRating;
        this.comments = comments;
    }

    //Getter und Setter
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail_verkaeufer() {
        return email_verkaeufer;
    }
    public void setEmail_verkaeufer(String email_verkaeufer) {
        this.email_verkaeufer = email_verkaeufer;
    }

    public String getKeywords() {
        return keywords;
    }
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Icon getCover() {
        return cover;
    }
    public void setCover(Icon cover) {
        this.cover = cover;
    }

    public double getAverageRating() {
        return averageRating;
    }
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
}
