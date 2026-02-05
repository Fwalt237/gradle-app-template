package com.mjc.school.repository.impl;

import com.mjc.school.repository.model.Author;
import com.mjc.school.repository.model.News;
import com.mjc.school.repository.model.Tag;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NewsRepository extends AbstractDBRepository<News, Long> {

    @Override
    void update(News prevState, News nextState) {
        if (nextState.getTitle() != null && !nextState.getTitle().isBlank()) {
            prevState.setTitle(nextState.getTitle());
        }
        if (nextState.getContent() != null && !nextState.getContent().isBlank()) {
            prevState.setContent(nextState.getContent());
        }
        Author author = nextState.getAuthor();
        if (author != null && !author.getName().isBlank()) {
            prevState.setAuthor(nextState.getAuthor());
        }
        List<Tag> tags = nextState.getTags();
        if (tags != null && !tags.isEmpty()) {
            prevState.setTags(tags);
        }
    }
}
