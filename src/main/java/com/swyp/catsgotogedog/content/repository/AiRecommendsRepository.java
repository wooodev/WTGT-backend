package com.swyp.catsgotogedog.content.repository;

import com.swyp.catsgotogedog.content.domain.entity.AiRecommends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiRecommendsRepository extends JpaRepository<AiRecommends, Integer> {

    @Query(value = "SELECT * FROM ai_recommends ORDER BY RAND() LIMIT 5", nativeQuery = true)
    List<AiRecommends> findRandomRecommends();

    long count();
}
