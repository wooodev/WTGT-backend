CREATE TABLE `catsgotogedog`.`content_wish` (
`wish_id` INT NOT NULL AUTO_INCREMENT,
`user_id` INT NOT NULL,
`content_id` INT NOT NULL,
PRIMARY KEY (`wish_id`),
INDEX `content_wish_user_id_fk_idx` (`user_id` ASC) VISIBLE,
INDEX `content_wish_content_id_fk_idx` (`content_id` ASC) VISIBLE,
UNIQUE INDEX `content_wish_wish_uq` (`user_id` ASC, `content_id` ASC) VISIBLE,
CONSTRAINT `content_wish_user_id_fk`
    FOREIGN KEY (`user_id`)
        REFERENCES `catsgotogedog`.`user` (`user_id`)
        ON DELETE CASCADE
        ON UPDATE NO ACTION,
CONSTRAINT `content_wish_content_id_fk`
    FOREIGN KEY (`content_id`)
        REFERENCES `catsgotogedog`.`content` (`content_id`)
        ON DELETE CASCADE
        ON UPDATE NO ACTION)
COMMENT = '장소 찜 목록';
