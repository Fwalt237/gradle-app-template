package com.mjc.school.service.mapper;

import com.mjc.school.repository.BaseRepository;
import com.mjc.school.repository.impl.AuthorRepository;
import com.mjc.school.repository.impl.TagRepository;
import com.mjc.school.repository.model.Author;
import com.mjc.school.repository.model.Comment;
import com.mjc.school.repository.model.News;
import com.mjc.school.service.dto.CommentsDtoForNewsResponse;
import com.mjc.school.service.dto.CreateNewsDtoRequest;
import com.mjc.school.service.dto.NewsDtoResponse;
import com.mjc.school.service.dto.UpdateNewsDtoRequest;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.stream.Collectors;

@Primary
@Mapper(componentModel = "spring", uses = {AuthorMapper.class, TagMapper.class, CommentMapper.class},
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class NewsMapper {

    @Autowired
    protected AuthorRepository authorRepository;
    @Autowired
    protected TagRepository tagRepository;
    @Autowired
    protected BaseRepository<Comment, Long> commentsRepository;
    @Autowired
    protected CommentMapper commentMapper;

    public abstract List<NewsDtoResponse> modelListToDtoList(List<News> modelList);

    @Mapping(source = "author", target = "authorDto")
    @Mapping(source = "tags", target = "tagsDto")
    @Mapping(target = "commentsDto", ignore = true)
    public abstract NewsDtoResponse modelToDto(News model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastUpdatedDate", ignore = true)
    @Mapping(target = "author", expression =
        "java(authorRepository.readByName(dto.author()).get())")
    @Mapping(target = "tags", expression =
        "java(dto.tags().stream().map(name -> tagRepository.readByName(name).get()).toList())")
    @Mapping(target = "comments", expression =
            "java(dto.commentsIds().stream().map(commentId -> commentsRepository.getReference(commentId)).toList())")
    public abstract News dtoToModel(CreateNewsDtoRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastUpdatedDate", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "tags", ignore=true)
    @Mapping(target = "comments", expression =
        "java(dto.commentsIds().stream().map(commentId -> commentsRepository.getReference(commentId)).toList())")
    public abstract News dtoToModel(UpdateNewsDtoRequest dto);

    @AfterMapping
    void setAuthor(UpdateNewsDtoRequest updateRequest, @MappingTarget News news) {
        if (updateRequest.author() != null && !updateRequest.author().isBlank()) {
            Author author = authorRepository.readByName(updateRequest.author()).get();
            news.setAuthor(author);
        }
    }
    @AfterMapping
    void setComments(News model, @MappingTarget NewsDtoResponse dto) {
        if (model.getComments() != null) {
            List<CommentsDtoForNewsResponse> commentsDto = model.getComments().stream()
                .map(c -> commentMapper.modelToDtoForNews(c))
                .collect(Collectors.toList());
            dto.setCommentsDto(commentsDto);
        }
    }
}
