package com.mjc.school.repository.impl;

import com.mjc.school.repository.model.Comment;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepository extends AbstractDBRepository<Comment, Long> {
    @Override
    void update(Comment prevState, Comment nextState) {
        if (nextState.getContent() != null && !nextState.getContent().isBlank()) {
            prevState.setContent(nextState.getContent());
        }
    }

    public List<Comment> readByNewsId(Long newsId) {
        TypedQuery<Comment> typedQuery = entityManager.createQuery("SELECT c FROM Comment c INNER JOIN " +
            "c.news n WHERE n.id=:newsId", Comment.class);
        typedQuery.setParameter("newsId", newsId);
        return typedQuery.getResultList();
    }
}
