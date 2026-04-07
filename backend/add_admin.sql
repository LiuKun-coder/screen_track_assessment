-- 添加管理员账号（可重复执行）
-- 用户名：kunliu
-- 密码：123456 (明文存储)

INSERT INTO sys_admin (username, password, name, phone, role, department, create_time, deleted)
VALUES ('kunliu', '123456', '刘坤', NULL, 'super_admin', '保卫处', NOW(), 0)
ON DUPLICATE KEY UPDATE
	password = VALUES(password),
	name = VALUES(name),
	role = VALUES(role),
	department = VALUES(department),
	deleted = 0;
