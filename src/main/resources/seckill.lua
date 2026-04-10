-- 参数说明：
-- KEYS[1] = 库存 key，如 seckill:stock:1
-- KEYS[2] = 已购用户集合 key，如 seckill:users:1
-- ARGV[1] = 用户ID

-- 1. 判断库存是否充足
-- local用于定义一个局部变量；redis.call表示要执行某个Redis指令
local stock = tonumber(redis.call('get', KEYS[1]))
-- nil就是null
if stock == nil or stock <= 0 then
    return -1  -- 库存不足
end

-- 2. 判断用户是否已经秒杀过
-- 判断ARGV[1]是不是在KEYS[2]里
local isMember = redis.call('sismember', KEYS[2], ARGV[1])
if isMember == 1 then
    return -2  -- 重复秒杀
end

-- 3. 扣库存
redis.call('decrby', KEYS[1], 1)

-- 4. 记录用户已秒杀
redis.call('sadd', KEYS[2], ARGV[1])

return 0  -- 秒杀成功