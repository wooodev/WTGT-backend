ALTER TABLE `catsgotogedog`.`user`
ADD COLUMN `name_update_at` DATETIME NULL;
INSERT INTO `catsgotogedog`.`pet_size` (`size`, `size_tooltip`) VALUES ('소형', '소형견: 성견 된 몸무게가 대략 10kg 미만 (성견: 생후 2년 이상)');
INSERT INTO `catsgotogedog`.`pet_size` (`size`, `size_tooltip`) VALUES ('중형', '중형견: 성견 된 몸무게가 대략 10~25kg 미만');
INSERT INTO `catsgotogedog`.`pet_size` (`size`, `size_tooltip`) VALUES ('대형', '대형견: 성견 된 몸무게가 대략 25kg 이상');
