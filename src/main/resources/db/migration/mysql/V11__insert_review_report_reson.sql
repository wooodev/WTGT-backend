ALTER TABLE `catsgotogedog`.`review_report`
DROP FOREIGN KEY `review_report_review_id`;
ALTER TABLE `catsgotogedog`.`review_report`
    ADD INDEX `review_report_user_id_idx` (`user_id` ASC) INVISIBLE,
ADD UNIQUE INDEX `user_id_review_id_uq` (`user_id` ASC, `review_id` ASC) INVISIBLE;
;
ALTER TABLE `catsgotogedog`.`review_report`
    ADD CONSTRAINT `review_report_review_id`
        FOREIGN KEY (`review_id`)
            REFERENCES `catsgotogedog`.`content_review` (`review_id`)
            ON DELETE CASCADE,
ADD CONSTRAINT `review_report_user_id`
  FOREIGN KEY (`user_id`)
  REFERENCES `catsgotogedog`.`user` (`user_id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;



INSERT INTO `catsgotogedog`.`report_reason` (`reason_id`, `content`) VALUES ('1', '욕설 및 비속어 사용');
INSERT INTO `catsgotogedog`.`report_reason` (`reason_id`, `content`) VALUES ('2', '개인정보 노출');
INSERT INTO `catsgotogedog`.`report_reason` (`reason_id`, `content`) VALUES ('3', '광고 및 홍보성 댓글');
INSERT INTO `catsgotogedog`.`report_reason` (`reason_id`, `content`) VALUES ('4', '도배성 댓글');
INSERT INTO `catsgotogedog`.`report_reason` (`reason_id`, `content`) VALUES ('5', '부적절하거나 불쾌한 내용');
