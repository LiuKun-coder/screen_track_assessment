-- 中文测试数据初始化脚本（每张业务表不少于20条）
SET NAMES utf8mb4;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE biz_appeal;
TRUNCATE TABLE biz_track;
TRUNCATE TABLE biz_violation;
TRUNCATE TABLE biz_fence;
TRUNCATE TABLE sys_admin;
TRUNCATE TABLE sys_user;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. 系统用户（20条）
INSERT INTO sys_user (username, password, name, phone, email, avatar, department, user_type, status, create_time, update_time, deleted) VALUES
('20260001', '123456', '张晨', '13800010001', NULL, NULL, '计算机学院', 'student', 'active', NOW(), NOW(), 0),
('20260002', '123456', '李航', '13800010002', NULL, NULL, '计算机学院', 'student', 'active', NOW(), NOW(), 0),
('20260003', '123456', '王宁', '13800010003', NULL, NULL, '信息工程学院', 'student', 'active', NOW(), NOW(), 0),
('20260004', '123456', '赵悦', '13800010004', NULL, NULL, '信息工程学院', 'student', 'active', NOW(), NOW(), 0),
('20260005', '123456', '孙杰', '13800010005', NULL, NULL, '机械学院', 'student', 'active', NOW(), NOW(), 0),
('20260006', '123456', '周晴', '13800010006', NULL, NULL, '机械学院', 'student', 'active', NOW(), NOW(), 0),
('20260007', '123456', '吴昊', '13800010007', NULL, NULL, '材料学院', 'student', 'active', NOW(), NOW(), 0),
('20260008', '123456', '郑琳', '13800010008', NULL, NULL, '材料学院', 'student', 'active', NOW(), NOW(), 0),
('20260009', '123456', '冯涛', '13800010009', NULL, NULL, '电气学院', 'student', 'active', NOW(), NOW(), 0),
('20260010', '123456', '陈楠', '13800010010', NULL, NULL, '电气学院', 'student', 'active', NOW(), NOW(), 0),
('20260011', '123456', '褚敏', '13800010011', NULL, NULL, '数学学院', 'teacher', 'active', NOW(), NOW(), 0),
('20260012', '123456', '卫杰', '13800010012', NULL, NULL, '数学学院', 'teacher', 'active', NOW(), NOW(), 0),
('20260013', '123456', '蒋欣', '13800010013', NULL, NULL, '外语学院', 'teacher', 'active', NOW(), NOW(), 0),
('20260014', '123456', '沈宇', '13800010014', NULL, NULL, '外语学院', 'teacher', 'active', NOW(), NOW(), 0),
('20260015', '123456', '韩露', '13800010015', NULL, NULL, '体育部', 'teacher', 'active', NOW(), NOW(), 0),
('20260016', '123456', '杨帆', '13800010016', NULL, NULL, '后勤保障部', 'staff', 'active', NOW(), NOW(), 0),
('20260017', '123456', '朱颖', '13800010017', NULL, NULL, '后勤保障部', 'staff', 'active', NOW(), NOW(), 0),
('20260018', '123456', '秦凯', '13800010018', NULL, NULL, '保卫处', 'staff', 'active', NOW(), NOW(), 0),
('20260019', '123456', '尤静', '13800010019', NULL, NULL, '保卫处', 'staff', 'active', NOW(), NOW(), 0),
('20260020', '123456', '许博', '13800010020', NULL, NULL, '资产管理处', 'staff', 'active', NOW(), NOW(), 0);

