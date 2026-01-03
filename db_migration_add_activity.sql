-- 活动公布功能数据库迁移脚本
-- 执行日期：2026-01-03
-- 说明：新增activity表，实现酒店活动公布功能

-- 1. 创建activity表
CREATE TABLE IF NOT EXISTS `activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `title` varchar(128) NOT NULL COMMENT '活动标题',
  `content` text COMMENT '活动内容',
  `startTime` datetime DEFAULT NULL COMMENT '活动开始时间',
  `endTime` datetime DEFAULT NULL COMMENT '活动结束时间',
  `status` int(1) DEFAULT '1' COMMENT '状态：0=下架，1=上架',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `photo` varchar(255) DEFAULT NULL COMMENT '活动图片',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='酒店活动表';

-- 2. 插入示例数据
INSERT INTO `activity` VALUES
(1, '春节特惠活动', '春节期间入住享受8折优惠，高级会员可享受7折优惠！活动期间预订任意房型均可参与。', '2026-01-20 00:00:00', '2026-02-10 23:59:59', 1, '2026-01-03 20:00:00', NULL),
(2, '会员日专享', '每月15日为会员日，所有会员当天预订可额外享受9折优惠！', '2026-01-15 00:00:00', '2026-12-31 23:59:59', 1, '2026-01-03 20:00:00', NULL);

-- 3. 验证修改
-- SELECT * FROM `activity`;
