package com.swyp.catsgotogedog.content.repository;

import com.swyp.catsgotogedog.User.domain.entity.User;
import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.content.domain.entity.VisitHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitHistoryRepository extends JpaRepository<VisitHistory, Integer> {
    boolean existsByUser_UserIdAndContent_ContentId(int userId, int contentId);

    void deleteByUserAndContent(User user, Content content);

}