-- 2. 管理员（20条）
INSERT INTO sys_admin (username, password, name, phone, role, department, create_time, deleted) VALUES
('kunliu', '123456', '刘坤', '13900020001', 'super_admin', '保卫处', NOW(), 0),
('admin002', '123456', '王卓', '13900020002', 'admin', '保卫处', NOW(), 0),
('admin003', '123456', '李敏', '13900020003', 'admin', '保卫处', NOW(), 0),
('admin004', '123456', '赵雷', '13900020004', 'admin', '保卫处', NOW(), 0),
('admin005', '123456', '孙磊', '13900020005', 'admin', '保卫处', NOW(), 0),
('admin006', '123456', '周璐', '13900020006', 'admin', '信息中心', NOW(), 0),
('admin007', '123456', '吴涛', '13900020007', 'admin', '信息中心', NOW(), 0),
('admin008', '123456', '郑凯', '13900020008', 'admin', '信息中心', NOW(), 0),
('admin009', '123456', '冯雪', '13900020009', 'admin', '信息中心', NOW(), 0),
('admin010', '123456', '陈浩', '13900020010', 'admin', '信息中心', NOW(), 0),
('admin011', '123456', '褚琳', '13900020011', 'admin', '学生处', NOW(), 0),
('admin012', '123456', '卫峰', '13900020012', 'admin', '学生处', NOW(), 0),
('admin013', '123456', '蒋鹏', '13900020013', 'admin', '学生处', NOW(), 0),
('admin014', '123456', '沈倩', '13900020014', 'admin', '学生处', NOW(), 0),
('admin015', '123456', '韩磊', '13900020015', 'admin', '学生处', NOW(), 0),
('admin016', '123456', '杨静', '13900020016', 'admin', '后勤处', NOW(), 0),
('admin017', '123456', '朱明', '13900020017', 'admin', '后勤处', NOW(), 0),
('admin018', '123456', '秦雯', '13900020018', 'admin', '后勤处', NOW(), 0),
('admin019', '123456', '尤昊', '13900020019', 'admin', '后勤处', NOW(), 0),
('admin020', '123456', '许晴', '13900020020', 'admin', '后勤处', NOW(), 0);

