package com.mjc.school.repository.impl;

import com.mjc.school.repository.model.Tag;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TagRepository extends AbstractDBRepository<Tag, Long> {

    @Override
    void update(Tag prevState, Tag nextState) {
        if (nextState.getName() != null && !nextState.getName().isBlank()) {
            prevState.setName(nextState.getName());
        }
    }

    public List<Tag> readByNewsId(Long newsId) {
        TypedQuery<Tag> typedQuery = entityManager.createQuery("SELECT t FROM Tag t INNER JOIN " +
            "t.news n WHERE n.id=:newsId", Tag.class);
        typedQuery.setParameter("newsId", newsId);
        return typedQuery.getResultList();
    }

    public Optional<Tag> readByName(String name) {
        TypedQuery<Tag> typedQuery = entityManager.createQuery(
            "SELECT t FROM Tag t WHERE t.name=:name", Tag.class);
        typedQuery.setParameter("name", name);
        try {
            return Optional.of(typedQuery.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }
}
