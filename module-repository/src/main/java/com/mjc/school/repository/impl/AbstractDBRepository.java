package com.mjc.school.repository.impl;

import com.mjc.school.repository.BaseRepository;
import com.mjc.school.repository.exception.EntityConflictRepositoryException;
import com.mjc.school.repository.filter.pagination.Page;
import com.mjc.school.repository.filter.sorting.SortOrder;
import com.mjc.school.repository.filter.sorting.Sorting;
import com.mjc.school.repository.filter.specification.EntitySearchSpecification;
import com.mjc.school.repository.filter.specification.SearchFilterSpecification;
import com.mjc.school.repository.model.BaseEntity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import org.hibernate.PersistentObjectException;
import org.hibernate.exception.ConstraintViolationException;


import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public abstract class AbstractDBRepository<T extends BaseEntity<K>, K> implements BaseRepository<T, K> {

    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<T> entityClass;
    private final Class<K> idClass;

    abstract void update(T prevState, T nextState);

    protected AbstractDBRepository() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        entityClass = (Class<T>) type.getActualTypeArguments()[0];
        idClass = (Class<K>) type.getActualTypeArguments()[1];
    }

    @Override
    public Page<T> readAll(final EntitySearchSpecification specification) {
      CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
      CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
      final Root<T> root = criteriaQuery.from(entityClass);

      SearchFilterSpecification searchSpec = (SearchFilterSpecification) specification.getSearchFilterSpecification();
      setSearchConditions(searchSpec, criteriaBuilder, criteriaQuery, root);
      setOrder(specification.getSorting(), criteriaBuilder, criteriaQuery, root);

      final int currentPage = specification.getPagination().page();
      final int pageSize = specification.getPagination().pageSize();
      TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
      typedQuery.setFirstResult((currentPage - 1) * pageSize);
      typedQuery.setMaxResults(pageSize);

      return new Page<T>(typedQuery.getResultList(), currentPage, countPages(searchSpec, pageSize));
    }

    private void setSearchConditions(
            SearchFilterSpecification searchSpecification, CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery, Root<T> root) {
        if (searchSpecification != null) {
            Predicate predicate = searchSpecification
                .toPredicate(root, criteriaQuery, criteriaBuilder);
            criteriaQuery.where(predicate);
        }
    }

    private void setOrder(
           List<Sorting> sortingList, CriteriaBuilder criteriaBuilder, CriteriaQuery<T> criteriaQuery, Root<T> root) {
        List<Order> orders = new ArrayList<>();
        for (Sorting sorting : sortingList) {
            Path<Object> fieldPath = root.get(sorting.field());
            Order order = SortOrder.ASC.equals(sorting.order()) ? criteriaBuilder.asc(fieldPath) :
                    criteriaBuilder.desc(fieldPath);
            orders.add(order);
        }
        criteriaQuery.orderBy(orders);
    }

    private int countPages(SearchFilterSpecification searchSpec, int pageSize) {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        final Root<T> root = countQuery.from(entityClass);

        setSearchConditions(searchSpec, criteriaBuilder, countQuery, root);

        countQuery.select(criteriaBuilder.count(root));
        Long entityCount = entityManager.createQuery(countQuery).getSingleResult();

        if (entityCount % pageSize == 0) {
            return (int) (entityCount / pageSize);
        }
        return (int) (entityCount / pageSize) + 1;
    }

    @Override
    public Optional<T> readById(K id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    @Override
    public T create(T entity) {
        try {
            entityManager.persist(entity);
            return entity;
        } catch (PersistentObjectException | ConstraintViolationException exc) {
            throw new EntityConflictRepositoryException(exc.getMessage());
        }
    }

    @Override
    public T update(T entity) {
            return readById(entity.getId()).map(existingEntity -> {
                update(existingEntity, entity);
                T updated = entityManager.merge(existingEntity);
                // flush is needed for auditable entities to get actual value of @LastModifiedDate field
                entityManager.flush();
                return updated;
            }).orElse(null);
    }

    @Override
    public void deleteById(K id) {
        if (id != null) {
            T entityRef = getReference(id);
            entityManager.remove(entityRef);
        }
    }

    @Override
    public boolean existById(K id) {
        EntityType<T> entityType = entityManager.getMetamodel().entity(entityClass);
        String idFieldName = entityType.getId(idClass).getName();

        Query query = entityManager.createQuery("SELECT COUNT(*) FROM " + entityClass.getSimpleName() +
            " WHERE " + idFieldName + " = ?1").setParameter(1, id);
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    public T getReference(K id) {
        return entityManager.getReference(this.entityClass, id);
    }
}