-- 3. 电子围栏（20条）
INSERT INTO biz_fence (name, range_desc, coordinates, status, create_time) VALUES
('北门主通道围栏', '北门车辆主出入口', '[{"lat":34.2226,"lng":117.1486},{"lat":34.2219,"lng":117.1494},{"lat":34.2213,"lng":117.1486},{"lat":34.2219,"lng":117.1478}]', 'active', NOW()),
('东门慢行管控区', '东门周边慢行区域', '[{"lat":34.2203,"lng":117.1495},{"lat":34.2195,"lng":117.1502},{"lat":34.2188,"lng":117.1495},{"lat":34.2195,"lng":117.1488}]', 'active', NOW()),
('西门秩序管控区', '西门出入高峰区域', '[{"lat":34.2151,"lng":117.1335},{"lat":34.2144,"lng":117.1342},{"lat":34.2138,"lng":117.1335},{"lat":34.2144,"lng":117.1328}]', 'active', NOW()),
('南门缓行区域', '南门非机动车通道', '[{"lat":34.2096,"lng":117.1422},{"lat":34.2089,"lng":117.1430},{"lat":34.2082,"lng":117.1422},{"lat":34.2089,"lng":117.1414}]', 'active', NOW()),
('图书馆广场禁停区', '图书馆北侧广场', '[{"lat":34.2167,"lng":117.1425},{"lat":34.2161,"lng":117.1432},{"lat":34.2155,"lng":117.1425},{"lat":34.2161,"lng":117.1418}]', 'active', NOW()),
('教学楼A区围栏', '教学楼A区出入口', '[{"lat":34.2184,"lng":117.1384},{"lat":34.2178,"lng":117.1390},{"lat":34.2172,"lng":117.1384},{"lat":34.2178,"lng":117.1378}]', 'active', NOW()),
('教学楼核心区围栏', '教学楼群核心慢行区域', '[{"lat":34.2176,"lng":117.1398},{"lat":34.2170,"lng":117.1405},{"lat":34.2164,"lng":117.1398},{"lat":34.2170,"lng":117.1391}]', 'active', NOW()),
('宿舍区夜间管控区', '宿舍区主路夜间限速', '[{"lat":34.2104,"lng":117.1356},{"lat":34.2098,"lng":117.1363},{"lat":34.2092,"lng":117.1356},{"lat":34.2098,"lng":117.1349}]', 'active', NOW()),
('体育馆周边围栏', '体育馆南北两侧车道', '[{"lat":34.2124,"lng":117.1453},{"lat":34.2118,"lng":117.1460},{"lat":34.2112,"lng":117.1453},{"lat":34.2118,"lng":117.1446}]', 'active', NOW()),
('食堂通道管控区', '食堂门前通道', '[{"lat":34.2124,"lng":117.1363},{"lat":34.2118,"lng":117.1370},{"lat":34.2112,"lng":117.1363},{"lat":34.2118,"lng":117.1356}]', 'active', NOW()),
('行政楼慢行示范区', '行政楼门前道路', '[{"lat":34.2143,"lng":117.1405},{"lat":34.2137,"lng":117.1412},{"lat":34.2131,"lng":117.1405},{"lat":34.2137,"lng":117.1398}]', 'active', NOW()),
('主干道巡检围栏', '校园主干道东段', '[{"lat":34.2157,"lng":117.1445},{"lat":34.2151,"lng":117.1452},{"lat":34.2145,"lng":117.1445},{"lat":34.2151,"lng":117.1438}]', 'active', NOW()),
('实验楼北侧围栏', '实验楼北侧行车道', '[{"lat":34.2140,"lng":117.1436},{"lat":34.2134,"lng":117.1443},{"lat":34.2128,"lng":117.1436},{"lat":34.2134,"lng":117.1429}]', 'active', NOW()),
('图书馆西侧禁停区', '图书馆西侧步行口', '[{"lat":34.2161,"lng":117.1417},{"lat":34.2155,"lng":117.1423},{"lat":34.2149,"lng":117.1417},{"lat":34.2155,"lng":117.1411}]', 'active', NOW()),
('校医院应急通道围栏', '校医院急救通道口', '[{"lat":34.2144,"lng":117.1466},{"lat":34.2138,"lng":117.1472},{"lat":34.2132,"lng":117.1466},{"lat":34.2138,"lng":117.1460}]', 'active', NOW()),
('学生活动中心围栏', '学生活动中心周边', '[{"lat":34.2134,"lng":117.1377},{"lat":34.2128,"lng":117.1383},{"lat":34.2122,"lng":117.1377},{"lat":34.2128,"lng":117.1371}]', 'active', NOW()),
('北区支路限速区', '北区支路口', '[{"lat":34.2214,"lng":117.1468},{"lat":34.2208,"lng":117.1474},{"lat":34.2202,"lng":117.1468},{"lat":34.2208,"lng":117.1462}]', 'inactive', NOW()),
('东区教学带围栏', '东区教学楼连接带', '[{"lat":34.2189,"lng":117.1478},{"lat":34.2183,"lng":117.1484},{"lat":34.2177,"lng":117.1478},{"lat":34.2183,"lng":117.1472}]', 'active', NOW()),
('宿舍东路围栏', '宿舍东路行车道', '[{"lat":34.2100,"lng":117.1368},{"lat":34.2094,"lng":117.1374},{"lat":34.2088,"lng":117.1368},{"lat":34.2094,"lng":117.1362}]', 'active', NOW()),
('体育馆西路围栏', '体育馆西路口', '[{"lat":34.2120,"lng":117.1445},{"lat":34.2114,"lng":117.1451},{"lat":34.2108,"lng":117.1445},{"lat":34.2114,"lng":117.1439}]', 'active', NOW());

