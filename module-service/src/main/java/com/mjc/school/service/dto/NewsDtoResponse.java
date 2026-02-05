package com.mjc.school.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsDtoResponse extends RepresentationModel<NewsDtoResponse> {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
    private AuthorDtoResponse authorDto;
    private List<TagDtoResponse> tagsDto;
    private List<CommentsDtoForNewsResponse> commentsDto;

    public NewsDtoResponse() {
        tagsDto = new ArrayList<>();
        commentsDto = new ArrayList<>();
    }

    public NewsDtoResponse(Long id, String title, String content, LocalDateTime createdDate, LocalDateTime lastUpdatedDate, AuthorDtoResponse authorDto, List<TagDtoResponse> tagsDto, List<CommentsDtoForNewsResponse> commentsDto) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.lastUpdatedDate = lastUpdatedDate;
        this.authorDto = authorDto;
        this.tagsDto = tagsDto;
        this.commentsDto = commentsDto;
    }



    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public AuthorDtoResponse getAuthorDto() {
        return authorDto;
    }

    public List<TagDtoResponse> getTagsDto() {
        return tagsDto;
    }

    public List<CommentsDtoForNewsResponse> getCommentsDto() {
        return commentsDto;
    }

    public void setCommentsDto(List<CommentsDtoForNewsResponse> commentsDto) {
        this.commentsDto = commentsDto;
    }
    public void setAuthorDto(AuthorDtoResponse authorDto) {
        this.authorDto = authorDto;
    }

    public void setTagsDto(List<TagDtoResponse> tagsDto) {
        this.tagsDto = tagsDto;
    }

    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
