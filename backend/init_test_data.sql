-- Test Data Initialization Script
-- Excluding sys_user table

-- 1. biz_fence (Electronic Fence) - 4 records
INSERT INTO biz_fence (name, range_desc, coordinates, status, create_time) VALUES
('Main Campus No-Go Zone', 'Library to Teaching Building A', '[{"lat":30.5728,"lng":104.0668},{"lat":30.5735,"lng":104.0668},{"lat":30.5735,"lng":104.0678},{"lat":30.5728,"lng":104.0678}]', 'active', NOW()),
('Student Dormitory Area', 'East Area Dorms 1-5', '[{"lat":30.5750,"lng":104.0700},{"lat":30.5760,"lng":104.0700},{"lat":30.5760,"lng":104.0720},{"lat":30.5750,"lng":104.0720}]', 'active', NOW()),
('Sports Field Area', 'Track and Basketball Courts', '[{"lat":30.5700,"lng":104.0650},{"lat":30.5715,"lng":104.0650},{"lat":30.5715,"lng":104.0670},{"lat":30.5700,"lng":104.0670}]', 'active', NOW()),
('Cafeteria Zone', 'Cafeteria 1 to Cafeteria 2', '[{"lat":30.5740,"lng":104.0680},{"lat":30.5748,"lng":104.0680},{"lat":30.5748,"lng":104.0695},{"lat":30.5740,"lng":104.0695}]', 'inactive', NOW());

-- 2. biz_violation - 5 records
INSERT INTO biz_violation (user_id, user_name, violation_time, place, type, detail, penalty, status, evidence_images, can_appeal, appeal_status, create_time, deleted) VALUES
(1, 'Zhang San', DATE_SUB(NOW(), INTERVAL 2 DAY), 'Library Plaza', 'Speeding', 'E-bike speed exceeded 25km/h, measured at 32km/h', 'Warning', 'processed', '["https://example.com/evidence/img001.jpg"]', 1, 'none', DATE_SUB(NOW(), INTERVAL 2 DAY), 0),
(2, 'Li Si', DATE_SUB(NOW(), INTERVAL 1 DAY), 'Teaching Building A Entrance', 'Illegal Parking', 'Parked in no-parking zone, blocking traffic', 'Warning', 'processed', '["https://example.com/evidence/img002.jpg"]', 1, 'pending', DATE_SUB(NOW(), INTERVAL 1 DAY), 0),
(3, 'Wang Wu', DATE_SUB(NOW(), INTERVAL 12 HOUR), 'Main Road', 'Wrong Way', 'Riding against traffic on main campus road', 'Verbal Warning', 'pending', '["https://example.com/evidence/img004.jpg"]', 1, 'none', DATE_SUB(NOW(), INTERVAL 12 HOUR), 0),
(1, 'Zhang San', DATE_SUB(NOW(), INTERVAL 6 HOUR), 'Campus Gate', 'Red Light', 'Ignored traffic signal and ran red light', 'Demerit', 'pending', '["https://example.com/evidence/img005.jpg"]', 1, 'none', DATE_SUB(NOW(), INTERVAL 6 HOUR), 0),
(2, 'Li Si', DATE_SUB(NOW(), INTERVAL 3 HOUR), 'Dormitory Area', 'Speeding', 'Speed exceeded limit in dorm area, measured 28km/h', 'Warning', 'pending', '["https://example.com/evidence/img007.jpg"]', 0, 'none', DATE_SUB(NOW(), INTERVAL 3 HOUR), 0);

-- 3. biz_appeal - 3 records
INSERT INTO biz_appeal (violation_id, user_id, appeal_number, platform, reason, description, contact_phone, evidence_files, status, reply_content, handler_id, handle_time, create_time) VALUES
(2, 2, 'AP2026020001', 'web', 'Factual Error', 'My e-bike was parked in the designated area. The camera angle may have caused misjudgment.', '13800138001', '["https://example.com/appeal/evidence001.jpg"]', 'pending', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 1, 'AP2026020002', 'miniapp', 'Emergency', 'Had an emergency and needed to rush to the infirmary. Please consider the circumstances.', '13900139002', '["https://example.com/appeal/evidence002.jpg"]', 'approved', 'Verified emergency situation. Penalty revoked. Please drive safely in the future.', 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 3, 'AP2026020003', 'web', 'Insufficient Evidence', 'The surveillance footage is unclear. Cannot accurately identify if it was me or my vehicle.', '13700137003', '[]', 'rejected', 'Multi-angle surveillance confirmed identity. Appeal rejected.', 1, DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 12 HOUR));

-- 4. biz_track - 4 records
INSERT INTO biz_track (user_id, date, start_time, end_time, distance, track_points, create_time) VALUES
(1, CURDATE(), DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR), 3.50, 
'[{"time":"2026-02-07T17:20:00","lat":30.5728,"lng":104.0668,"speed":15},{"time":"2026-02-07T17:25:00","lat":30.5735,"lng":104.0675,"speed":18},{"time":"2026-02-07T17:30:00","lat":30.5740,"lng":104.0680,"speed":12}]', 
NOW()),
(2, CURDATE(), DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 4 HOUR), 5.20,
'[{"time":"2026-02-07T15:20:00","lat":30.5700,"lng":104.0650,"speed":22},{"time":"2026-02-07T15:30:00","lat":30.5720,"lng":104.0665,"speed":25},{"time":"2026-02-07T15:40:00","lat":30.5735,"lng":104.0680,"speed":18}]',
NOW()),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 27 HOUR), DATE_SUB(NOW(), INTERVAL 26 HOUR), 2.80,
'[{"time":"2026-02-06T17:20:00","lat":30.5745,"lng":104.0685,"speed":10},{"time":"2026-02-06T17:30:00","lat":30.5752,"lng":104.0692,"speed":15},{"time":"2026-02-06T17:40:00","lat":30.5758,"lng":104.0700,"speed":18}]',
DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, CURDATE(), DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 30 MINUTE), 1.50,
'[{"time":"2026-02-07T19:20:00","lat":30.5730,"lng":104.0660,"speed":12},{"time":"2026-02-07T19:30:00","lat":30.5738,"lng":104.0668,"speed":16},{"time":"2026-02-07T19:40:00","lat":30.5745,"lng":104.0675,"speed":14}]',
NOW());

SELECT 'Test data initialized successfully!' AS result;