-- 4. 违规记录（20条）
INSERT INTO biz_violation (user_id, user_name, violation_time, place, type, detail, penalty, status, evidence_images, can_appeal, appeal_status, create_time, deleted) VALUES
(1, '张晨', DATE_SUB(NOW(), INTERVAL 1 HOUR), '北门', 'speeding', '北门主路限速15公里每小时，系统检测速度22公里每小时', '警告', 'pending', '[]', 1, 'pending', DATE_SUB(NOW(), INTERVAL 1 HOUR), 0),
(2, '李航', DATE_SUB(NOW(), INTERVAL 2 HOUR), '东门', 'illegal_parking', '东门非停车区临停超过10分钟，影响通行', '警告', 'processed', '[]', 1, 'none', DATE_SUB(NOW(), INTERVAL 2 HOUR), 0),
(3, '王宁', DATE_SUB(NOW(), INTERVAL 3 HOUR), '西门', 'wrong_way', '西门进出口逆向行驶，存在会车风险', '通报批评', 'pending', '[]', 1, 'none', DATE_SUB(NOW(), INTERVAL 3 HOUR), 0),
(4, '赵悦', DATE_SUB(NOW(), INTERVAL 5 HOUR), '南门', 'red_light', '南门路口未按信号灯通行', '警告', 'processed', '[]', 1, 'approved', DATE_SUB(NOW(), INTERVAL 5 HOUR), 0),
(5, '孙杰', DATE_SUB(NOW(), INTERVAL 7 HOUR), '图书馆广场', 'illegal_parking', '图书馆广场消防通道违停', '警告', 'processed', '[]', 1, 'rejected', DATE_SUB(NOW(), INTERVAL 7 HOUR), 0),
(6, '周晴', DATE_SUB(NOW(), INTERVAL 9 HOUR), '教学楼A区', 'speeding', '教学楼A区限速路段超速', '警告', 'pending', '[]', 1, 'pending', DATE_SUB(NOW(), INTERVAL 9 HOUR), 0),
(7, '吴昊', DATE_SUB(NOW(), INTERVAL 11 HOUR), '教学楼', 'wrong_way', '教学楼主道逆行', '通报批评', 'processed', '[]', 1, 'none', DATE_SUB(NOW(), INTERVAL 11 HOUR), 0),
(8, '郑琳', DATE_SUB(NOW(), INTERVAL 13 HOUR), '宿舍区', 'illegal_parking', '宿舍区楼栋门前长时间违停', '警告', 'pending', '[]', 1, 'none', DATE_SUB(NOW(), INTERVAL 13 HOUR), 0),
(9, '冯涛', DATE_SUB(NOW(), INTERVAL 16 HOUR), '体育馆', 'speeding', '体育馆西侧道路超速行驶', '警告', 'processed', '[]', 1, 'approved', DATE_SUB(NOW(), INTERVAL 16 HOUR), 0),
(10, '陈楠', DATE_SUB(NOW(), INTERVAL 19 HOUR), '食堂', 'red_light', '食堂路口闯红灯', '记过', 'pending', '[]', 0, 'none', DATE_SUB(NOW(), INTERVAL 19 HOUR), 0),
(11, '褚敏', DATE_SUB(NOW(), INTERVAL 1 DAY), '行政楼', 'illegal_parking', '行政楼门前违停阻塞通道', '警告', 'processed', '[]', 1, 'none', DATE_SUB(NOW(), INTERVAL 1 DAY), 0),
(12, '卫杰', DATE_SUB(NOW(), INTERVAL 27 HOUR), '主干道', 'speeding', '主干道测速超限', '警告', 'processed', '[]', 1, 'none', DATE_SUB(NOW(), INTERVAL 27 HOUR), 0),
(13, '蒋欣', DATE_SUB(NOW(), INTERVAL 30 HOUR), '实验楼', 'wrong_way', '实验楼北侧支路逆行', '通报批评', 'pending', '[]', 1, 'pending', DATE_SUB(NOW(), INTERVAL 30 HOUR), 0),
(14, '沈宇', DATE_SUB(NOW(), INTERVAL 2 DAY), '图书馆', 'red_light', '图书馆南口闯红灯', '警告', 'processed', '[]', 1, 'approved', DATE_SUB(NOW(), INTERVAL 2 DAY), 0),
(15, '韩露', DATE_SUB(NOW(), INTERVAL 52 HOUR), '校医院', 'illegal_parking', '校医院应急通道违停', '警告', 'processed', '[]', 1, 'rejected', DATE_SUB(NOW(), INTERVAL 52 HOUR), 0),
(16, '杨帆', DATE_SUB(NOW(), INTERVAL 56 HOUR), '学生活动中心', 'speeding', '学生活动中心周边超速', '警告', 'pending', '[]', 1, 'none', DATE_SUB(NOW(), INTERVAL 56 HOUR), 0),
(17, '朱颖', DATE_SUB(NOW(), INTERVAL 3 DAY), '北门', 'wrong_way', '北门环道逆行', '通报批评', 'processed', '[]', 1, 'none', DATE_SUB(NOW(), INTERVAL 3 DAY), 0),
(18, '秦凯', DATE_SUB(NOW(), INTERVAL 76 HOUR), '宿舍区', 'red_light', '宿舍区路口闯红灯', '警告', 'pending', '[]', 1, 'pending', DATE_SUB(NOW(), INTERVAL 76 HOUR), 0),
(19, '尤静', DATE_SUB(NOW(), INTERVAL 4 DAY), '图书馆广场', 'speeding', '图书馆广场周边超速', '警告', 'processed', '[]', 1, 'approved', DATE_SUB(NOW(), INTERVAL 4 DAY), 0),
(20, '许博', DATE_SUB(NOW(), INTERVAL 5 DAY), '主干道', 'illegal_parking', '主干道临时违停影响通行', '警告', 'pending', '[]', 1, 'pending', DATE_SUB(NOW(), INTERVAL 5 DAY), 0);

