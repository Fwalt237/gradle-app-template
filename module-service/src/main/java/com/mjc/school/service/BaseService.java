package com.mjc.school.service;

import com.mjc.school.repository.filter.specification.EntitySearchSpecification;
import com.mjc.school.service.dto.PageDtoResponse;
import com.mjc.school.service.filter.ResourceSearchFilter;

public interface BaseService<C, R, G, S, U> {
    PageDtoResponse<R> readAll(S searchFilterRequest);

    R readById(G id);

    R create(C createRequest);

    R update(G id, U updateRequest);

    void deleteById(G id);

    default EntitySearchSpecification getEntitySearchSpecification(final ResourceSearchFilter searchFilter) {
        return new EntitySearchSpecification.Builder()
                .pagination(searchFilter.getPagination())
                .sorting(searchFilter.getOrder())
                .searchFilterSpecification(searchFilter.getSearchCriteriaList()).build();
    }

}
