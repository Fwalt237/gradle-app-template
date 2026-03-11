package com.mjc.school.repository.impl;


import com.mjc.school.repository.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface NewsRepository extends JpaRepository<News,Long>, JpaSpecificationExecutor<News> {

    boolean existsByTitle(String title);

    @Modifying
    @Query("DELETE FROM News n WHERE n.createdDate < :date")
    void deleteOlderThan(LocalDateTime date);
}
