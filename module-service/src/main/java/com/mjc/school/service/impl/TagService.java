package com.mjc.school.service.impl;

import com.mjc.school.repository.exception.EntityConflictRepositoryException;
import com.mjc.school.repository.filter.pagination.Page;
import com.mjc.school.repository.impl.TagRepository;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.service.BaseService;
import com.mjc.school.service.dto.PageDtoResponse;
import com.mjc.school.service.dto.ResourceSearchFilterRequestDTO;
import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
import com.mjc.school.service.exceptions.NotFoundException;
import com.mjc.school.service.exceptions.ResourceConflictServiceException;
import com.mjc.school.service.filter.ResourceSearchFilter;
import com.mjc.school.service.filter.mapper.BaseSearchFilterMapper;
import com.mjc.school.service.mapper.TagMapper;
import com.mjc.school.service.validator.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mjc.school.service.exceptions.ServiceErrorCode.TAG_CONFLICT;
import static com.mjc.school.service.exceptions.ServiceErrorCode.TAG_ID_DOES_NOT_EXIST;

@Service
public class TagService implements
    BaseService<TagDtoRequest, TagDtoResponse, Long, ResourceSearchFilterRequestDTO, TagDtoRequest> {

    private final TagRepository tagRepository;
    private final TagMapper mapper;
    private final BaseSearchFilterMapper tagSearchFilterMapper;

    @Autowired
    public TagService(TagRepository tagRepository, TagMapper mapper, BaseSearchFilterMapper tagSearchFilterMapper) {
        this.tagRepository = tagRepository;
        this.mapper = mapper;
        this.tagSearchFilterMapper = tagSearchFilterMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageDtoResponse<TagDtoResponse> readAll(@Valid ResourceSearchFilterRequestDTO searchFilterRequest) {
        final ResourceSearchFilter searchFilter = tagSearchFilterMapper.map(searchFilterRequest);
        final Page page = tagRepository.readAll(getEntitySearchSpecification(searchFilter));
        final List<TagDtoResponse> modelDtoList = mapper.modelListToDtoList(page.entities());
        return new PageDtoResponse<>(modelDtoList, page.currentPage(), page.pageCount());
    }

    @Override
    @Transactional(readOnly = true)
    public TagDtoResponse readById(Long id) {
        return tagRepository.readById(id)
            .map(mapper::modelToDto)
            .orElseThrow(
                () -> new NotFoundException(
                    String.format(
                        TAG_ID_DOES_NOT_EXIST.getMessage(),
                        id
                    )
                )
            );
    }

    @Override
    @Transactional
    public TagDtoResponse create(@Valid TagDtoRequest createRequest) {
        try {
            Tag model = mapper.dtoToModel(createRequest);
            model = tagRepository.create(model);
            return mapper.modelToDto(model);
        } catch (EntityConflictRepositoryException exc) {
            throw new ResourceConflictServiceException(TAG_CONFLICT.getMessage(), TAG_CONFLICT.getErrorCode(), exc.getMessage());
        }
    }

    @Override
    @Transactional
    public TagDtoResponse update(Long id, @Valid TagDtoRequest updateRequest) {
        if (tagRepository.existById(id)) {
            Tag model = mapper.dtoToModel(updateRequest);
            model.setId(id);
            model = tagRepository.update(model);
            return mapper.modelToDto(model);
        } else {
            throw new NotFoundException(String.format(TAG_ID_DOES_NOT_EXIST.getMessage(), id));
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (tagRepository.existById(id)) {
            tagRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format(TAG_ID_DOES_NOT_EXIST.getMessage(), id));
        }
    }

    @Transactional(readOnly = true)
    public List<TagDtoResponse> readByNewsId(Long newsId) {
        return mapper.modelListToDtoList(tagRepository.readByNewsId(newsId));
    }
}
