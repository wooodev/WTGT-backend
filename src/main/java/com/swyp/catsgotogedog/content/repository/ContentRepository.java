package com.swyp.catsgotogedog.content.repository;

import com.swyp.catsgotogedog.content.domain.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import com.swyp.catsgotogedog.content.domain.entity.Content;

public interface ContentRepository extends JpaRepository<Content, Integer> {
    Content findByContentId(int contentId);
}
