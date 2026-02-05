package com.mjc.school.service.impl;

import com.mjc.school.repository.exception.EntityConflictRepositoryException;
import com.mjc.school.repository.filter.pagination.Page;
import com.mjc.school.repository.impl.CommentRepository;
import com.mjc.school.repository.impl.NewsRepository;
import com.mjc.school.repository.model.Comment;
import com.mjc.school.service.BaseService;
import com.mjc.school.service.dto.CommentsDtoRequest;
import com.mjc.school.service.dto.CommentsDtoResponse;
import com.mjc.school.service.dto.PageDtoResponse;
import com.mjc.school.service.dto.ResourceSearchFilterRequestDTO;
import com.mjc.school.service.exceptions.NotFoundException;
import com.mjc.school.service.exceptions.ResourceConflictServiceException;
import com.mjc.school.service.filter.ResourceSearchFilter;
import com.mjc.school.service.filter.mapper.BaseSearchFilterMapper;
import com.mjc.school.service.mapper.CommentMapper;
import com.mjc.school.service.validator.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mjc.school.service.exceptions.ServiceErrorCode.*;

@Service
public class CommentService
    implements BaseService<CommentsDtoRequest, CommentsDtoResponse, Long, ResourceSearchFilterRequestDTO, CommentsDtoRequest> {

    private final CommentRepository commentRepository;
    private final NewsRepository newsRepository;
    private final CommentMapper mapper;
    private final BaseSearchFilterMapper commentsSearchFilterMapper;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                          NewsRepository newsRepository,
                          CommentMapper mapper,
                          BaseSearchFilterMapper commentsSearchFilterMapper) {
        this.commentRepository = commentRepository;
        this.newsRepository = newsRepository;
        this.mapper = mapper;
        this.commentsSearchFilterMapper = commentsSearchFilterMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageDtoResponse<CommentsDtoResponse> readAll(@Valid ResourceSearchFilterRequestDTO searchFilterRequest) {
        final ResourceSearchFilter searchFilter = commentsSearchFilterMapper.map(searchFilterRequest);
        final Page page = commentRepository.readAll(getEntitySearchSpecification(searchFilter));
        final List<CommentsDtoResponse> modelDtoList = mapper.modelListToDtoList(page.entities());
        return new PageDtoResponse<>(modelDtoList, page.currentPage(), page.pageCount());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentsDtoResponse readById(Long id) {
        return commentRepository.readById(id)
                .map(mapper::modelToDto)
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format(
                                        COMMENT_ID_DOES_NOT_EXIST.getMessage(),
                                        id
                                )
                        )
                );
    }

    @Override
    @Transactional
    public CommentsDtoResponse create(@Valid CommentsDtoRequest createRequest) {
        if (!newsRepository.existById(createRequest.newsId())) {
            throw new NotFoundException(String.format(NEWS_ID_DOES_NOT_EXIST.getMessage(), createRequest.newsId()));
        }
        try {
            Comment model = mapper.dtoToModel(createRequest);
            model = commentRepository.create(model);
            return mapper.modelToDto(model);
        } catch (EntityConflictRepositoryException exc) {
            throw new ResourceConflictServiceException(COMMENT_CONFLICT.getMessage(), COMMENT_CONFLICT.getErrorCode(), exc.getMessage());
        }
    }

    @Override
    @Transactional
    public CommentsDtoResponse update(Long id, @Valid CommentsDtoRequest updateRequest) {
        if (commentRepository.existById(id)) {
            Comment model = mapper.dtoToModel(updateRequest);
            model.setId(id);
            model = commentRepository.update(model);
            return mapper.modelToDto(model);
        } else {
            throw new NotFoundException(String.format(COMMENT_ID_DOES_NOT_EXIST.getMessage(), id));
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (commentRepository.existById(id)) {
            commentRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format(COMMENT_ID_DOES_NOT_EXIST.getMessage(), id));
        }
    }

    @Transactional(readOnly = true)
    public List<CommentsDtoResponse> readByNewsId(Long newsId) {
        return mapper.modelListToDtoList(commentRepository.readByNewsId(newsId));
    }
}
