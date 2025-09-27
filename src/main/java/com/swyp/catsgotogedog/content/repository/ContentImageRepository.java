package com.swyp.catsgotogedog.content.repository;

import com.swyp.catsgotogedog.content.domain.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import com.swyp.catsgotogedog.content.domain.entity.ContentImage;

import java.util.List;

public interface ContentImageRepository extends JpaRepository<ContentImage, Integer> {
  ContentImage findByContent_ContentId(int contentId);

  List<ContentImage> findAllByContent(Content content);
}
