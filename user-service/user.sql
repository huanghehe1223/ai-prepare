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

 Date: 07/10/2024 15:26:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
USE quick;
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `userid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `schoolId` int(11) NULL DEFAULT NULL,
  `sex` tinyint(4) NULL DEFAULT NULL,
  `nickName` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `createTime` datetime(0) NULL DEFAULT NULL,
  `balance` double NULL DEFAULT NULL,
  `state` int(11) NULL DEFAULT NULL,
  `openid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信id',
  `ticket` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '二维码id',
  `score` int(11) NULL DEFAULT NULL COMMENT '闪送员评分',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `deliverReward` double NULL DEFAULT NULL COMMENT '骑手收益',
  `imageUrl` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sentence` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`userid`) USING BTREE,
  INDEX `schoolId`(`schoolId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'accesske', '123456', '17860780522', 1, 0, '旺旺', '2024-07-20 15:05:14', 0, 0, '', '82_0khldwgdVmLeu8FJbWXaG8Q4pf8MDkvsur2KqJHMTcb8Yd4gqHW1vaYteoILfwoQhKvBHGhIGRNDE4tdUENeTl-JQ5E6SinLoQxzLR6poYWsFC-L6BqvTg2F9vgDVCiADARJQ', 0, '1234@example.com', 0, 'https://th.bing.com/th/id/R.0577f41962c64e49262acf89efe49654?rik=sO5fNfs%2fRo721w&riu=http%3a%2f%2fimg.touxiangwu.com%2fuploads%2fallimg%2f2022053119%2fjj0j5bpbmi4.jpg&ehk=JexBHTyth0QHxGfwDipjHzh3pzhl2DfWj5uy6TGnQd8%3d&risl=&pid=ImgRaw&r=0', NULL);
