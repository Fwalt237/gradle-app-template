package com.mjc.school.repository.filter.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;


import java.lang.reflect.ParameterizedType;
import java.util.List;

public class SearchFilterSpecification<T> implements Specification<T> {

    private SearchCriteria criteria;

    public SearchFilterSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    public Class<T> getType() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (criteria.getOperation().equals(SearchOperation.GREATER_THAN)) {
            return criteriaBuilder.greaterThan(
                    root.get(criteria.getField()), criteria.getValue().toString());
        }
        else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get(criteria.getField()), criteria.getValue().toString());
        }
        else if (criteria.getOperation().equals(SearchOperation.LESS_THAN)) {
            return criteriaBuilder.lessThan(
                    root.get(criteria.getField()), criteria.getValue().toString());
        }
        else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
            return criteriaBuilder.lessThanOrEqualTo(
                    root.get(criteria.getField()), criteria.getValue().toString());
        }
        else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
            return criteriaBuilder.equal(
                    root.get(criteria.getField()), criteria.getValue());
        }
        else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL)) {
            return criteriaBuilder.notEqual(
                    root.get(criteria.getField()), criteria.getValue());
        }
        else if (criteria.getOperation().equals(SearchOperation.LIKE_START)) {
            return criteriaBuilder.like(
                    root.get(criteria.getField()), criteria.getValue() + "%");
        }
        else if (criteria.getOperation().equals(SearchOperation.LIKE_END)) {
            return criteriaBuilder.like(
                    root.get(criteria.getField()), "%" + criteria.getValue());
        }
        else if (criteria.getOperation().equals(SearchOperation.LIKE)) {
            return criteriaBuilder.like(
                    root.get(criteria.getField()), "%" + criteria.getValue() + "%");
        }
        else if (criteria.getOperation().equals(SearchOperation.IN)) {
            return criteriaBuilder.in(
                    root.get(criteria.getField())).value(criteria.getValue());
        }
        else if (criteria.getOperation().equals(SearchOperation.NOT_IN)) {
            return criteriaBuilder.not(
                    root.get(criteria.getField())).in(criteria.getValue());
        }
        else if (criteria.getOperation().equals(SearchOperation.BETWEEN) && criteria.getValue() instanceof List) {
            List<String> values = (List<String>) criteria.getValue();
            return criteriaBuilder.between(
                    root.get(criteria.getField()), values.get(0), values.get(1));
        }

        return null;
    }
}
