package com.swyp.catsgotogedog.content.repository;

import com.swyp.catsgotogedog.User.domain.entity.User;
import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.content.domain.entity.ContentWish;

import com.swyp.catsgotogedog.content.repository.projection.WishCountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ContentWishRepository extends JpaRepository<ContentWish, Integer> {

	@Query("SELECT COUNT(c) FROM ContentWish c WHERE c.content.contentId = :contentId")
	int countByContent_ContentId(int contentId);

    @Query("SELECT cw FROM ContentWish cw WHERE cw.userId = :userId AND cw.content.contentId = :contentId")
	Optional<ContentWish> findByUserIdAndContentId(@Param("userId") int userId, @Param("contentId") int contentId);

	@Query("SELECT cw.content.contentId FROM ContentWish cw WHERE cw.userId = :userId AND cw.content.contentId IN :contentIds")
	Set<Integer> findWishedContentIdsByUserIdAndContentIds(@Param("userId") Integer userId, @Param("contentIds") List<Integer> contentIds);

	Page<ContentWish> findAllByUserId(int userId, Pageable pageable);

	boolean existsByUserIdAndContent_ContentId(int userId, int contentId);

	void deleteByUserIdAndContent(int userId, Content content);

	List<ContentWish> findByUserIdAndContentContentIdIn(@Param("userId") int userId, @Param("topContentIds") List<Integer> topContentIds);

	@Query("SELECT cw.content.contentId FROM ContentWish cw WHERE cw.userId = :userId")
	List<Integer> findContentIdsByUserId(@Param("userId") int userId);

	long countByUserId(int userId);

	@Query("""
           SELECT cw.content.contentId AS contentId,
                  COUNT(cw)            AS wishCount
           FROM ContentWish cw
           WHERE cw.content.contentId IN :contentIds
           GROUP BY cw.content.contentId
           """)
	List<WishCountProjection> countByContentIdIn(List<Integer> contentIds);
}
