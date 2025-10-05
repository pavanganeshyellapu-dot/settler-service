package com.settler.infra.cache;

import org.springframework.stereotype.Service;

@Service
public class CacheEvictService {

    private final RedisAdapter redis;

    public CacheEvictService(RedisAdapter redis) {
        this.redis = redis;
    }

    public void evictUser(String userId) {
        redis.evict(CacheKeyUtil.userKey(userId));
    }

    public void evictGroup(String groupId) {
        redis.evict(CacheKeyUtil.groupKey(groupId));
        redis.evict(CacheKeyUtil.groupBalanceKey(groupId));
    }

    public void evictExpense(String expenseId) {
        redis.evict(CacheKeyUtil.expenseKey(expenseId));
    }
}
