/*
 Navicat Premium Data Transfer

 Source Server         : 1111
 Source Server Type    : MySQL
 Source Server Version : 50714
 Source Host           : localhost:3306
 Source Schema         : quick

 Target Server Type    : MySQL
 Target Server Version : 50714
 File Encoding         : 65001

 Date: 02/10/2024 16:27:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for task
-- ----------------------------
USE quick;
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `publishUserId` int(11) NULL DEFAULT NULL,
  `acceptUserId` int(11) NULL DEFAULT NULL,
  `reward` double NULL DEFAULT NULL,
  `createTime` datetime(0) NULL DEFAULT NULL,
  `endTime` datetime(0) NULL DEFAULT NULL,
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `context` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `state` int(11) NULL DEFAULT NULL,
  `fromPlace` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `toPlace` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `takeTime` datetime(0) NULL DEFAULT NULL COMMENT '骑手接单时间',
  `appointTime` datetime(0) NULL DEFAULT NULL COMMENT '预约取货时间',
  `pickTime` datetime(0) NULL DEFAULT NULL COMMENT '骑手拿货时间',
  `receiveTime` datetime(0) NULL DEFAULT NULL COMMENT '用户收货时间',
  `imageUrl` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单图片',
  `categories` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类别',
  `attachments` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `publishUserId`(`publishUserId`) USING BTREE,
  INDEX `acceptUserId`(`acceptUserId`) USING BTREE,
  INDEX `id`(`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 102 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task
-- ----------------------------
INSERT INTO `task` VALUES (3, 4, 1, 123, '2021-07-12 15:52:00', '2024-07-11 11:08:00', '生日派对用品', '将生日派对所需物品送达指定地点', 1, '派对用品店', '派对举办地', '2024-09-25 21:04:55', '2024-06-22 12:50:00', '2024-06-27 17:50:00', '2024-06-27 17:50:00', NULL, '派对用品', 'https://th.bing.com/th/id/R.b593210a438e2859ce481fdc1a1a0a3e?rik=8kKvyfQFgv%2bJhQ&riu=http%3a%2f%2fimg.51miz.com%2fElement%2f00%2f90%2f68%2f95%2f8d22da23_E906895_d5a3eaa5.png!%2fquality%2f90%2funsharp%2ftrue%2fcompress%2ftrue%2fformat%2fpng&ehk=UClxB3LKTnCcOebx%2fSQcblBj%2bUoDlaOCGBB9p64hToQ%3d&risl=&pid=ImgRaw&r=0');
INSERT INTO `task` VALUES (7, 4, 1, 1, '2024-07-13 10:28:42', '2024-07-12 10:28:00', '晚间药物配送', '将药物从药房送至老年公寓', 1, '药房', '老年公寓', '2024-09-26 10:26:24', '2024-06-27 17:50:00', '2024-06-27 17:50:00', '2024-06-27 17:50:00', NULL, '药品', 'https://th.bing.com/th/id/OIP.HX6VX-BFtIS3nQdZ5NMAZAHaFj?rs=1&pid=ImgDetMain');
INSERT INTO `task` VALUES (8, 4, 1, 12, '2024-07-14 10:28:42', '2024-07-14 10:28:42', '晚间药物配送', '将药物从药房送至老年公寓', 1, '药房', '老年公寓', '2024-09-26 10:33:29', '2024-06-27 17:50:00', '2024-06-27 17:50:00', '2024-06-27 17:50:00', NULL, '药品', 'https://pic4.zhimg.com/v2-9c6faca4d190bd6bfffdd7e917fd90e8_720w.jpg?source=172ae18b');
INSERT INTO `task` VALUES (9, 4, 1, 10, '2024-07-15 10:28:42', '2024-07-15 10:28:42', '健身器材送达', '将购买的健身器材从商店送至客户家中', 1, '健身器材店', '用户住所', '2024-09-26 10:34:40', '2024-06-12 08:50:00', '2024-06-27 17:50:00', '2024-06-27 17:50:00', NULL, '健身器材', 'https://www.ganas.cn/ueditor/php/upload/image/20200423/1587623346250707.jpg');
INSERT INTO `task` VALUES (13, 4, 1, 10, '2024-07-17 10:28:42', '2024-07-17 10:28:42', '生日派对用品', '将生日派对所需物品送达指定地点', 0, '派对用品店', '派对举办地', '2024-09-25 20:54:53', '2024-07-17 10:28:42', '2024-07-17 10:28:42', '2024-07-17 10:28:42', NULL, '派对用品', 'https://th.bing.com/th/id/R.9eb91b3339f86ae2af54198bbebf198b?rik=jPjELNEne1a07A&riu=http%3a%2f%2fimg13.360buyimg.com%2fn1%2fs800x800_jfs%2ft1%2f171538%2f25%2f986%2f225262%2f5ff3faa3E80af63d9%2fd48a72a496485c13.jpg&ehk=HghYOjQIFQSOvcC3NKPr84g1cU1JvlBeqI%2fZOgsZ2D0%3d&risl=&pid=ImgRaw&r=0');
INSERT INTO `task` VALUES (14, 4, 1, 10, '2024-07-18 10:28:42', '2024-07-04 10:28:00', '晚间药物配送', '<p>将药物从药房送至老年公寓</p>', 0, '药房', '老年公寓', '2024-09-25 20:56:17', '2024-07-03 10:28:00', '2024-07-18 10:28:42', '2024-07-18 10:28:42', NULL, '', 'https://img.zcool.cn/community/0198fd5a227333a801216e8d8a6c06.jpg@2o.jpg');
INSERT INTO `task` VALUES (20, 1, 4, 10, '2024-07-22 10:28:42', '2024-07-21 10:28:42', '建筑材料配送', '从建材市场送至施工现场', 1, '建材市场', '施工现场', '2024-09-20 01:23:43', '2024-07-22 10:28:42', '2024-07-22 10:28:42', '2024-07-22 10:28:42', NULL, '建材', 'https://img.zcool.cn/community/01b3865a7828d0a801206f41ca4855.jpg@1280w_1l_2o_100sh.jpg');
INSERT INTO `task` VALUES (21, 4, 36, 10, '2024-07-22 10:28:42', '2024-07-21 10:28:42', '电器维修配送', '将需要维修的电器从用户家中送到维修中心', 1, '用户家中', '维修中心', '2024-09-24 11:32:58', '2024-07-22 10:28:42', '2024-07-22 10:28:42', '2024-07-22 10:28:42', NULL, '电器', 'https://bpic.588ku.com/element_origin_min_pic/21/11/06/8630dd55efea7200cad8a72123b25491.jpg');
INSERT INTO `task` VALUES (22, 4, 36, 10, '2024-07-22 10:28:42', '2024-07-21 10:28:42', '花卉配送', '从花市到酒店布置现场', 1, '本地花市', '酒店布置现场', '2024-09-24 11:33:11', '2024-07-22 10:28:42', '2024-07-22 10:28:42', '2024-07-22 10:28:42', NULL, '花卉,装饰', 'https://oss.huawa.com/shop/placeorder/06775351147179559.jpeg');
INSERT INTO `task` VALUES (23, 4, 36, 10, '2024-07-22 10:28:42', '2024-07-22 10:28:42', '宠物食品配送', '从宠物店将宠物食品送到宠物医院', 1, '宠物店', '宠物医院', '2024-09-24 11:35:40', '2024-07-22 10:28:42', '2024-07-22 10:28:42', '2024-07-22 10:28:42', NULL, '宠物食品', 'https://img95.699pic.com/photo/60031/8431.jpg_wh300.jpg!/fh/300/quality/90');
INSERT INTO `task` VALUES (24, 8, 4, 10, '2024-07-22 10:28:42', '2024-07-22 10:28:42', '婚庆物资配送', '将婚庆所需物资从仓库送到婚礼现场', 1, '婚庆物资仓库', '婚礼现场', '2024-09-13 11:48:51', '2024-07-22 10:28:42', '2024-07-22 10:28:42', '2024-07-22 10:28:42', NULL, '婚礼,装饰', 'https://th.bing.com/th/id/R.4a49174543aaba9d966146b48ced92cb?rik=pD9RAR7fJVy39Q&riu=http%3a%2f%2fseopic.699pic.com%2fphoto%2f50054%2f4778.jpg_wh1200.jpg&ehk=%2fBt6f4TIbQUhgrEL98NGgwEod8fRIjFy2Sxx92Rj1Xg%3d&risl=&pid=ImgRaw&r=0');
INSERT INTO `task` VALUES (25, 8, 5, 10, '2024-07-22 10:28:42', '2024-07-22 10:28:42', '晚餐配送', '从高端餐厅将晚餐送至客户家中', 0, '高端餐厅', '客户家中', '2024-07-26 05:12:24', '2024-07-22 10:28:42', '2024-07-22 10:28:42', '2024-07-22 10:28:42', NULL, '食品,晚餐', 'https://img.zcool.cn/community/017c535a278c44a801216e8d22380b.JPG@1280w_1l_2o_100sh.jpg');
INSERT INTO `task` VALUES (26, 7, 8, 10, '2024-07-22 10:28:42', '2024-07-22 10:28:42', '早市农产品配送', '将早市采购的新鲜农产品送到餐馆', 0, '市区早市', '附近餐馆', '2024-07-22 10:28:42', '2024-07-22 10:28:42', '2024-07-22 10:28:42', '2024-07-22 10:28:42', NULL, '食品,农产品', 'https://th.bing.com/th/id/R.29ee6d05d90342b2c642010d4ec8039c?rik=68G4XD%2b8%2fa3%2b3w&riu=http%3a%2f%2fimg.cnwest.com%2fa%2f10001%2f202211%2f26%2fad886c6f8ea5e5e3e747539ee5921399.jpeg&ehk=OVtxvf8%2fef7GqlOSDKwfrv5L72Wdi1nWXgvEHbKyhZM%3d&risl=&pid=ImgRaw&r=0');
INSERT INTO `task` VALUES (27, 6, 5, 10, '2024-07-23 10:28:42', '2024-07-23 10:28:42', '建筑材料配送', '从建材市场送至施工现场', 2, '建材市场', '施工现场', '2024-07-23 10:28:42', '2024-07-23 10:28:42', '2024-07-23 10:28:42', '2024-07-23 10:28:42', NULL, '建材', 'https://th.bing.com/th/id/R.444111d415b98aac057a936dce5234eb?rik=X0O32M9cb8%2bI0g&riu=http%3a%2f%2fpic.tugou.com%2fjingyan%2f20160421111507_73990.jpg&ehk=gidQy3qcKTHY%2bfzSAJVSsXYVjAgKteglTICLDsBS0Wc%3d&risl=&pid=ImgRaw&r=0&sres=1&sresct=1');
INSERT INTO `task` VALUES (28, 1, 6, 10, '2024-07-23 10:28:42', '2024-07-04 10:28:00', '生日派对用品', '<p>将生日派对所需物品送达指定地点</p>', 1, '派对用品店', '派对举办地', '2024-07-23 10:28:42', '2024-07-02 10:28:00', '2024-07-23 10:28:42', '2024-07-23 10:28:42', NULL, '', 'https://cbu01.alicdn.com/img/ibank/2020/120/252/21965252021_211487005.jpg');
INSERT INTO `task` VALUES (29, 1, 4, 10, '2024-07-24 10:28:42', '2024-07-04 10:28:00', '晚间药物配送', '<p>将药物从药房送至老年公寓</p>', 1, '药房', '老年公寓', '2024-09-13 11:41:54', '2024-07-03 10:28:00', '2024-07-24 10:28:42', '2024-07-24 10:28:42', NULL, '', 'https://img.zcool.cn/community/01365758572df7a801219c77fcef17.jpg@2o.jpg');
INSERT INTO `task` VALUES (30, 7, 8, 10, '2024-07-25 10:28:42', '2024-07-25 10:28:42', '商务文件快递', '紧急商务文件需要从总部快速送达分公司', 2, '总部办公室', '分公司地址', '2024-07-25 10:28:42', '2024-07-25 10:28:42', '2024-07-25 10:28:42', '2024-07-25 10:28:42', NULL, '文件,紧急', 'https://th.bing.com/th/id/OIP.94LdPz35Y2bvNWZQIOpVIQHaFb?rs=1&pid=ImgDetMain');
INSERT INTO `task` VALUES (31, 4, 5, 0, '2024-07-23 19:42:11', '2024-07-24 09:01:28', '花卉配送', '从花市到酒店布置现场', 2, '本地花市', '酒店布置现场', '2024-07-24 09:01:28', '2024-07-24 09:01:28', '2024-07-24 09:01:28', '2024-07-24 09:01:28', NULL, '花卉,装饰', 'https://bpic.588ku.com/back_origin_min_pic/21/03/25/9025bcd7f4886b0dee436389ba28806d.jpg');
INSERT INTO `task` VALUES (32, 6, 5, 10, '2024-07-24 09:01:28', '2024-07-23 19:42:00', '水果', '买水果', 1, '水果店', '研三', '2024-07-26 05:14:16', '2024-07-24 09:01:28', '2024-07-24 09:01:28', '2024-07-24 09:01:28', 'https://ui-avatars.com/api/?name=水果&background=455a64&color=ffffff', '水果，食物', 'https://bpic.588ku.com/back_origin_min_pic/19/10/22/df0934343cd3f0eff985740cf5348b9b.jpg');
INSERT INTO `task` VALUES (33, 6, 1, 10, '2024-07-24 09:03:31', '2024-07-23 19:42:00', '水果', '买水果', 1, '水果店', '研三', '2024-07-24 09:01:28', '2024-07-24 09:01:28', '2024-07-24 09:01:28', '2024-07-24 09:01:28', 'https://ui-avatars.com/api/?name=水果&background=455a64&color=ffffff', '水果，食物', 'https://bpic.588ku.com/back_origin_min_pic/19/10/22/4e0e626ba96c2b0ba2ada046f09de0d9.jpg');
INSERT INTO `task` VALUES (34, 6, 1, 100, '2024-07-24 09:07:58', '2024-07-23 19:42:00', '饮料', '买饮料', 2, '水果店', '研三', '2024-07-24 09:07:58', '2024-07-24 09:07:58', '2024-07-24 09:07:58', '2024-07-24 09:07:58', 'https://ui-avatars.com/api/?name=水果&background=455a64&color=ffffff', '水果，食物', 'https://img.zcool.cn/community/0117f35d483fc6a80120695c301cd5.jpg@2o.jpg');
INSERT INTO `task` VALUES (65, 1, 4, 50, '2024-06-05 14:30:00', '2024-06-05 16:30:00', '文件送达', '紧急文件需要从办公室送到客户手中', 2, '市中心办公室', '客户公司地址', '2024-06-05 14:30:00', '2024-06-05 14:30:00', '2024-06-05 14:30:00', '2024-06-05 14:30:00', NULL, '文件,紧急', 'https://img95.699pic.com/photo/30697/1220.jpg_wh860.jpg');
INSERT INTO `task` VALUES (66, 7, 5, 30, '2024-06-10 09:00:00', '2024-06-10 11:00:00', '早餐配送', '将新鲜烘焙的面包和咖啡送到指定地点', 3, '本地面包店', '科技园区', '2024-06-10 09:00:00', '2024-06-10 09:00:00', '2024-06-10 09:00:00', '2024-06-10 09:00:00', NULL, '食品,早餐', 'https://th.bing.com/th/id/R.b593210a438e2859ce481fdc1a1a0a3e?rik=8kKvyfQFgv%2bJhQ&riu=http%3a%2f%2fimg.51miz.com%2fElement%2f00%2f90%2f68%2f95%2f8d22da23_E906895_d5a3eaa5.png!%2fquality%2f90%2funsharp%2ftrue%2fcompress%2ftrue%2fformat%2fpng&ehk=UClxB3LKTnCcOebx%2fSQcblBj%2bUoDlaOCGBB9p64hToQ%3d&risl=&pid=ImgRaw&r=0');
INSERT INTO `task` VALUES (67, 4, 6, 20, '2024-06-15 10:00:00', '2024-06-15 10:45:00', '药品快递', '从药店取药后送往用户家中', 2, '市区药店', '用户住址', '2024-06-10 09:00:00', '2024-06-10 09:00:00', '2024-06-10 09:00:00', '2024-06-15 10:40:00', NULL, '药品', 'https://th.bing.com/th/id/OIP.HX6VX-BFtIS3nQdZ5NMAZAHaFj?rs=1&pid=ImgDetMain');
INSERT INTO `task` VALUES (68, 5, 1, 80, '2024-06-20 13:00:00', '2024-07-31 14:00:00', '电脑配件交付', '<p>将电脑配件从仓库送至修理店</p>', 1, '配件仓库', '市区修理店', '2024-09-13 15:34:35', '2024-07-01 13:00:00', '2024-06-20 13:00:00', '2024-06-20 13:00:00', NULL, '', 'https://pic4.zhimg.com/v2-9c6faca4d190bd6bfffdd7e917fd90e8_720w.jpg?source=172ae18b');
INSERT INTO `task` VALUES (69, 7, 8, 40, '2024-06-25 18:00:00', '2024-06-25 19:00:00', '餐厅订餐配送', '晚餐高峰期，需要将顾客订购的餐食从餐厅送达', 2, '市中心餐厅', '客户住所', '2024-06-25 18:00:00', '2024-06-25 18:00:00', '2024-06-25 18:00:00', '2024-06-25 18:00:00', NULL, '食品', 'https://www.ganas.cn/ueditor/php/upload/image/20200423/1587623346250707.jpg');
INSERT INTO `task` VALUES (70, 6, 1, 25, '2024-07-05 12:00:00', '2024-07-05 13:00:00', '宠物食品送达', '从宠物店取得宠物食品后送至客户家中', 3, '宠物店', '客户住址', '2024-07-05 12:00:00', '2024-07-05 12:00:00', '2024-07-05 12:00:00', '2024-07-05 12:00:00', NULL, '宠物食品', 'https://th.bing.com/th/id/R.4ae5236b528396dd5a8f9eeef6d8db11?rik=NCpDykz4YPYBuA&riu=http%3a%2f%2fpropic.pandamart.cn%2farticle_20190927111222197394.png&ehk=vdy%2fcQlAR7LmjOXgJ5aOEl9Y8SOM9VKL%2f9pYhcO7PzU%3d&risl=&pid=ImgRaw&r=0');
INSERT INTO `task` VALUES (71, 8, 1, 60, '2024-07-10 11:00:00', '2024-07-10 12:00:00', '生日蛋糕配送', '将预定的生日蛋糕从糕点店送到顾客指定地点', 2, '糕点店', '顾客庆祝地点', '2024-07-10 11:00:00', '2024-07-10 11:00:00', '2024-07-10 11:00:00', '2024-07-10 11:00:00', NULL, '食品,生日', 'https://img.shetu66.com/2023/06/30/1688112593881907.png');
INSERT INTO `task` VALUES (72, 8, 4, 45, '2024-07-15 08:00:00', '2024-07-15 09:00:00', '文档急送', '重要文档需要从办公室快速送达到另一办公地点', 3, '企业办公室', '合作伙伴办公室', '2024-07-15 08:00:00', '2024-07-15 08:00:00', '2024-07-15 08:00:00', '2024-07-15 08:00:00', NULL, '文件,紧急', 'https://img.zcool.cn/community/0198fd5a227333a801216e8d8a6c06.jpg@2o.jpg');
INSERT INTO `task` VALUES (73, 10, 5, 70, '2024-07-20 16:00:00', '2024-07-20 17:00:00', '工艺品传递', '从工艺品店将精致工艺品送至客户', 2, '工艺品店', '客户指定地点', '2024-07-20 16:00:00', '2024-07-20 16:00:00', '2024-07-20 16:00:00', '2024-07-20 16:00:00', NULL, '工艺品', 'https://img.zcool.cn/community/01b3865a7828d0a801206f41ca4855.jpg@1280w_1l_2o_100sh.jpg');
INSERT INTO `task` VALUES (74, 4, 6, 35, '2024-07-26 19:30:00', '2024-07-26 20:30:00', '晚宴食材配送', '从市场采购新鲜食材后，需要快速送至餐厅', 2, '本地市场', '市中心餐厅', NULL, '2024-07-26 19:20:00', '2024-07-26 19:20:00', '2024-07-26 19:20:00', NULL, '食品,晚餐', 'https://bpic.588ku.com/element_origin_min_pic/21/11/06/8630dd55efea7200cad8a72123b25491.jpg');
INSERT INTO `task` VALUES (75, 1, 4, 55, '2024-06-07 14:30:00', '2024-06-07 16:30:00', '图书馆书籍归还', '将借阅的书籍及时归还至图书馆', 2, '用户住所', '市中心图书馆', '2024-06-07 14:40:00', '2024-06-07 14:35:00', '2024-06-07 14:55:00', '2024-06-07 16:20:00', NULL, '书籍,图书馆', 'https://oss.huawa.com/shop/placeorder/06775351147179559.jpeg');
INSERT INTO `task` VALUES (76, 8, 5, 45, '2024-06-12 09:00:00', '2024-06-12 11:00:00', '健身器材送达', '将购买的健身器材从商店送至客户家中', 0, '健身器材店', '用户住所', NULL, '2024-06-12 08:50:00', '2024-07-26 19:20:00', '2024-07-26 19:20:00', NULL, '健身器材', 'https://img95.699pic.com/photo/60031/8431.jpg_wh300.jpg!/fh/300/quality/90');
INSERT INTO `task` VALUES (77, 4, 6, 65, '2024-06-18 10:00:00', '2024-06-18 11:00:00', '建筑材料配送', '从建材市场送至施工现场', 2, '建材市场', '施工现场', NULL, '2024-06-18 11:00:00', '2024-07-26 19:20:00', '2024-06-18 10:45:00', NULL, '建材', 'https://th.bing.com/th/id/R.4a49174543aaba9d966146b48ced92cb?rik=pD9RAR7fJVy39Q&riu=http%3a%2f%2fseopic.699pic.com%2fphoto%2f50054%2f4778.jpg_wh1200.jpg&ehk=%2fBt6f4TIbQUhgrEL98NGgwEod8fRIjFy2Sxx92Rj1Xg%3d&risl=&pid=ImgRaw&r=0');
INSERT INTO `task` VALUES (78, 5, 6, 95, '2024-06-22 13:00:00', '2024-06-22 15:00:00', '生日派对用品', '将生日派对所需物品送达指定地点', 1, '派对用品店', '派对举办地', NULL, '2024-06-22 12:50:00', '2024-06-22 12:50:00', '2024-06-22 12:50:00', NULL, '派对用品', 'https://img.zcool.cn/community/017c535a278c44a801216e8d22380b.JPG@1280w_1l_2o_100sh.jpg');
INSERT INTO `task` VALUES (79, 7, 8, 30, '2024-06-27 18:00:00', '2024-06-27 19:30:00', '晚间药物配送', '将药物从药房送至老年公寓', 3, '药房', '老年公寓', NULL, '2024-06-27 17:50:00', '2024-06-27 17:50:00', '2024-06-27 17:50:00', NULL, '药品', 'https://th.bing.com/th/id/R.29ee6d05d90342b2c642010d4ec8039c?rik=68G4XD%2b8%2fa3%2b3w&riu=http%3a%2f%2fimg.cnwest.com%2fa%2f10001%2f202211%2f26%2fad886c6f8ea5e5e3e747539ee5921399.jpeg&ehk=OVtxvf8%2fef7GqlOSDKwfrv5L72Wdi1nWXgvEHbKyhZM%3d&risl=&pid=ImgRaw&r=0');
INSERT INTO `task` VALUES (80, 6, 1, 20, '2024-07-06 12:00:00', '2024-07-06 13:30:00', '商务文件快递', '紧急商务文件需要从总部快速送达分公司', 2, '总部办公室', '分公司地址', NULL, '2024-07-06 13:30:00', '2024-07-06 13:15:00', '2024-07-06 13:15:00', NULL, '文件,紧急', 'https://th.bing.com/th/id/R.444111d415b98aac057a936dce5234eb?rik=X0O32M9cb8%2bI0g&riu=http%3a%2f%2fpic.tugou.com%2fjingyan%2f20160421111507_73990.jpg&ehk=gidQy3qcKTHY%2bfzSAJVSsXYVjAgKteglTICLDsBS0Wc%3d&risl=&pid=ImgRaw&r=0&sres=1&sresct=1');
INSERT INTO `task` VALUES (81, 8, 1, 85, '2024-07-11 11:00:00', '2024-07-11 12:30:00', '装饰品配送', '将装饰品从商店送至客户新家', 2, '装饰品商店', '客户新居', NULL, '2024-07-11 10:45:00', '2024-07-11 10:45:00', '2024-07-11 10:45:00', NULL, '装饰品', 'https://cbu01.alicdn.com/img/ibank/2020/120/252/21965252021_211487005.jpg');
INSERT INTO `task` VALUES (82, 8, 5, 35, '2024-07-16 08:00:00', '2024-07-16 09:30:00', '花店货物配送', '将花店的新鲜花卉送到婚礼现场', 1, '花店', '婚礼现场', '2024-07-26 05:53:33', '2024-07-06 13:30:00', '2024-07-06 13:30:00', '2024-07-16 09:00:00', NULL, '花卉,婚礼', 'https://img.zcool.cn/community/01365758572df7a801219c77fcef17.jpg@2o.jpg');
INSERT INTO `task` VALUES (83, 1, 4, 100, '2024-07-21 16:00:00', '2024-07-21 17:30:00', '婚礼蛋糕配送', '将定制的大型婚礼蛋糕从糕点店送到婚礼现场', 1, '糕点店', '婚礼现场', '2024-09-13 11:46:41', '2024-07-21 15:45:00', '2024-07-21 15:45:00', '2024-07-21 15:45:00', NULL, '蛋糕,婚礼', 'https://th.bing.com/th/id/OIP.94LdPz35Y2bvNWZQIOpVIQHaFb?rs=1&pid=ImgDetMain');
INSERT INTO `task` VALUES (84, 4, 6, 75, '2024-07-26 19:30:00', '2024-07-26 21:00:00', '晚宴配送', '将餐厅准备的晚宴菜品送至客户指定宴会地点', 2, '高级餐厅', '宴会场所', NULL, '2024-07-26 21:00:00', '2024-07-26 21:00:00', '2024-07-26 21:00:00', NULL, '食品,晚宴', 'https://bpic.588ku.com/back_origin_min_pic/21/03/25/9025bcd7f4886b0dee436389ba28806d.jpg');
INSERT INTO `task` VALUES (85, 14, 5, 25, '2024-06-02 07:30:00', '2024-06-02 08:00:00', '早餐快递', '将健康早餐从餐厅送到办公楼', 0, '市中心早餐店', '附近办公楼', '2024-06-02 07:40:00', '2024-06-02 07:30:00', '2024-06-02 07:50:00', '2024-06-02 07:55:00', NULL, '食品', 'https://bpic.588ku.com/back_origin_min_pic/19/10/22/df0934343cd3f0eff985740cf5348b9b.jpg');
INSERT INTO `task` VALUES (86, 4, 1, 35, '2024-06-06 10:15:00', '2024-06-06 11:00:00', '文件传递', '从法律事务所送往客户指定地点', 1, '法律事务所', '客户指定接收地点', '2024-09-15 16:06:46', '2024-06-06 10:10:00', '2024-06-06 10:10:00', '2024-06-06 10:10:00', NULL, '文件,紧急', 'https://bpic.588ku.com/back_origin_min_pic/19/10/22/4e0e626ba96c2b0ba2ada046f09de0d9.jpg');
INSERT INTO `task` VALUES (87, 5, 8, 55, '2024-06-11 12:00:00', '2024-06-11 13:30:00', '商务午餐配送', '从五星级餐厅配送午餐到企业园区', 2, '五星级餐厅', '企业园区', NULL, '2024-06-11 11:50:00', '2024-06-11 11:50:00', '2024-06-11 11:50:00', NULL, '食品,午餐', 'https://img.zcool.cn/community/0117f35d483fc6a80120695c301cd5.jpg@2o.jpg');
INSERT INTO `task` VALUES (88, 7, 8, 65, '2024-06-17 17:30:00', '2024-06-17 18:30:00', '体育器材配送', '从体育用品店送到学校体育馆', 1, '体育用品店', '学校体育馆', NULL, '2024-06-17 17:20:00', '2024-06-17 17:20:00', '2024-06-17 17:20:00', NULL, '体育器材', 'https://img95.699pic.com/photo/30697/1220.jpg_wh860.jpg');
INSERT INTO `task` VALUES (89, 5, 7, 45, '2024-06-23 14:00:00', '2024-06-23 15:30:00', '电器维修配送', '将需要维修的电器从用户家中送到维修中心', 2, '用户家中', '维修中心', NULL, '2024-06-23 13:50:00', '2024-06-23 13:50:00', '2024-06-23 13:50:00', NULL, '电器', 'https://img.shetu66.com/2023/06/30/1688112593881907.png');
INSERT INTO `task` VALUES (90, 8, 4, 70, '2024-07-03 09:00:00', '2024-07-03 10:30:00', '花卉配送', '从花市到酒店布置现场', 3, '本地花市', '酒店布置现场', NULL, '2024-07-03 08:50:00', '2024-07-03 08:50:00', '2024-07-03 08:50:00', NULL, '花卉,装饰', 'https://img.zcool.cn/community/0198fd5a227333a801216e8d8a6c06.jpg@2o.jpg');
INSERT INTO `task` VALUES (91, 14, 5, 30, '2024-07-08 16:00:00', '2024-07-08 17:00:00', '宠物食品配送', '从宠物店将宠物食品送到宠物医院', 2, '宠物店', '宠物医院', NULL, '2024-07-08 15:50:00', NULL, '2024-07-03 08:50:00', NULL, '宠物食品', 'https://img.zcool.cn/community/01b3865a7828d0a801206f41ca4855.jpg@1280w_1l_2o_100sh.jpg');
INSERT INTO `task` VALUES (92, 4, 6, 40, '2024-07-13 11:30:00', '2024-07-13 12:30:00', '婚庆物资配送', '将婚庆所需物资从仓库送到婚礼现场', 2, '婚庆物资仓库', '婚礼现场', NULL, '2024-07-13 11:20:00', '2024-07-03 08:50:00', '2024-07-03 08:50:00', NULL, '婚礼,装饰', 'https://bpic.588ku.com/element_origin_min_pic/21/11/06/8630dd55efea7200cad8a72123b25491.jpg');
INSERT INTO `task` VALUES (93, 1, 4, 40, '2024-07-19 19:00:00', '2024-07-25 21:32:00', '晚餐配送', '<p>从高端餐厅将晚餐送至客户家中</p>', 1, '高端餐厅', '客户家中', '2024-09-13 11:45:05', '2024-07-19 18:50:00', NULL, NULL, NULL, '蛋糕,快递', 'https://oss.huawa.com/shop/placeorder/06775351147179559.jpeg');
INSERT INTO `task` VALUES (94, 5, 8, 55, '2024-07-24 08:00:00', '2024-07-24 09:30:00', '早市农产品配送', '将早市采购的新鲜农产品送到餐馆', 3, '市区早市', '附近餐馆', NULL, '2024-07-24 07:50:00', '2024-07-03 08:50:00', '2024-07-03 08:50:00', NULL, '食品,农产品', 'https://img95.699pic.com/photo/60031/8431.jpg_wh300.jpg!/fh/300/quality/90');
INSERT INTO `task` VALUES (101, 5, NULL, 5, '2024-07-26 04:58:57', '2024-07-26 06:58:00', '测试', '<p>这是一条测试</p>', 0, '', '', NULL, '2024-07-26 05:00:00', NULL, NULL, 'https://ui-avatars.com/api/?name=测试&background=455a64&color=ffffff', '鲜花,蛋糕,外卖', 'https://lww-buck2.oss-cn-beijing.aliyuncs.com/1510da23-3196-4f6d-9322-e525744520e2.png');

SET FOREIGN_KEY_CHECKS = 1;
