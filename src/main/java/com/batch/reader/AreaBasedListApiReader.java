package com.batch.reader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.batch.client.MigrationClient;
import com.batch.dto.AreaBasedListResponse;
import com.batch.dto.AreaBasedListResponse.Item;
import com.batch.dto.ContentTypeId;
import com.swyp.catsgotogedog.content.repository.ContentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AreaBasedListApiReader implements ItemReader<Item> {

	private final MigrationClient migrationClient;
	private final ContentRepository contentRepository;
	private final Queue<Item> itemQueue = new ConcurrentLinkedQueue<>();

	private int currentPageNo = 1;
	private final int numOfRows = 100;
	private int currentContentTypeIndex = 0;
	private int totalPages = 1;
	private boolean isInitialLoad = false;
	private String modifiedTimeParam = null;

	private Iterator<Item> itemIterator;
	private List<String> contentTypeIds = List.of(
		String.valueOf(ContentTypeId.관광지.getContentTypeId()),
		String.valueOf(ContentTypeId.축제공연행사.getContentTypeId()),
		String.valueOf(ContentTypeId.숙박.getContentTypeId()),
		String.valueOf(ContentTypeId.음식점.getContentTypeId())
	);

	@Override
	public Item read() throws
		Exception,
		UnexpectedInputException,
		ParseException,
		NonTransientResourceException {

		if (!itemQueue.isEmpty()) {
			return itemQueue.poll();
		}

		boolean hasMore = fetchNextPage();
		if (!hasMore || itemQueue.isEmpty()) {
			return null;
		}

		return itemQueue.poll();
	}

	private boolean fetchNextPage() {
		while(currentContentTypeIndex < contentTypeIds.size()) {

			if(currentPageNo == 1 && currentContentTypeIndex == 0) {
				isInitialLoad = contentRepository.count() == 0;
				modifiedTimeParam = isInitialLoad
					? null
					: LocalDate.now().minusDays(3).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

				log.info("Content {} 데이터 처리 시작 :: {}",
					isInitialLoad ? "초기" : "갱신", modifiedTimeParam);
			}

			var currentContentTypeId = contentTypeIds.get(currentContentTypeIndex);
			log.info("처리 중인 ContentType ID: {}, 페이지: {}/{}", currentContentTypeId, currentPageNo, totalPages);

			AreaBasedListResponse response;
			try {
				response = migrationClient.getAreaBasedLists(currentPageNo, numOfRows, currentContentTypeId, modifiedTimeParam);
			} catch(Exception e) {
				log.error("API 호출 중 오류 발생 (contentTypeId : {}, pageNo : {})", currentContentTypeId, currentPageNo, e);
				moveToNextContentType();
				continue;
			}

			if (response == null || response.response() == null || response.response().body() == null) {
				log.warn("응답 없음 - contentTypeId: {}, pageNo: {}", currentContentTypeId, currentPageNo);
				moveToNextContentType();
				continue;
			}
			var body = response.response().body();
			var items = (body.items() == null) ? null : body.items().item();

			if (items == null || items.isEmpty()) {
				log.info("더 이상 데이터 없음 - contentTypeId: {}, pageNo: {}", currentContentTypeId, currentPageNo);
				moveToNextContentType();
				continue;
			}

			itemQueue.addAll(items);

			if (currentPageNo == 1) {
				totalPages = (int) Math.ceil((double) body.totalCount() / numOfRows);
				log.info("총 건수: {}, 총 페이지: {}", body.totalCount(), totalPages);
			}

			currentPageNo++;

			if (currentPageNo > totalPages) {
				log.info("ContentType 완료 - {}", currentContentTypeId);
				moveToNextContentType();
			}

			return true;
		}
		log.info("모든 ContentType 처리 완료");
		return false;
	}

	private void moveToNextContentType() {
		currentContentTypeIndex++;
		currentPageNo = 1;
		totalPages = 1;
	}
}
