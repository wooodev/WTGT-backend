CREATE TABLE `catsgotogedog`.`review_recommend_history` (
`recommend_history_id` INT NOT NULL AUTO_INCREMENT,
`review_id` INT NOT NULL,
`user_id` INT NOT NULL,
`created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`recommend_history_id`),
INDEX `review_review_id_fk_idx` (`review_id` ASC) VISIBLE,
INDEX `recommend_history_user_user_id_fk_idx` (`user_id` ASC) VISIBLE,
CONSTRAINT `recommend_history_review_review_id_fk`
    FOREIGN KEY (`review_id`)
        REFERENCES `catsgotogedog`.`content_review` (`review_id`)
        ON DELETE CASCADE
        ON UPDATE NO ACTION,
CONSTRAINT `recommend_history_user_user_id_fk`
    FOREIGN KEY (`user_id`)
        REFERENCES `catsgotogedog`.`user` (`user_id`)
        ON DELETE CASCADE
        ON UPDATE NO ACTION);

ALTER TABLE `catsgotogedog`.`review_recommend_history`
    ADD UNIQUE INDEX `review_id_user_id_recommend_uq` (`user_id` ASC, `review_id` ASC) VISIBLE;
ALTER TABLE `catsgotogedog`.`review_recommend_history` ALTER INDEX `recommend_history_user_user_id_fk_idx` INVISIBLE;

ALTER TABLE `catsgotogedog`.`content_review`
    CHANGE COLUMN `like` `recommended_number` INT NULL DEFAULT '0' ;
