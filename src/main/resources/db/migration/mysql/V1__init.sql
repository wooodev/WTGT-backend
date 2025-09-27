-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema catsgotogedog
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema catsgotogedog
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `catsgotogedog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `catsgotogedog` ;

-- -----------------------------------------------------
-- Table `catsgotogedog`.`category_code`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`category_code` (
                                                               `category_id` INT NOT NULL AUTO_INCREMENT,
                                                               `category_name` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`category_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '숙박, 음식점, 관광지 등 카테고리 분류를 위한 테이블';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`region_code`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`region_code` (
                                                             `region_id` INT NOT NULL AUTO_INCREMENT,
                                                             `region_name` VARCHAR(50) NOT NULL,
    `parent_code` INT NULL DEFAULT NULL,
    PRIMARY KEY (`region_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '지역코드 구분 테이블';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`content`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`content` (
                                                         `content_id` INT NOT NULL AUTO_INCREMENT,
                                                         `category_id` INT NOT NULL,
                                                         `region_id` INT NOT NULL,
                                                         `addr1` VARCHAR(100) NULL DEFAULT NULL,
    `addr2` VARCHAR(100) NULL DEFAULT NULL,
    `image` VARCHAR(255) NULL DEFAULT NULL,
    `thumb_image` VARCHAR(255) NULL DEFAULT NULL,
    `copyright` VARCHAR(10) NULL DEFAULT NULL,
    `mapx` DECIMAL(10,8) NULL DEFAULT NULL,
    `mapy` DECIMAL(11,8) NULL DEFAULT NULL,
    `mlevel` SMALLINT NULL DEFAULT NULL,
    `modified_at` DATETIME NULL DEFAULT NULL,
    `tel` VARCHAR(20) NULL DEFAULT NULL,
    `title` VARCHAR(255) NULL DEFAULT NULL,
    `zipcode` INT NULL DEFAULT NULL,
    `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    `content_type_id` INT NOT NULL,
    PRIMARY KEY (`content_id`),
    INDEX `category_code_category_code_idx` (`category_id` ASC) VISIBLE,
    INDEX `region_code_region_id_idx` (`region_id` ASC) VISIBLE,
    CONSTRAINT `category_code_category_id`
    FOREIGN KEY (`category_id`)
    REFERENCES `catsgotogedog`.`category_code` (`category_id`),
    CONSTRAINT `region_code_region_id`
    FOREIGN KEY (`region_id`)
    REFERENCES `catsgotogedog`.`region_code` (`region_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '장소 목록 테이블';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`content_image`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`content_image` (
                                                               `content_image_id` INT NOT NULL AUTO_INCREMENT,
                                                               `content_id` INT NOT NULL,
                                                               `image_url` VARCHAR(255) NULL DEFAULT NULL,
    `image_filename` VARCHAR(255) NULL DEFAULT NULL,
    `small_image_url` VARCHAR(255) NULL DEFAULT NULL,
    `small_image_filename` VARCHAR(255) NULL DEFAULT NULL,
    PRIMARY KEY (`content_image_id`),
    INDEX `content_content_id_content_image_fk_idx` (`content_id` ASC) VISIBLE,
    CONSTRAINT `content_content_id_content_image_fk`
    FOREIGN KEY (`content_id`)
    REFERENCES `catsgotogedog`.`content` (`content_id`)
    ON DELETE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '장소별 사진 테이블';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`user` (
                                                      `user_id` INT NOT NULL AUTO_INCREMENT,
                                                      `display_name` VARCHAR(50) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `provider` VARCHAR(50) NOT NULL,
    `provider_id` VARCHAR(255) NOT NULL,
    `image_filename` VARCHAR(255) NOT NULL,
    `image_url` VARCHAR(255) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_active` TINYINT NULL DEFAULT '1',
    PRIMARY KEY (`user_id`),
    UNIQUE INDEX `display_name_UNIQUE` (`display_name` ASC) VISIBLE,
    UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '유저 정보';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`content_review`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`content_review` (
                                                                `review_id` INT NOT NULL AUTO_INCREMENT,
                                                                `user_id` INT NOT NULL,
                                                                `content_id` INT NOT NULL,
                                                                `content` VARCHAR(6000) NOT NULL,
    `score` DECIMAL(2,1) NOT NULL,
    `like` INT NULL DEFAULT '0',
    `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`review_id`),
    INDEX `review_user_id_idx` (`user_id` ASC) VISIBLE,
    INDEX `review_content_id_idx` (`content_id` ASC) VISIBLE,
    CONSTRAINT `review_content_id`
    FOREIGN KEY (`content_id`)
    REFERENCES `catsgotogedog`.`content` (`content_id`),
    CONSTRAINT `review_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `catsgotogedog`.`user` (`user_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '장소 리뷰';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`festival_information`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`festival_information` (
                                                                      `festival_id` INT NOT NULL AUTO_INCREMENT,
                                                                      `content_id` INT NOT NULL,
                                                                      `age_limit` VARCHAR(45) NULL DEFAULT NULL,
    `booking_place` VARCHAR(50) NULL DEFAULT NULL,
    `discount_info` VARCHAR(100) NULL DEFAULT NULL,
    `event_start_date` DATE NULL DEFAULT NULL,
    `event_end_date` DATE NULL DEFAULT NULL,
    `event_homepage` VARCHAR(255) NULL DEFAULT NULL,
    `event_place` VARCHAR(100) NULL DEFAULT NULL,
    `place_info` VARCHAR(50) NULL DEFAULT NULL,
    `play_time` VARCHAR(50) NULL DEFAULT NULL,
    `program` VARCHAR(100) NULL DEFAULT NULL,
    `spend_time` VARCHAR(50) NULL DEFAULT NULL,
    `organizer` VARCHAR(50) NULL DEFAULT NULL,
    `organizer_tel` VARCHAR(50) NULL DEFAULT NULL,
    `supervisor` VARCHAR(45) NULL DEFAULT NULL,
    `sub_event` VARCHAR(100) NULL DEFAULT NULL,
    `fee_info` VARCHAR(100) NULL DEFAULT NULL,
    PRIMARY KEY (`festival_id`),
    INDEX `content_content_id_fk_idx` (`content_id` ASC) VISIBLE,
    CONSTRAINT `content_content_id_festival_information_fk`
    FOREIGN KEY (`content_id`)
    REFERENCES `catsgotogedog`.`content` (`content_id`)
    ON DELETE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '소개정보조회_행사_공연_축제';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`hashtag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`hashtag` (
                                                         `hashtag_id` INT NOT NULL,
                                                         `content_id` INT NOT NULL,
                                                         `content` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`hashtag_id`),
    INDEX `hashtag_content_id_idx` (`content_id` ASC) VISIBLE,
    CONSTRAINT `hashtag_content_id`
    FOREIGN KEY (`content_id`)
    REFERENCES `catsgotogedog`.`content` (`content_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '해시태그';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`last_view_history`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`last_view_history` (
                                                                   `user_id` INT NOT NULL,
                                                                   `content_id` INT NOT NULL,
                                                                   `last_viewed_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                                   PRIMARY KEY (`user_id`, `content_id`),
    INDEX `last_view_history_content_id_idx` (`content_id` ASC) VISIBLE,
    CONSTRAINT `last_view_history_content_id`
    FOREIGN KEY (`content_id`)
    REFERENCES `catsgotogedog`.`content` (`content_id`),
    CONSTRAINT `last_view_history_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `catsgotogedog`.`user` (`user_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '최근 본 장소';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`lodge_information`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`lodge_information` (
                                                                   `lodge_id` INT NOT NULL AUTO_INCREMENT,
                                                                   `content_id` INT NOT NULL,
                                                                   `capacity_count` INT NULL DEFAULT NULL,
                                                                   `lodge_informationcol` INT NULL DEFAULT NULL,
                                                                   `benikia` TINYINT NULL DEFAULT NULL,
                                                                   `check_in_time` TIME NULL DEFAULT NULL,
                                                                   `check_out_time` TIME NULL DEFAULT NULL,
                                                                   `cooking` VARCHAR(50) NULL DEFAULT NULL,
    `foodplace` VARCHAR(50) NULL DEFAULT NULL,
    `hanok` TINYINT NULL DEFAULT NULL,
    `information` VARCHAR(50) NULL DEFAULT NULL,
    `parking` VARCHAR(50) NULL DEFAULT NULL,
    `pickup_service` TINYINT NULL DEFAULT NULL,
    `room_count` INT NULL DEFAULT NULL,
    `reservation_info` VARCHAR(30) NULL DEFAULT NULL,
    `reservation_url` VARCHAR(50) NULL DEFAULT NULL,
    `room_type` VARCHAR(30) NULL DEFAULT NULL,
    `scale` VARCHAR(30) NULL DEFAULT NULL,
    `sub_facility` VARCHAR(50) NULL DEFAULT NULL,
    `barbecue` TINYINT NULL DEFAULT NULL,
    `beauty` TINYINT NULL DEFAULT NULL,
    `beverage` TINYINT NULL DEFAULT NULL,
    `bicycle` TINYINT NULL DEFAULT NULL,
    `campfire` TINYINT NULL DEFAULT NULL,
    `fitness` TINYINT NULL DEFAULT NULL,
    `karaoke` TINYINT NULL DEFAULT NULL,
    `public_bath` TINYINT NULL DEFAULT NULL,
    `public_pc_room` TINYINT NULL DEFAULT NULL,
    `sauna` TINYINT NULL DEFAULT NULL,
    `seminar` TINYINT NULL DEFAULT NULL,
    `sports` TINYINT NULL DEFAULT NULL,
    `refund_regulation` VARCHAR(100) NULL DEFAULT NULL,
    PRIMARY KEY (`lodge_id`),
    INDEX `content_content_id_lodge_information_fk_idx` (`content_id` ASC) VISIBLE,
    CONSTRAINT `content_content_id_lodge_information_fk`
    FOREIGN KEY (`content_id`)
    REFERENCES `catsgotogedog`.`content` (`content_id`)
    ON DELETE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '소개정보조회_숙박';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`pet_size`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`pet_size` (
                                                          `size_id` INT NOT NULL AUTO_INCREMENT,
                                                          `size` VARCHAR(10) NOT NULL,
    `size_tooltip` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`size_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '반려동물 사이즈 정보';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`pet`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`pet` (
                                                     `pet_id` INT NOT NULL AUTO_INCREMENT,
                                                     `user_id` INT NOT NULL,
                                                     `gender` CHAR(1) NOT NULL,
    `birth` DATE NOT NULL,
    `fierce_dog` TINYINT NOT NULL,
    `size_id` INT NOT NULL,
    `name` VARCHAR(20) NOT NULL,
    `image_filename` VARCHAR(255) NULL DEFAULT NULL,
    `image_url` VARCHAR(255) NULL DEFAULT NULL,
    PRIMARY KEY (`pet_id`),
    INDEX `user_id_idx` (`user_id` ASC) VISIBLE,
    INDEX `size_id_idx` (`size_id` ASC) VISIBLE,
    CONSTRAINT `pet_pet_size_id`
    FOREIGN KEY (`size_id`)
    REFERENCES `catsgotogedog`.`pet_size` (`size_id`),
    CONSTRAINT `pet_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `catsgotogedog`.`user` (`user_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '반려동물 정보';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`pet_guide`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`pet_guide` (
                                                           `pet_guide_id` INT NOT NULL AUTO_INCREMENT,
                                                           `content_id` INT NOT NULL,
                                                           `accident_prep` VARCHAR(50) NULL DEFAULT NULL,
    `available_facility` VARCHAR(50) NULL DEFAULT NULL,
    `provided_item` VARCHAR(50) NULL DEFAULT NULL,
    `etc_info` VARCHAR(255) NULL DEFAULT NULL,
    `purchasable_item` VARCHAR(50) NULL DEFAULT NULL,
    `allowed_pet_type` VARCHAR(50) NULL DEFAULT NULL,
    `rent_item` VARCHAR(50) NULL DEFAULT NULL,
    `pet_prep` VARCHAR(50) NULL DEFAULT NULL,
    `with_pet` VARCHAR(50) NULL DEFAULT NULL,
    PRIMARY KEY (`pet_guide_id`),
    INDEX `content_content_id_pet_guide_fk_idx` (`content_id` ASC) VISIBLE,
    CONSTRAINT `content_content_id_pet_guide_fk`
    FOREIGN KEY (`content_id`)
    REFERENCES `catsgotogedog`.`content` (`content_id`)
    ON DELETE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '장소별 반려동물 동반시 안내사항';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`recur_information`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`recur_information` (
                                                                   `recur_id` INT NOT NULL AUTO_INCREMENT,
                                                                   `content_id` INT NOT NULL,
                                                                   `info_name` VARCHAR(45) NULL DEFAULT NULL,
    `info_text` TEXT NULL DEFAULT NULL,
    PRIMARY KEY (`recur_id`),
    INDEX `content_content_id_recur_information_fk_idx` (`content_id` ASC) VISIBLE,
    CONSTRAINT `content_content_id_recur_information_fk`
    FOREIGN KEY (`content_id`)
    REFERENCES `catsgotogedog`.`content` (`content_id`)
    ON DELETE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '장소별 반복정보';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`recur_information_room`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`recur_information_room` (
                                                                        `recur_room_id` INT NOT NULL AUTO_INCREMENT,
                                                                        `content_id` INT NOT NULL,
                                                                        `room_title` VARCHAR(100) NULL DEFAULT NULL,
    `room_size1` INT NULL DEFAULT NULL,
    `room_count` INT NULL DEFAULT NULL,
    `room_base_couint` INT NULL DEFAULT NULL,
    `room_max_count` INT NULL DEFAULT NULL,
    `off_season_week_min_fee` INT NULL DEFAULT NULL,
    `off_season_weekend_min_fee` INT NULL DEFAULT NULL,
    `peak_season_week_min_fee` INT NULL DEFAULT NULL,
    `peak_season_weekend_min_fee` INT NULL DEFAULT NULL,
    `room_intro` TEXT NULL DEFAULT NULL,
    `room_bath_pacility` TINYINT NULL DEFAULT NULL,
    `room_bath` TINYINT NULL DEFAULT NULL,
    `room_home_theater` TINYINT NULL DEFAULT NULL,
    `room_aircondition` TINYINT NULL DEFAULT NULL,
    `room_tv` TINYINT NULL DEFAULT NULL,
    `room_pc` TINYINT NULL DEFAULT NULL,
    `room_cable` TINYINT NULL DEFAULT NULL,
    `room_internet` TINYINT NULL DEFAULT NULL,
    `room_refrigerator` TINYINT NULL DEFAULT NULL,
    `room_toiletries` TINYINT NULL DEFAULT NULL,
    `room_sofa` TINYINT NULL DEFAULT NULL,
    `room_cook` TINYINT NULL DEFAULT NULL,
    `room_table` TINYINT NULL DEFAULT NULL,
    `room_hairdryer` TINYINT NULL DEFAULT NULL,
    `room_size2` DECIMAL(10,2) NULL DEFAULT NULL,
    PRIMARY KEY (`recur_room_id`),
    INDEX `content_content_id_recur_information_room_fk_idx` (`content_id` ASC) VISIBLE,
    CONSTRAINT `content_content_id_recur_information_room_fk`
    FOREIGN KEY (`content_id`)
    REFERENCES `catsgotogedog`.`content` (`content_id`)
    ON DELETE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '숙박 객실별 반복정보';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`recur_information_room_image`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`recur_information_room_image` (
                                                                              `image_id` INT NOT NULL AUTO_INCREMENT,
                                                                              `recur_room_id` INT NOT NULL,
                                                                              `image_url` VARCHAR(255) NULL DEFAULT NULL,
    `image_filename` VARCHAR(255) NULL DEFAULT NULL,
    `image_alt` VARCHAR(255) NULL DEFAULT NULL,
    `image_copyright` VARCHAR(50) NULL DEFAULT NULL,
    PRIMARY KEY (`image_id`),
    INDEX `recur_information_room_recur_information_room_image_fk_idx` (`recur_room_id` ASC) VISIBLE,
    CONSTRAINT `recur_information_room_recur_information_room_image_fk`
    FOREIGN KEY (`recur_room_id`)
    REFERENCES `catsgotogedog`.`recur_information_room` (`recur_room_id`)
    ON DELETE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '반복정보 객실별 이미지';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`refresh_token`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`refresh_token` (
                                                               `user_id` INT NOT NULL,
                                                               `refresh_token` TEXT NOT NULL,
                                                               `expires_at` DATETIME NOT NULL,
                                                               `is_revoked` TINYINT NOT NULL DEFAULT '0',
                                                               PRIMARY KEY (`user_id`),
    CONSTRAINT `fk_token_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `catsgotogedog`.`user` (`user_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = 'JWT 리프레시 토큰 정보';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`report_reason`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`report_reason` (
                                                               `reason_id` INT NOT NULL AUTO_INCREMENT,
                                                               `content` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`reason_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '신고 사유 목록';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`restaurant_information`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`restaurant_information` (
                                                                        `restaurant_id` INT NOT NULL AUTO_INCREMENT,
                                                                        `content_id` INT NOT NULL,
                                                                        `chk_creditcard` VARCHAR(50) NULL DEFAULT NULL,
    `discount_info` VARCHAR(100) NULL DEFAULT NULL,
    `signature_menu` VARCHAR(100) NULL DEFAULT NULL,
    `information` VARCHAR(100) NULL DEFAULT NULL,
    `kids_facility` TINYINT NULL DEFAULT NULL,
    `open_date` DATE NULL DEFAULT NULL,
    `open_time` VARCHAR(50) NULL DEFAULT NULL,
    `takeout` VARCHAR(10) NULL DEFAULT NULL,
    `parking` VARCHAR(100) NULL DEFAULT NULL,
    `reservation` VARCHAR(100) NULL DEFAULT NULL,
    `rest_date` VARCHAR(50) NULL DEFAULT NULL,
    `scale` INT NULL DEFAULT NULL,
    `seat` INT NULL DEFAULT NULL,
    `smoking` TINYINT NULL DEFAULT NULL,
    `treat_menu` VARCHAR(100) NULL DEFAULT NULL,
    PRIMARY KEY (`restaurant_id`),
    INDEX `content_content_id_restaurant_information_fk_idx` (`content_id` ASC) VISIBLE,
    CONSTRAINT `content_content_id_restaurant_information_fk`
    FOREIGN KEY (`content_id`)
    REFERENCES `catsgotogedog`.`content` (`content_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '소개정보조회_음식점';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`review_image`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`review_image` (
                                                              `image_id` INT NOT NULL AUTO_INCREMENT,
                                                              `review_id` INT NOT NULL,
                                                              `image_filename` VARCHAR(255) NOT NULL,
    `image_url` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`image_id`),
    INDEX `review_image_review_id_idx` (`review_id` ASC) VISIBLE,
    CONSTRAINT `review_image_review_id`
    FOREIGN KEY (`review_id`)
    REFERENCES `catsgotogedog`.`content_review` (`review_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '리뷰 이미지';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`review_report`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`review_report` (
                                                               `report_id` INT NOT NULL AUTO_INCREMENT,
                                                               `review_id` INT NOT NULL,
                                                               `reason_id` INT NOT NULL,
                                                               `user_id` INT NOT NULL,
                                                               PRIMARY KEY (`report_id`),
    INDEX `review_report_review_id_idx` (`review_id` ASC) VISIBLE,
    INDEX `review_report_reason_id_idx` (`reason_id` ASC) VISIBLE,
    CONSTRAINT `review_report_reason_id`
    FOREIGN KEY (`reason_id`)
    REFERENCES `catsgotogedog`.`report_reason` (`reason_id`),
    CONSTRAINT `review_report_review_id`
    FOREIGN KEY (`review_id`)
    REFERENCES `catsgotogedog`.`content_review` (`review_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '리뷰 신고';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`sigtes_information`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`sigtes_information` (
                                                                    `sights_id` INT NOT NULL AUTO_INCREMENT,
                                                                    `content_id` INT NOT NULL,
                                                                    `content_type_id` INT NULL DEFAULT NULL,
                                                                    `accom_count` INT NULL DEFAULT NULL,
                                                                    `chk_creditcard` VARCHAR(50) NULL DEFAULT NULL,
    `exp_age_range` VARCHAR(45) NULL DEFAULT NULL,
    `exp_guide` VARCHAR(500) NULL DEFAULT NULL,
    `info_center` VARCHAR(100) NULL DEFAULT NULL,
    `open_date` DATE NULL DEFAULT NULL,
    `parking` VARCHAR(50) NULL DEFAULT NULL,
    `rest_date` VARCHAR(50) NULL DEFAULT NULL,
    `use_season` VARCHAR(50) NULL DEFAULT NULL,
    `use_time` VARCHAR(50) NULL DEFAULT NULL,
    `heritage1` TINYINT NULL DEFAULT NULL,
    `heritage2` TINYINT NULL DEFAULT NULL,
    `heritage3` TINYINT NULL DEFAULT NULL,
    PRIMARY KEY (`sights_id`),
    INDEX `content_content_id_fk_idx` (`content_id` ASC) VISIBLE,
    CONSTRAINT `content_content_id_fk`
    FOREIGN KEY (`content_id`)
    REFERENCES `catsgotogedog`.`content` (`content_id`)
    ON DELETE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '소개정보조회_관광지';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`ticket`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`ticket` (
                                                        `ticket_id` INT NOT NULL AUTO_INCREMENT,
                                                        `email` VARCHAR(255) NOT NULL,
    `content` TEXT NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`ticket_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '문의 내역';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`view_log`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`view_log` (
                                                          `view_id` INT NOT NULL AUTO_INCREMENT,
                                                          `user_id` INT NOT NULL,
                                                          `content_id` INT NOT NULL,
                                                          `viewed_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
                                                          PRIMARY KEY (`view_id`, `user_id`, `content_id`),
    INDEX `view_log_user_id_idx` (`user_id` ASC) VISIBLE,
    INDEX `view_log_content_id_idx` (`content_id` ASC) VISIBLE,
    CONSTRAINT `view_log_content_id`
    FOREIGN KEY (`content_id`)
    REFERENCES `catsgotogedog`.`content` (`content_id`),
    CONSTRAINT `view_log_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `catsgotogedog`.`user` (`user_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '조회 정보 기록 저장';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`view_total`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`view_total` (
                                                            `content_id` INT NOT NULL,
                                                            `total_view` INT NULL DEFAULT '0',
                                                            `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                            PRIMARY KEY (`content_id`),
    CONSTRAINT `view_total_content_id`
    FOREIGN KEY (`content_id`)
    REFERENCES `catsgotogedog`.`content` (`content_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '장소별 전체 조회수';


-- -----------------------------------------------------
-- Table `catsgotogedog`.`visit_history`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `catsgotogedog`.`visit_history` (
                                                               `visit_id` INT NOT NULL AUTO_INCREMENT,
                                                               `user_id` INT NOT NULL,
                                                               `content_id` INT NOT NULL,
                                                               PRIMARY KEY (`visit_id`),
    INDEX `visit_history_user_id_idx` (`user_id` ASC) VISIBLE,
    INDEX `visit_history_content_id_idx` (`content_id` ASC) VISIBLE,
    CONSTRAINT `visit_history_content_id`
    FOREIGN KEY (`content_id`)
    REFERENCES `catsgotogedog`.`content` (`content_id`),
    CONSTRAINT `visit_history_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `catsgotogedog`.`user` (`user_id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '방문한 장소';


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
