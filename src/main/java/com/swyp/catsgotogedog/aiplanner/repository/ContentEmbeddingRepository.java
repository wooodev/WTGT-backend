package com.swyp.catsgotogedog.aiplanner.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.swyp.catsgotogedog.aiplanner.domain.ContentEmbedding;

public interface ContentEmbeddingRepository extends ElasticsearchRepository<ContentEmbedding, Integer> {
	List<ContentEmbedding> findByContentIdIn(List<Integer> contentIds);
}
