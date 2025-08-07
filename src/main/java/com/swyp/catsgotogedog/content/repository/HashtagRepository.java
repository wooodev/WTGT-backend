package com.swyp.catsgotogedog.content.repository;

import com.swyp.catsgotogedog.content.domain.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface HashtagRepository extends JpaRepository<Hashtag, Integer> {
    @Query("select h.content from Hashtag h where h.contentId = :contentId")
    List<String> findContentsByContentId(int contentId);

    boolean existsByContentId(int contentId);

    List<Hashtag> findByContentId(int contentId);

    List<Hashtag> findByContentIdIn(Collection<Integer> contentIds);
}
