package com.snowalker.shardingjdbc.snowalker.demo.strategy;

import com.snowalker.shardingjdbc.snowalker.demo.utils.ShardKeyUtil;
import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;

import java.util.Collection;

public class OrderDbShardingAlgorithm  implements PreciseShardingAlgorithm<String> {

    @Override
    public String doSharding(Collection<String> availableTargetNames,
                             PreciseShardingValue<String> shardingValue) {

        String userId = shardingValue.getValue();

        if (userId == null || userId.length() < 2) {
            throw new IllegalArgumentException("invalid userId: " + userId);
        }

        // ⭐ 倒数第2位
        char dbChar = userId.charAt(userId.length() - 2);

        if (dbChar < '0' || dbChar > '9') {
            throw new IllegalArgumentException("userId last 2 char not digit: " + userId);
        }

        return "ds" + dbChar;
    }
}
