-- ----------------------------
-- Table structure for user
-- ----------------------------
# DROP TABLE IF EXISTS `user`;
# CREATE TABLE `user`
# (
#     `id`       bigint(0)                                                      NOT NULL AUTO_INCREMENT COMMENT '用户ID',
#     `username`      varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '用户昵称',
#     `userAccount`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '账号',
#     `avatarUrl`    varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '头像',
#     `gender`        tinyint(0)                                                     NULL     DEFAULT 1 COMMENT '性别（1：男 2：女）',
#     `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '密码',
#     `phone`         varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '电话',
#     `email`         varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '邮箱',
#     `tags`          varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '标签 json 列表',
#     `profile`       varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '个人简介',
#     `userStatus`   tinyint(0)                                                     NOT NULL DEFAULT 0 COMMENT '用户状态 (0：正常 )',
#     `createTime`   datetime(0)                                                    NULL     DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
#     `updateTime`   datetime(0)                                                    NULL     DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
#     `isDelete`     tinyint(0)                                                     NOT NULL DEFAULT 0 COMMENT '是否删除',
#     `userRole`     int(0)                                                         NOT NULL DEFAULT 2 COMMENT '用户角色(1：管理员 2：普通用户)',
#     PRIMARY KEY (`id`) USING BTREE
# ) ENGINE = InnoDB
#   AUTO_INCREMENT = 1029063
#   CHARACTER SET = utf8mb4
#   COLLATE = utf8mb4_general_ci COMMENT = '用户'
#   ROW_FORMAT = Dynamic;



INSERT INTO yupi.`user`
VALUES (23250, '巴卿', 'henriette', 'https://ae01.alicdn.com/kf/HTB1IWVMbvWG3KVjSZFP760aiXXaU.png', 1,
        '20f7fd41b4f5e36a9cdc0e3217f11b00', '15714759800', 'p2t9kxlh@qq.com', 0,
        '2023-02-08 16:48:56', '2023-02-08 16:48:56', 0, 0, 12433, '[\"go\",\"男\",\"大四\"]');
INSERT INTO yupi.`user`
VALUES (23251, '闾丘素菱', 'zane', 'https://ae01.alicdn.com/kf/HTB1oU0SbBKw3KVjSZTE763uRpXaH.png', 2,
        '20f7fd41b4f5e36a9cdc0e3217f11b00', '13862242127', 'rsonbj@qq.com', '[\"java\",\"女\",\"大三\"]',
        '有个肺动脉奋勇协调流行性腮腺炎上帝，莱索托损伤眼药。', '2023-02-08 16:48:56', '2023-02-08 16:48:56', '2023-02-08 16:48:56', 0, 2);
INSERT INTO yupi.`user`
VALUES (23252, '千茗珠', 'cassey', 'https://ae01.alicdn.com/kf/HTB1hepNbA5E3KVjSZFC762uzXXaf.png', 2,
        '20f7fd41b4f5e36a9cdc0e3217f11b00', '13176786796', '8h@qq.com', '[\"python\",\"男\",\"大一\"]',
        '有个情人节互相投笔从戎趣事墓道，嘴巴买好仇怨。', '2023-02-08 16:48:56', '2023-02-08 16:48:56', '2023-02-08 16:48:56', 0, 2);
INSERT INTO yupi.`user`
VALUES (23253, '荣迪', 'francine', 'https://ae01.alicdn.com/kf/HTB1QlxNbBWD3KVjSZFs763qkpXaw.png', 1,
        '20f7fd41b4f5e36a9cdc0e3217f11b00', '18703491110', 'wrhtxjw@qq.com', '[\"python\",\"女\",\"大二\"]',
        '所有冷言冷语万万拉开违法行为，那些绝技提交咸菜。', 0, '2023-02-08 16:48:56', '2023-02-08 16:48:56', 0, 2);
INSERT INTO yupi.`user`
VALUES (23254, '倪树', 'diamond', 'https://ae01.alicdn.com/kf/HTB1M1JMbECF3KVjSZJn762nHFXaF.png', 2,
        '20f7fd41b4f5e36a9cdc0e3217f11b00', '17478124629', 'dnlfggbp5q@qq.com', '[\"python\",\"女\",\"大三\"]',
        '那些发票油然领导乡镇，太阴发病新德里。', 0, '2023-02-08 16:48:56', '2023-02-08 16:48:56', 0, 2);
INSERT INTO yupi.`user`
VALUES (23255, '曾琴宇', 'dottie', 'https://ae01.alicdn.com/kf/HTB1sBdLbvWG3KVjSZFg762TspXab.png', 2,
        '20f7fd41b4f5e36a9cdc0e3217f11b00', '16531094093', 'oxiul@qq.com', '[\"go\",\"女\",\"大三\"]',
        '兰陵王势必口试满山遍野么？这个口头语相信净值。', 0, '2023-02-08 16:48:56', '2023-02-08 16:48:56', 0, 2);
INSERT INTO yupi.`user`
VALUES (23256, '潘雪', 'clementine', 'https://ae01.alicdn.com/kf/HTB1lkFMbA9E3KVjSZFG76319XXar.png', 1,
        '20f7fd41b4f5e36a9cdc0e3217f11b00', '19679203612', 'zmiyewkxpr@qq.com', '[\"python\",\"男\",\"大三\"]',
        '这些空气隔夜蹉跎岁月冰雪节仪式。', 0, '2023-02-08 16:48:56', '2023-02-08 16:48:56', 0, 2);
INSERT INTO yupi.`user`
VALUES (23257, '万羽', 'calvin', 'https://ae01.alicdn.com/kf/HTB1PpdMbwKG3KVjSZFL761MvXXau.png', 1,
        '20f7fd41b4f5e36a9cdc0e3217f11b00', '13961106377', 'rwqsev@qq.com', '[\"python\",\"男\",\"大一\"]',
        '蛙泳万般哽咽旅舍。', 0, '2023-02-08 16:48:57', '2023-02-08 16:48:57', 0, 2);
INSERT INTO yupi.`user`
VALUES (23258, '荆哲', 'branden', 'https://ae01.alicdn.com/kf/HTB1lkFMbA9E3KVjSZFG76319XXar.png', 1,
        '20f7fd41b4f5e36a9cdc0e3217f11b00', '16978860243', 'cg5ovaqol@qq.com', '[\"go\",\"女\",\"大四\"]',
        '前程刚巧气喘吁吁身材，沟槽钟情爆发力。', 0, '2023-02-08 16:48:57', '2023-02-08 16:48:57', 0, 2);
INSERT INTO yupi.`user`
VALUES (23259, '景婵', 'josphine', 'https://ae01.alicdn.com/kf/HTB12IdVbq5s3KVjSZFN763D3FXaH.png', 1,
        '20f7fd41b4f5e36a9cdc0e3217f11b00', '17027988826', 'ipzfsj164@qq.com', '[\"java\",\"男\",\"大二\"]',
        '那些苇子如今忘记泰山溶解度，诚然人手纠缠春节。', 0, '2023-02-08 16:48:57', '2023-02-08 16:48:57', 0, 2);
INSERT INTO yupi.`user`
VALUES (23260, '明燕佳', 'lera', 'https://ae01.alicdn.com/kf/HTB19ohMbEuF3KVjSZK9762VtXXaZ.png', 1,
        '20f7fd41b4f5e36a9cdc0e3217f11b00', '14700348424', 'a3r5nl@qq.com', '[\"go\",\"女\",\"大三\"]',
        '天涯并排左迁孬种吧？电力网绝迹乌梅。', 0, '2023-02-08 16:48:57', '2023-02-08 16:48:57', 0, 2);
