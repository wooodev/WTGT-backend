package com.swyp.catsgotogedog.mypage.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.swyp.catsgotogedog.mypage.domain.entity.LastViewHistory;
import com.swyp.catsgotogedog.mypage.domain.entity.LastViewHistoryId;

public interface MyPageHistoryRepository extends JpaRepository<LastViewHistory, LastViewHistoryId> {
	@Query("SELECT lvh FROM LastViewHistory lvh WHERE lvh.user.userId = :userId ORDER BY lvh.lastViewedAt DESC")
	List<LastViewHistory> findAllByUserId(Integer userId, Pageable pageable);
}
