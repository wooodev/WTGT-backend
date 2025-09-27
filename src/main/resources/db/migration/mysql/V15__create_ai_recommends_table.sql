CREATE TABLE `catsgotogedog`.`ai_recommends` (
    `recommends_id` INT NOT NULL AUTO_INCREMENT,
    `content_id` INT NOT NULL,
    `message` TEXT NULL,
    `image_url` VARCHAR(255) NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`recommends_id`),
    CONSTRAINT `ai_recommends_content_id_fk`
        FOREIGN KEY (`content_id`)
        REFERENCES `catsgotogedog`.`content` (`content_id`)
        ON DELETE CASCADE
        ON UPDATE NO ACTION
);
