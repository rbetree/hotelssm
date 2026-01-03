-- 会员等级功能数据库迁移脚本
-- 执行日期：2026-01-03
-- 说明：在account表增加level字段，在book_order表增加价格字段，实现会员等级折扣功能

-- 1. 在account表增加level字段
ALTER TABLE `account` ADD COLUMN `level` int(1) DEFAULT '1' COMMENT '会员等级：1=普通会员(九折)，2=高级会员(八折)' AFTER `status`;

-- 2. 在book_order表增加价格相关字段
ALTER TABLE `book_order` ADD COLUMN `originalPrice` decimal(10,2) DEFAULT '0.00' COMMENT '原价（房型价格）' AFTER `leaveDate`;
ALTER TABLE `book_order` ADD COLUMN `discount` decimal(3,2) DEFAULT '1.00' COMMENT '折扣率（0.9=九折，0.8=八折）' AFTER `originalPrice`;
ALTER TABLE `book_order` ADD COLUMN `actualPrice` decimal(10,2) DEFAULT '0.00' COMMENT '实付价格' AFTER `discount`;

-- 3. 更新现有数据（可选：将部分老用户升级为高级会员）
-- UPDATE `account` SET `level` = 2 WHERE `id` IN (1, 2);

-- 4. 验证修改
-- SELECT * FROM `account` LIMIT 5;
-- SELECT * FROM `book_order` LIMIT 5;

