-- 秒杀执行存储过程
DELIMITER $$ -- console ; 转换为 $$
-- 定义存储过程
-- 参数：in 输入参数；out 输出参数
-- row_count() : 返回上一条修改类型sql（delete，insert，update）影响的行数
-- row_count() : 0：未修改数据 >0：修改的行数 <0：sql错误、未执行sql
create procedure seckill.execute_seckill(in v_seckill_id bigint, in v_phone bigint,
                                             in v_kill_time timestamp, out r_result int)
begin
    declare insert_count int default 0;
    start transaction ;
    insert ignore into success_killed
        (seckill_id, user_phone, create_time)
    VALUES (v_seckill_id, v_phone, v_kill_time);
    select row_count() into insert_count;
    if (insert_count = 0) then
        rollback;
        set r_result = -1;
    else
        if (insert_count < 0) then
            rollback;
            set r_result = -2;
        else
            update seckill
            set number = number - 1
            where seckill_id = v_seckill_id
              and end_time > v_kill_time
              and start_time < v_kill_time
              and number > 0;
            select ROW_COUNT() into insert_count;
            if (insert_count = 0) then
                rollback;
                set r_result = 0;
            elseif (insert_count < 0) then
                rollback;
                set r_result = -2;
            else
                commit;
                set r_result = 1;
            end if;
        end if;
    end if;
end;
$$ -- 定义结束
show create procedure execute_seckill ;

DELIMITER ;;
set @r_result = -3;
-- 执行存储过程
call seckill.execute_seckill(1002,13228217106,now(),@r_result);
-- 获取结果
select @r_result;

-- 存储过程
-- 1：存储过程优化：事务行级锁持有的时间
-- 2：不要过度的依赖存储过程
-- 3：简单的逻辑，可以应用存储过程