-- 5. 申诉记录（20条）
INSERT INTO biz_appeal (violation_id, user_id, appeal_number, platform, reason, description, contact_phone, evidence_files, status, reply_content, handler_id, handle_time, create_time) VALUES
(1, 1, 'AP202604060001', 'web', '事实不符', '当时正在避让行人，速度短时上升后已及时减速。', '13800010001', '[]', 'pending', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(2, 2, 'AP202604060002', 'miniapp', '临时停靠', '接送同学就医，停车时间较短。', '13800010002', '[]', 'approved', '核查监控后确认情况属实，予以撤销。', 1, DATE_SUB(NOW(), INTERVAL 20 MINUTE), DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(3, 3, 'AP202604060003', 'web', '识别偏差', '夜间光线较暗，车牌识别存在误差。', '13800010003', '[]', 'rejected', '视频清晰可识别本人车辆，维持原处理。', 2, DATE_SUB(NOW(), INTERVAL 50 MINUTE), DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(4, 4, 'AP202604060004', 'web', '紧急情况', '接到紧急通知需快速前往实验室处理故障。', '13800010004', '[]', 'approved', '经学院证明属实，予以通过。', 1, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(5, 5, 'AP202604060005', 'miniapp', '证据不足', '现场照片角度不足以证明占用消防通道。', '13800010005', '[]', 'rejected', '现场多角度照片完整，驳回申诉。', 3, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(6, 6, 'AP202604060006', 'web', '事实不符', '测速点位与行驶车道存在偏移。', '13800010006', '[]', 'pending', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(7, 7, 'AP202604060007', 'miniapp', '标识不清', '道路提示标识被遮挡未能及时识别。', '13800010007', '[]', 'pending', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 7 HOUR)),
(8, 8, 'AP202604060008', 'web', '临时停靠', '外卖配送临时停靠不足五分钟。', '13800010008', '[]', 'approved', '核验后确属短时停靠，予以提醒教育。', 4, DATE_SUB(NOW(), INTERVAL 8 HOUR), DATE_SUB(NOW(), INTERVAL 10 HOUR)),
(9, 9, 'AP202604060009', 'web', '紧急情况', '雨天刹车距离增加导致超速预警。', '13800010009', '[]', 'approved', '考虑天气因素，减轻处理。', 2, DATE_SUB(NOW(), INTERVAL 9 HOUR), DATE_SUB(NOW(), INTERVAL 12 HOUR)),
(10, 10, 'AP202604060010', 'miniapp', '事实不符', '我已在黄灯前减速，非故意闯灯。', '13800010010', '[]', 'pending', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 11 HOUR)),
(11, 11, 'AP202604060011', 'web', '临时停靠', '行政楼门前接送设备，时间较短。', '13800010011', '[]', 'rejected', '停车超过规定时长，驳回。', 5, DATE_SUB(NOW(), INTERVAL 13 HOUR), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(12, 12, 'AP202604060012', 'miniapp', '识别偏差', '测速点受遮挡导致结果偏高。', '13800010012', '[]', 'approved', '经复核后采信申诉。', 1, DATE_SUB(NOW(), INTERVAL 15 HOUR), DATE_SUB(NOW(), INTERVAL 26 HOUR)),
(13, 13, 'AP202604060013', 'web', '事实不符', '当时在避让行人，短时逆行后立即纠正。', '13800010013', '[]', 'pending', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 30 HOUR)),
(14, 14, 'AP202604060014', 'web', '证据不足', '监控画面存在遮挡，无法完整还原。', '13800010014', '[]', 'approved', '根据补充证据通过申诉。', 3, DATE_SUB(NOW(), INTERVAL 32 HOUR), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(15, 15, 'AP202604060015', 'miniapp', '紧急情况', '送同学就医临时占用应急通道。', '13800010015', '[]', 'rejected', '应急通道不得占用，驳回。', 4, DATE_SUB(NOW(), INTERVAL 50 HOUR), DATE_SUB(NOW(), INTERVAL 52 HOUR)),
(16, 16, 'AP202604060016', 'web', '标识不清', '活动中心周边临时施工，标识不明显。', '13800010016', '[]', 'pending', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 56 HOUR)),
(17, 17, 'AP202604060017', 'miniapp', '事实不符', '逆行识别发生在临时绕行路段。', '13800010017', '[]', 'approved', '经现场核查，予以通过。', 2, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 73 HOUR)),
(18, 18, 'AP202604060018', 'web', '紧急情况', '晚间值班任务导致通过路口较急。', '13800010018', '[]', 'pending', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 76 HOUR)),
(19, 19, 'AP202604060019', 'miniapp', '识别偏差', '速度识别可能叠加了旁车轨迹。', '13800010019', '[]', 'approved', '复核后确认识别偏差，已撤销。', 1, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 98 HOUR)),
(20, 20, 'AP202604060020', 'web', '临时停靠', '临时搬运实验设备，停留时间较短。', '13800010020', '[]', 'pending', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 5 DAY));

