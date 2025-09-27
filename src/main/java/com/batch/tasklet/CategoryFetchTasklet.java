package com.batch.tasklet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.batch.client.MigrationClient;
import com.batch.dto.CategoryCodeResponse;
import com.swyp.catsgotogedog.category.domain.entity.CategoryCode;
import com.swyp.catsgotogedog.category.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryFetchTasklet implements Tasklet {

	private final MigrationClient migrationClient;
	private final CategoryRepository categoryRepository;

	private static final List<String> CATEGORY_LIST = Arrays.asList(
		"12", // 관광지
		"14", // 문화시설
		"15", // 축제공연행사
		"25", // 여행코스
		"28", // 레포츠
		"32", // 숙박
		"38", // 쇼핑
		"39"  // 음식점
	);

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		if(categoryRepository.count() > 0) {
			log.info("카테고리 데이터가 존재하여 스킵");
			return RepeatStatus.FINISHED;
		};

		log.info("Category Fetch Tasklet Start");
		List<CategoryCode> categoryList = new ArrayList<>();

		for(String category : CATEGORY_LIST) {
			log.info("Fetching Category : {}", category);

			// cat1 대분류 조회
			List<CategoryCodeResponse.Item> cat1Items = migrationClient.getCategoryCode(category, null, null);
			for(CategoryCodeResponse.Item cat1Item : cat1Items) {
				log.debug("cat1Item : {} ({})", cat1Item, cat1Item.getCode());

				List<CategoryCodeResponse.Item> cat2Items = migrationClient.getCategoryCode(category, cat1Item.getCode(), null);

				for(CategoryCodeResponse.Item cat2Item : cat2Items) {
					log.debug("cat2Item : {} ({})", cat2Item, cat2Item.getCode());

					List<CategoryCodeResponse.Item> cat3Items = migrationClient.getCategoryCode(category, cat1Item.getCode(), cat2Item.getCode());

					for(CategoryCodeResponse.Item cat3Item : cat3Items) {

						log.debug("cat3Item : {} ({})", cat3Item, cat3Item.getCode());

						categoryList.add(CategoryCode.builder()
							.categoryId(cat3Item.getCode())
							.categoryName(cat3Item.getName())
							.contentTypeId(Integer.parseInt(category))
							.build());
					}
				}
			}
		}
		categoryRepository.saveAll(categoryList);
		log.info("Category Fetch Tasklet End");
		return RepeatStatus.FINISHED;
	}
}
