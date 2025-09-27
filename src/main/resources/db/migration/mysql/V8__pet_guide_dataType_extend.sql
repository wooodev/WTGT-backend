ALTER TABLE `catsgotogedog`.`pet_guide`
    CHANGE COLUMN `accident_prep` `accident_prep` VARCHAR(200) NULL DEFAULT NULL ,
    CHANGE COLUMN `provided_item` `provided_item` VARCHAR(200) NULL DEFAULT NULL ,
    CHANGE COLUMN `purchasable_item` `purchasable_item` VARCHAR(200) NULL DEFAULT NULL ,
    CHANGE COLUMN `rent_item` `rent_item` VARCHAR(200) NULL DEFAULT NULL ,
    CHANGE COLUMN `pet_prep` `pet_prep` VARCHAR(200) NULL DEFAULT NULL ,
    CHANGE COLUMN `with_pet` `with_pet` VARCHAR(200) NULL DEFAULT NULL ;