INSERT INTO `user` VALUES (4, '冬日里的暖阳光', '123456', '17860780522', 1, 1, 'heller', '2024-07-20 15:05:16', 0, 0, 'oS3m96NeiLlu4X2kZGD5yh_RXx2s', 'gQEb8DwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAya3ppbGtvYlZlWkUxRGpmRE5DMUQAAgSzM6dmAwQgHAAA', 0, '123@example.com', 0, 'https://lww-buck2.oss-cn-beijing.aliyuncs.com/18e6202b-e919-4d78-979a-f90bac95ad0e.jpg', NULL);
INSERT INTO `user` VALUES (5, '春日里的暖阳光影', '123456', '18702548972', 1, 1, 'jack', '2024-07-20 15:16:55', 672.5, 0, 'oS3m96I9O20Wc-KVYZES63oObWVg', 'gQFM8DwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyU194M2xBYlZlWkUxemR0ek5DMWUAAgStAaNmAwQgHAAA', 0, '1752244645@163.com', 0, 'https://lww-buck2.oss-cn-beijing.aliyuncs.com/18e6202b-e919-4d78-979a-f90bac95ad0e.jpg', NULL);
INSERT INTO `user` VALUES (6, '逐梦远行者', 'password123', '15233355664', 1, 1, 'Johnny', NULL, 0, 1, NULL, NULL, 0, '123456@example.com', 0, 'https://lww-buck2.oss-cn-beijing.aliyuncs.com/9d761778-afcf-4568-a061-f3289c694a6d.jpg', NULL);
INSERT INTO `user` VALUES (7, '我是一只西瓜', 'password123', '12365454545', 1, 1, 'Johnny', NULL, 190.5, 1, NULL, NULL, 0, '1234567@example.com', 0, 'https://th.bing.com/th/id/OIP.nOViTG6HZ2UK68k405bXrwAAAA?rs=1&pid=ImgDetMain', NULL);
INSERT INTO `user` VALUES (8, '黄河', '123', '15656458971', 1, 1, 'moon', NULL, 255.5, 0, NULL, NULL, 0, '12345678@example.com', 0, 'https://th.bing.com/th/id/R.ce0aa2534603f579525e7901a36082a5?rik=%2fPg%2bPh7VUNaIOA&riu=http%3a%2f%2fpic.imeitou.com%2fuploads%2fallimg%2f230112%2f7-230112093403-50.jpg&ehk=WM%2b0HttA4BgHqb73pDcbwNgikfNRBbvg%2bWuEreLxl6Y%3d&risl=&pid=ImgRaw&r=0', NULL);
INSERT INTO `user` VALUES (10, '简则石', 'oS3m96OP6_v7QHaisO0WIbJY_tWA', '12548656547', 1, 1, 'Tim', '2024-07-22 17:01:13', 0, 1, 'oS3m96OP6_v7QHaisO0WIbJY_tWA', 'gQE68DwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyWGNLZWx3YlZlWkUxek5uejFDMU8AAgTR_6JmAwQgHAAA', 0, '123456789@example.com', 0, 'https://lww-buck2.oss-cn-beijing.aliyuncs.com/d4e4a545-bab8-4967-872f-70d9ef8f509f.jpg', '个性签名');
INSERT INTO `user` VALUES (13, '今天吃龙虾', '123456', '16969587456', 1, 1, NULL, NULL, 0, 0, NULL, NULL, 0, '1234567898@example.com', 0, 'https://lww-buck2.oss-cn-beijing.aliyuncs.com/0594f342-cd79-407b-b241-d26d511f5f19.jpg', NULL);
INSERT INTO `user` VALUES (14, '我不怕辣', '123456', '16969587456', 1, 1, NULL, NULL, 499.5, 1, NULL, NULL, 0, '我是黄河@163.com', 0, 'http://pic.imeitou.com/uploads/allimg/240403/10-240403151235.jpg', NULL);
INSERT INTO `user` VALUES (18, '晨光里的诗', 'pass1234', '13000000001', 1, 0, 'Tom', '2024-07-24 14:00:00', 500, 1, 'openid1', 'ticket1', 3, 'user1@example.com', 100.5, 'https://th.bing.com/th/id/OIP.zTbfwHi_ndY_yd5JOJuhRQAAAA?rs=1&pid=ImgDetMain', 'Stay positive');
INSERT INTO `user` VALUES (19, '纸鸢年华', 'pass1235', '13000000002', 1, 1, 'Jerry', '2024-07-24 14:05:00', 200, 0, 'openid2', 'ticket2', 4, 'user2@example.com', 150.75, 'https://th.bing.com/th/id/OIP.fgjIZZR3ebBlK1eJynGWcwAAAA?rs=1&pid=ImgDetMain', 'Keep moving forward');
INSERT INTO `user` VALUES (20, '静夜思', 'pass1236', '13000000003', 1, 0, 'Bob', '2024-07-24 14:10:00', 0, 1, 'openid3', 'ticket3', 0, 'user3@example.com', 0, 'https://lww-buck2.oss-cn-beijing.aliyuncs.com/1f0b881f-fe35-4743-9c53-32404719d864.jpg', 'Challenge yourself');
INSERT INTO `user` VALUES (21, '连往往', 'pass1237', '13000000004', 1, 1, 'Alice', '2024-07-24 14:15:00', 0, 1, 'openid4', 'ticket4', 0, 'user4@example.com', 0, 'https://lww-buck2.oss-cn-beijing.aliyuncs.com/957cb47e-ae42-41f9-a8ed-c8e061d56d2f.jpg', 'Dream big');
INSERT INTO `user` VALUES (22, '雾中旅人', 'pass1238', '13000000005', 1, 0, 'John', '2024-07-24 14:20:00', 0, 1, 'openid5', 'ticket5', 0, 'user5@example.com', 0, 'https://lww-buck2.oss-cn-beijing.aliyuncs.com/8a9a5cfa-6ec8-4fac-87ec-8117f37a4f5c.jpg', 'Never give up');
INSERT INTO `user` VALUES (23, '流星雨的秘密', 'pass1239', '13000000006', 1, 1, 'Emma', '2024-07-24 14:25:00', 0, 1, 'openid6', 'ticket6', 0, 'user6@example.com', 0, 'https://lww-buck2.oss-cn-beijing.aliyuncs.com/bdb1b0d8-f003-4642-8066-fb3372928a9e.jpg', 'Be yourself');
INSERT INTO `user` VALUES (24, '张三杀手', 'pass1240', '13000000007', 1, 0, 'Oliver', '2024-07-24 14:30:00', 550, 1, 'openid7', 'ticket7', 4, 'user7@example.com', 95, 'https://th.bing.com/th/id/OIP.De9OEDEqo8rFlgYtPz3sxQAAAA?rs=1&pid=ImgDetMain', 'Believe in yourself');
INSERT INTO `user` VALUES (25, '黑白钢琴键', 'pass1241', '13000000008', 1, 1, 'Sophia', '2024-07-24 14:35:00', 0, 1, 'openid8', 'ticket8', 0, 'user8@example.com', 0, 'https://lww-buck2.oss-cn-beijing.aliyuncs.com/004df773-3def-4b76-8f76-225c0177eca5.jpg', 'Stay focused');
INSERT INTO `user` VALUES (26, '谈钢琴', 'pass1242', '13000000009', 1, 0, 'Mia', '2024-07-24 14:40:00', 0, 1, 'openid9', 'ticket9', 0, 'user9@example.com', 0, 'https://lww-buck2.oss-cn-beijing.aliyuncs.com/4f93ad13-c68f-4098-9254-d826a5ef2d24.jpg', 'Go for it');
INSERT INTO `user` VALUES (27, '吃饭999', 'pass1243', '13000000010', 1, 1, 'Lucas', '2024-07-24 14:45:00', 0, 0, 'openid10', 'ticket10', 0, 'user10@example.com', 0, 'https://lww-buck2.oss-cn-beijing.aliyuncs.com/ed4c2a2e-aa3e-4663-9e60-787ec197e2f6.jpg', 'Stay strong');
INSERT INTO `user` VALUES (28, '999999', '999999', NULL, NULL, NULL, NULL, NULL, 0, 0, NULL, NULL, 0, '999999@qq.com', 0, 'https://lww-buck2.oss-cn-beijing.aliyuncs.com/86d77301-b7be-431d-99dd-2eb835884367.jpg', NULL);
INSERT INTO `user` VALUES (31, 'tiamor1', '123456', NULL, NULL, NULL, NULL, NULL, 0, 0, NULL, NULL, 0, '17522446452@qq.com', 0, 'https://ui-avatars.com/api/?name=tiamor&background=455a64&color=ffffff', NULL);
INSERT INTO `user` VALUES (32, 'oS3m96CPSTMt4KyI7pq9Qx2vudWY', 'oS3m96CPSTMt4KyI7pq9Qx2vudWY', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'oS3m96CPSTMt4KyI7pq9Qx2vudWY', 'gQEM8TwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyUTN0ZWxCYlZlWkUxR19KeWhDMWwAAgSfkaJmAwQgHAAA', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user` VALUES (33, '连旺旺哈哈哈哈', '123456', NULL, NULL, NULL, NULL, NULL, 0, 0, NULL, NULL, 0, '2058991758@qq.com', 0, 'https://ui-avatars.com/api/?name=连旺旺哈哈哈哈&background=455a64&color=ffffff', NULL);
INSERT INTO `user` VALUES (34, 'oS3m96J5ykFFkcWxPR32eCd9vpoc', 'oS3m96J5ykFFkcWxPR32eCd9vpoc', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'oS3m96J5ykFFkcWxPR32eCd9vpoc', 'gQGT8DwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAySGJ3OWxYYlZlWkUxTHF5eU5DMVYAAgS6xqJmAwQgHAAA', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user` VALUES (35, 'woshilianwangwang', '111111', NULL, NULL, NULL, NULL, NULL, 0, 0, NULL, NULL, 0, '3373558566@qq.com', 0, 'https://ui-avatars.com/api/?name=woshilianwangwang&background=455a64&color=ffffff', NULL);
INSERT INTO `user` VALUES (36, 'testone', '123456789', NULL, NULL, NULL, NULL, NULL, 0, 0, NULL, NULL, 0, '1752244645@qq.com', 0, 'https://ui-avatars.com/api/?name=testone&background=455a64&color=ffffff', NULL);

SET FOREIGN_KEY_CHECKS = 1;