-- 6. 轨迹记录（20条）
INSERT INTO biz_track (user_id, date, start_time, end_time, distance, track_points, create_time) VALUES
(1, CURDATE(), DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR), 3.20, '[{"time":"2026-04-06T08:10:00","lat":34.2218,"lng":117.1488,"speed":16},{"time":"2026-04-06T08:20:00","lat":34.2199,"lng":117.1468,"speed":18},{"time":"2026-04-06T08:30:00","lat":34.2178,"lng":117.1430,"speed":14}]', NOW()),
(2, CURDATE(), DATE_SUB(NOW(), INTERVAL 4 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR), 2.80, '[{"time":"2026-04-06T09:00:00","lat":34.2200,"lng":117.1492,"speed":14},{"time":"2026-04-06T09:10:00","lat":34.2185,"lng":117.1479,"speed":17},{"time":"2026-04-06T09:20:00","lat":34.2162,"lng":117.1422,"speed":15}]', NOW()),
(3, CURDATE(), DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 4 HOUR), 4.10, '[{"time":"2026-04-06T10:00:00","lat":34.2148,"lng":117.1338,"speed":13},{"time":"2026-04-06T10:15:00","lat":34.2156,"lng":117.1380,"speed":19},{"time":"2026-04-06T10:30:00","lat":34.2168,"lng":117.1421,"speed":16}]', NOW()),
(4, CURDATE(), DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR), 3.60, '[{"time":"2026-04-06T11:00:00","lat":34.2090,"lng":117.1420,"speed":12},{"time":"2026-04-06T11:15:00","lat":34.2112,"lng":117.1438,"speed":16},{"time":"2026-04-06T11:30:00","lat":34.2140,"lng":117.1460,"speed":15}]', NOW()),
(5, CURDATE(), DATE_SUB(NOW(), INTERVAL 7 HOUR), DATE_SUB(NOW(), INTERVAL 6 HOUR), 2.50, '[{"time":"2026-04-06T12:00:00","lat":34.2160,"lng":117.1424,"speed":11},{"time":"2026-04-06T12:10:00","lat":34.2169,"lng":117.1405,"speed":13},{"time":"2026-04-06T12:20:00","lat":34.2179,"lng":117.1384,"speed":14}]', NOW()),
(6, CURDATE(), DATE_SUB(NOW(), INTERVAL 8 HOUR), DATE_SUB(NOW(), INTERVAL 7 HOUR), 3.00, '[{"time":"2026-04-06T13:00:00","lat":34.2182,"lng":117.1383,"speed":15},{"time":"2026-04-06T13:10:00","lat":34.2173,"lng":117.1399,"speed":17},{"time":"2026-04-06T13:20:00","lat":34.2158,"lng":117.1443,"speed":16}]', NOW()),
(7, CURDATE(), DATE_SUB(NOW(), INTERVAL 9 HOUR), DATE_SUB(NOW(), INTERVAL 8 HOUR), 2.70, '[{"time":"2026-04-06T14:00:00","lat":34.2172,"lng":117.1397,"speed":14},{"time":"2026-04-06T14:10:00","lat":34.2152,"lng":117.1410,"speed":15},{"time":"2026-04-06T14:20:00","lat":34.2129,"lng":117.1432,"speed":16}]', NOW()),
(8, CURDATE(), DATE_SUB(NOW(), INTERVAL 10 HOUR), DATE_SUB(NOW(), INTERVAL 9 HOUR), 2.40, '[{"time":"2026-04-06T15:00:00","lat":34.2100,"lng":117.1355,"speed":12},{"time":"2026-04-06T15:10:00","lat":34.2112,"lng":117.1362,"speed":14},{"time":"2026-04-06T15:20:00","lat":34.2137,"lng":117.1404,"speed":15}]', NOW()),
(9, CURDATE(), DATE_SUB(NOW(), INTERVAL 11 HOUR), DATE_SUB(NOW(), INTERVAL 10 HOUR), 3.30, '[{"time":"2026-04-06T16:00:00","lat":34.2121,"lng":117.1452,"speed":15},{"time":"2026-04-06T16:10:00","lat":34.2138,"lng":117.1464,"speed":18},{"time":"2026-04-06T16:20:00","lat":34.2158,"lng":117.1446,"speed":16}]', NOW()),
(10, CURDATE(), DATE_SUB(NOW(), INTERVAL 12 HOUR), DATE_SUB(NOW(), INTERVAL 11 HOUR), 2.20, '[{"time":"2026-04-06T17:00:00","lat":34.2120,"lng":117.1364,"speed":13},{"time":"2026-04-06T17:10:00","lat":34.2136,"lng":117.1398,"speed":15},{"time":"2026-04-06T17:20:00","lat":34.2149,"lng":117.1435,"speed":14}]', NOW()),
(11, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 26 HOUR), DATE_SUB(NOW(), INTERVAL 25 HOUR), 3.60, '[{"time":"2026-04-05T08:20:00","lat":34.2140,"lng":117.1404,"speed":14},{"time":"2026-04-05T08:35:00","lat":34.2156,"lng":117.1422,"speed":15},{"time":"2026-04-05T08:50:00","lat":34.2174,"lng":117.1390,"speed":16}]', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(12, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 27 HOUR), DATE_SUB(NOW(), INTERVAL 26 HOUR), 3.10, '[{"time":"2026-04-05T09:10:00","lat":34.2157,"lng":117.1446,"speed":15},{"time":"2026-04-05T09:25:00","lat":34.2142,"lng":117.1463,"speed":14},{"time":"2026-04-05T09:40:00","lat":34.2130,"lng":117.1378,"speed":16}]', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(13, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 28 HOUR), DATE_SUB(NOW(), INTERVAL 27 HOUR), 2.90, '[{"time":"2026-04-05T10:10:00","lat":34.2132,"lng":117.1434,"speed":14},{"time":"2026-04-05T10:20:00","lat":34.2150,"lng":117.1444,"speed":16},{"time":"2026-04-05T10:30:00","lat":34.2160,"lng":117.1421,"speed":15}]', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(14, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 50 HOUR), DATE_SUB(NOW(), INTERVAL 49 HOUR), 3.40, '[{"time":"2026-04-04T08:10:00","lat":34.2156,"lng":117.1416,"speed":13},{"time":"2026-04-04T08:25:00","lat":34.2144,"lng":117.1465,"speed":15},{"time":"2026-04-04T08:40:00","lat":34.2129,"lng":117.1450,"speed":14}]', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(15, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 51 HOUR), DATE_SUB(NOW(), INTERVAL 50 HOUR), 2.60, '[{"time":"2026-04-04T09:10:00","lat":34.2138,"lng":117.1462,"speed":12},{"time":"2026-04-04T09:20:00","lat":34.2150,"lng":117.1447,"speed":14},{"time":"2026-04-04T09:35:00","lat":34.2179,"lng":117.1382,"speed":16}]', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(16, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 52 HOUR), DATE_SUB(NOW(), INTERVAL 51 HOUR), 2.80, '[{"time":"2026-04-04T10:10:00","lat":34.2128,"lng":117.1375,"speed":13},{"time":"2026-04-04T10:20:00","lat":34.2141,"lng":117.1402,"speed":14},{"time":"2026-04-04T10:35:00","lat":34.2158,"lng":117.1443,"speed":15}]', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(17, DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 74 HOUR), DATE_SUB(NOW(), INTERVAL 73 HOUR), 3.00, '[{"time":"2026-04-03T08:00:00","lat":34.2216,"lng":117.1487,"speed":14},{"time":"2026-04-03T08:15:00","lat":34.2198,"lng":117.1476,"speed":16},{"time":"2026-04-03T08:30:00","lat":34.2165,"lng":117.1423,"speed":15}]', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(18, DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 75 HOUR), DATE_SUB(NOW(), INTERVAL 74 HOUR), 2.70, '[{"time":"2026-04-03T09:10:00","lat":34.2102,"lng":117.1359,"speed":12},{"time":"2026-04-03T09:25:00","lat":34.2118,"lng":117.1367,"speed":14},{"time":"2026-04-03T09:40:00","lat":34.2140,"lng":117.1406,"speed":15}]', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(19, DATE_SUB(CURDATE(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 98 HOUR), DATE_SUB(NOW(), INTERVAL 97 HOUR), 2.50, '[{"time":"2026-04-02T08:10:00","lat":34.2162,"lng":117.1424,"speed":13},{"time":"2026-04-02T08:25:00","lat":34.2148,"lng":117.1439,"speed":15},{"time":"2026-04-02T08:40:00","lat":34.2120,"lng":117.1451,"speed":14}]', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(20, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 122 HOUR), DATE_SUB(NOW(), INTERVAL 121 HOUR), 3.20, '[{"time":"2026-04-01T08:10:00","lat":34.2159,"lng":117.1444,"speed":14},{"time":"2026-04-01T08:20:00","lat":34.2147,"lng":117.1427,"speed":16},{"time":"2026-04-01T08:35:00","lat":34.2136,"lng":117.1405,"speed":15}]', DATE_SUB(NOW(), INTERVAL 5 DAY));

SELECT '中文测试数据初始化完成（每张业务表不少于20条）' AS result;
