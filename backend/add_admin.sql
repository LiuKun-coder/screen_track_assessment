-- 添加管理员账号
-- 用户名：kunliu
-- 密码：123456 (明文存储)

INSERT INTO sys_admin (username, password, name, phone, role, department, create_time, deleted)
VALUES ('kunliu', '123456', '刘坤', NULL, 'admin', '管理部门', NOW(), 0);
