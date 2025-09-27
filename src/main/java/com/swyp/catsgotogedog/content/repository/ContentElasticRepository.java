package com.swyp.catsgotogedog.content.repository;

import com.swyp.catsgotogedog.content.domain.entity.ContentDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ContentElasticRepository extends ElasticsearchRepository<ContentDocument, Integer> {
    List<ContentDocument> findByTitleContaining(String title);
}
