package com.mjc.school.service.impl;

import com.mjc.school.repository.exception.EntityConflictRepositoryException;
import com.mjc.school.repository.impl.TagRepository;
import com.mjc.school.repository.model.Author;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.service.BaseService;
import com.mjc.school.service.dto.*;
import com.mjc.school.service.exceptions.NotFoundException;
import com.mjc.school.service.exceptions.ResourceConflictServiceException;
import com.mjc.school.service.filter.ResourceSearchFilter;
import com.mjc.school.service.filter.mapper.BaseSearchFilterMapper;
import com.mjc.school.service.filter.mapper.TagSearchFilterMapper;
import com.mjc.school.service.mapper.TagMapper;
import com.mjc.school.service.validator.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mjc.school.service.exceptions.ServiceErrorCode.*;

@Service
public class TagService implements
    BaseService<TagDtoRequest, TagDtoResponse, Long, ResourceSearchFilterRequestDTO, TagDtoRequest> {

    private final TagRepository tagRepository;
    private final TagMapper mapper;
    private final TagSearchFilterMapper tagSearchFilterMapper;

    @Autowired
    public TagService(TagRepository tagRepository, TagMapper mapper, TagSearchFilterMapper tagSearchFilterMapper) {
        this.tagRepository = tagRepository;
        this.mapper = mapper;
        this.tagSearchFilterMapper = tagSearchFilterMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageDtoResponse<TagDtoResponse> readAll(@Valid ResourceSearchFilterRequestDTO searchFilterRequest) {
        final ResourceSearchFilter searchFilter = tagSearchFilterMapper.map(searchFilterRequest);
        final Specification<Tag> specification = getEntitySearchSpecification(searchFilter).getSearchFilterSpecification();
        final Pageable pageable = createPageable(searchFilter);
        final Page<Tag> page = tagRepository.findAll(specification,pageable);
        final List<TagDtoResponse> modelDtoList = mapper.modelListToDtoList(page.getContent());
        return new PageDtoResponse<>(modelDtoList, page.getNumber()+1, page.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public TagDtoResponse readById(Long id) {
        return tagRepository.findById(id)
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
            model = tagRepository.save(model);
            return mapper.modelToDto(model);
        } catch (EntityConflictRepositoryException exc) {
            throw new ResourceConflictServiceException(TAG_CONFLICT.getMessage(), TAG_CONFLICT.getErrorCode(), exc.getMessage());
        }
    }

    @Override
    @Transactional
    public TagDtoResponse update(Long id, @Valid TagDtoRequest updateRequest) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(()->new NotFoundException(String.format(TAG_ID_DOES_NOT_EXIST.getMessage(), id)));

        if(updateRequest.name()!=null && !updateRequest.name().isBlank()){
            tag.setName(updateRequest.name());
        }
        Tag updatedTag = tagRepository.save(tag);
        return mapper.modelToDto(updatedTag);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (tagRepository.existsById(id)) {
            tagRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format(TAG_ID_DOES_NOT_EXIST.getMessage(), id));
        }
    }

    @Transactional(readOnly = true)
    public List<TagDtoResponse> readByNewsId(Long newsId) {
        return mapper.modelListToDtoList(tagRepository.findByNewsId(newsId));
    }
}
