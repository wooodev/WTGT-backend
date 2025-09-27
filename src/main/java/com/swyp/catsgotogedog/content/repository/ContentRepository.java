package com.swyp.catsgotogedog.content.repository;

import java.util.List;

import com.swyp.catsgotogedog.content.domain.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swyp.catsgotogedog.content.domain.entity.Content;

public interface ContentRepository extends JpaRepository<Content, Integer> {
    Content findByContentId(int contentId);

    @Query(value =
        "SELECT c.* FROM content c "
        + "LEFT JOIN hashtag h ON c.content_id = h.content_id "
        + "WHERE h.content_id IS NULL", nativeQuery = true)
    List<Content> findContentsWithoutHashtags();

    List<Content> findAllByContentIdIn(List<Integer> contentIds);

    /**
     * 이미지가 있는 컨텐츠만 랜덤으로 5개 조회 (AI 추천용)
     */
    @Query(value = """
    SELECT DISTINCT c.* FROM content c 
    WHERE c.image != ""
    AND c.overview != ""
    ORDER BY RAND() LIMIT 5
    """, nativeQuery = true)
    List<Content> findRandomContentsWithImages();

    /**
     * 특정 해시태그를 가진 장소들 중 이미지가 있는 장소만 랜덤으로 5개 조회 (찜한 장소 제외)
     */
    @Query(value = """
    SELECT DISTINCT c.* FROM content c 
    INNER JOIN hashtag h ON c.content_id = h.content_id 
    WHERE h.content IN :hashtags 
    AND c.content_id NOT IN :excludeContentIds
    AND c.image != ""
    AND c.overview != ""
    ORDER BY RAND() LIMIT 5
    """, nativeQuery = true)
    List<Content> findRandomContentsByHashtagsExcluding(
        @Param("hashtags") List<String> hashtags,
        @Param("excludeContentIds") List<Integer> excludeContentIds
    );
}
