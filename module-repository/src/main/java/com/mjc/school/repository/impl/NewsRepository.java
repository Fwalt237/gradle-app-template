package com.mjc.school.repository.impl;


import com.mjc.school.repository.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface NewsRepository extends JpaRepository<News,Long>, JpaSpecificationExecutor<News> {
}
