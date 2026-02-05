package com.mjc.school.repository.impl;

import com.mjc.school.repository.model.Author;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AuthorRepository extends AbstractDBRepository<Author, Long> {

    @Override
    void update(Author prevState, Author nextState) {
        if (nextState.getName() != null && !nextState.getName().isBlank()) {
            prevState.setName(nextState.getName());
        }
    }

    public Optional<Author> readByNewsId(Long newsId) {
        TypedQuery<Author> typedQuery = entityManager.createQuery("SELECT a FROM Author a INNER JOIN " +
            "a.news n WHERE n.id=:newsId", Author.class);
        typedQuery.setParameter("newsId", newsId);
        try {
            return Optional.of(typedQuery.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<Author> readByName(String name) {
        TypedQuery<Author> typedQuery = entityManager.createQuery(
            "SELECT a FROM Author a WHERE a.name=:name", Author.class);
        typedQuery.setParameter("name", name);
        try {
            return Optional.of(typedQuery.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }
}
