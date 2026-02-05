package com.mjc.school.service.impl;

import com.mjc.school.repository.exception.EntityConflictRepositoryException;
import com.mjc.school.repository.filter.pagination.Page;
import com.mjc.school.repository.impl.AuthorRepository;
import com.mjc.school.repository.model.Author;
import com.mjc.school.service.BaseService;
import com.mjc.school.service.dto.AuthorDtoRequest;
import com.mjc.school.service.dto.AuthorDtoResponse;
import com.mjc.school.service.dto.PageDtoResponse;
import com.mjc.school.service.dto.ResourceSearchFilterRequestDTO;
import com.mjc.school.service.exceptions.NotFoundException;
import com.mjc.school.service.exceptions.ResourceConflictServiceException;
import com.mjc.school.service.filter.ResourceSearchFilter;
import com.mjc.school.service.filter.mapper.AuthorSearchFilterMapper;
import com.mjc.school.service.mapper.AuthorMapper;
import com.mjc.school.service.validator.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mjc.school.service.exceptions.ServiceErrorCode.*;

@Service
public class AuthorService
    implements BaseService<AuthorDtoRequest, AuthorDtoResponse, Long, ResourceSearchFilterRequestDTO, AuthorDtoRequest> {

    private final AuthorRepository authorRepository;
    private final AuthorMapper mapper;
    private final AuthorSearchFilterMapper authorSearchFilterMapper;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, AuthorMapper mapper, AuthorSearchFilterMapper authorSearchFilterMapper) {
        this.authorRepository = authorRepository;
        this.mapper = mapper;
        this.authorSearchFilterMapper = authorSearchFilterMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageDtoResponse<AuthorDtoResponse> readAll(@Valid ResourceSearchFilterRequestDTO searchFilterRequest) {
        final ResourceSearchFilter searchFilter = authorSearchFilterMapper.map(searchFilterRequest);
        final Page page = authorRepository.readAll(getEntitySearchSpecification(searchFilter));
        final List<AuthorDtoResponse> modelDtoList = mapper.modelListToDtoList(page.entities());
        return new PageDtoResponse<>(modelDtoList, page.currentPage(), page.pageCount());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorDtoResponse readById(Long id) {
        return authorRepository.readById(id)
            .map(mapper::modelToDto)
            .orElseThrow(
                () -> new NotFoundException(
                    String.format(
                        AUTHOR_ID_DOES_NOT_EXIST.getMessage(),
                        id
                    )
                )
            );
    }

    @Override
    @Transactional
    public AuthorDtoResponse create(@Valid AuthorDtoRequest createRequest) {
        try {
            Author model = mapper.dtoToModel(createRequest);
            model = authorRepository.create(model);
            return mapper.modelToDto(model);
        } catch (EntityConflictRepositoryException exc) {
            throw new ResourceConflictServiceException(AUTHOR_CONFLICT.getMessage(), AUTHOR_CONFLICT.getErrorCode(), exc.getMessage());
        }
    }

    @Override
    @Transactional
    public AuthorDtoResponse update(Long id, @Valid AuthorDtoRequest updateRequest) {
        if (authorRepository.existById(id)) {
            Author model = mapper.dtoToModel(updateRequest);
            model.setId(id);
            model = authorRepository.update(model);
            return mapper.modelToDto(model);
        } else {
            throw new NotFoundException(String.format(AUTHOR_ID_DOES_NOT_EXIST.getMessage(), id));
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (authorRepository.existById(id)) {
            authorRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format(AUTHOR_ID_DOES_NOT_EXIST.getMessage(), id));
        }
    }

    public AuthorDtoResponse readByNewsId(Long newsId) {
        return authorRepository.readByNewsId(newsId)
            .map(mapper::modelToDto)
            .orElseThrow(
                () ->
                    new NotFoundException(String.format(AUTHOR_DOES_NOT_EXIST_FOR_NEWS_ID.getMessage(), newsId))
            );
    }
}
