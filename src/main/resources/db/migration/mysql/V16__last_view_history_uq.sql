ALTER TABLE `catsgotogedog`.`last_view_history`
    ADD UNIQUE INDEX `last_view_content_id_user_id` (`user_id` ASC, `content_id` ASC) VISIBLE;
;
