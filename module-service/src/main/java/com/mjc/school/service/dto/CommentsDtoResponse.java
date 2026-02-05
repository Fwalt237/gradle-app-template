package com.mjc.school.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentsDtoResponse extends RepresentationModel<CommentsDtoResponse> {

    private Long id;
    private String content;
    private Long newsId;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;

    public CommentsDtoResponse() {
    }

    public CommentsDtoResponse(Long id, String content, Long newsId, LocalDateTime createdDate, LocalDateTime lastUpdatedDate) {
        this.id = id;
        this.content = content;
        this.newsId = newsId;
        this.createdDate = createdDate;
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Long getNewsId() {
        return newsId;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
}

