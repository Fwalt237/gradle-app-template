package com.mjc.school.repository.filter.specification;

import com.mjc.school.repository.filter.pagination.Pagination;
import com.mjc.school.repository.filter.sorting.Sorting;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class EntitySearchSpecification {

    private Pagination pagination;

    private List<Sorting> sorting;

    private Specification searchFilterSpecification;

    private EntitySearchSpecification(Builder builder) {
        this.pagination = builder.pagination;
        this.sorting = builder.sorting;
        this.searchFilterSpecification = builder.searchFilterSpecification;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public List<Sorting> getSorting() {
        return sorting;
    }

    public Specification getSearchFilterSpecification() {
        return searchFilterSpecification;
    }

    public static class Builder {
        private Pagination pagination;

        private List<Sorting> sorting;

        private Specification searchFilterSpecification;

        public EntitySearchSpecification build() {
            return new EntitySearchSpecification(this);
        }

        public Builder pagination(final Pagination pagination) {
            this.pagination = pagination;
            return this;
        }

        public Builder sorting(final List<Sorting> sorting) {
            this.sorting = sorting;
            return this;
        }

        public Builder searchFilterSpecification(final List<SearchCriteria> searchCriteriaList) {
            this.searchFilterSpecification = new SearchFilterSpecificationsBuilder<>()
                    .withSearchCriteriaList(searchCriteriaList).build();
            return this;
        }
    }
}
