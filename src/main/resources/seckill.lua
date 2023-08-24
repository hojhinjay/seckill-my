local voucherId = ARGV[1]
local userId = ARGV[2]

local stockKey = 'seckillGoods:' .. voucherId
local orderKey = 'order:' .. userId .. ':' .. voucherId

if(tonumber(redis.call('get',stockKey)) <= 0) then
  return 1
end

if(redis.call('exists',orderKey) == 1) then
  return 2
end

redis.call('incrby',stockKey,-1)
return 0