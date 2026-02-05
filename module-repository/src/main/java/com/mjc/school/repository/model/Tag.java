package com.mjc.school.repository.model;

import javax.persistence.*;

import java.util.List;

@Entity
@Table(name = "tags")
public class Tag implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<News> news;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(final List<News> news) {
        this.news = news;
    }
}
