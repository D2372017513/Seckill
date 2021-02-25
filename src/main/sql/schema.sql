-- 数据库初始化脚本
-- 创建数据库
CREATE DATABASE seckill;
-- 使用数据库
use seckill;
-- 创建秒杀数据库
-- 表名和字段名要用 `` 引号（esc下面），COMMENT 注释用 ‘’ ，默认字符 utf8 而不是 utf-8
-- 查看表如何创建的 show create table 表名
CREATE TABLE seckill
(
    `seckill_id`  bigint       NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
    `name`        varchar(120) NOT NULL COMMENT '商品名称',
    `number`      int          NOT NULL COMMENT '商品数量',
    `start_time`  timestamp    NOT NULL COMMENT '开始时间',
    `end_time`    timestamp    NOT NULL COMMENT '结束时间',
    `create_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (seckill_id),
    key idx_start_time (start_time),
    key idx_end_time (end_time),
    key idx_create_time (create_time)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000
  DEFAULT CHARSET = utf8 COMMENT ='秒杀库存表';

-- 初始化数据
insert into seckill (name, number, start_time, end_time)
values ('1000元秒杀IPhone 12', 100, '2021-01-18 00:00:00', '2021-01-19 00:00:00'),
       ('10元秒杀垃圾袋', 1000, '2021-01-16 00:40:00', '2021-01-19 00:00:00'),
       ('36元秒杀大米', 10000, '2021-01-17 08:00:00', '2021-01-20 00:00:00'),
       ('500元秒杀大会员', 999, '2021-01-18 10:00:00', '2021-01-25 00:00:00');

-- 秒杀成功明细表
-- 用户登录认证的相关信息
CREATE TABLE success_killed
(
    `seckill_id`  bigint    NOT NULL COMMENT '商品id',
    `user_phone`  bigint    NOT NULL COMMENT '用户手机号码',
    `state`       tinyint   NOT NULL DEFAULT -1 COMMENT '状态码: -1:无效 0：成功 1：已付款 2：已发货',
    `create_time` timestamp NOT NULL COMMENT '创建时间',
    PRIMARY KEY (seckill_id, user_phone), /* 联合主键，确保唯一性 */
    key idx_create_time (create_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='秒杀成功明细表';

-- 连接数据库
use seckill;
SELECT * FROM seckill;
show create table seckill;
use seckill;
select *
from success_killed;
delete from success_killed
where seckill_id = 1002;
alter table success_killed modify create_time timestamp default CURRENT_TIMESTAMP not null COMMENT '创建时间';
update seckill set end_time = '2021-02-25 08:00:00' where seckill_id= 1001