package com.snowalker.shardingjdbc.snowalker.demo.strategy;

import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import java.util.Collection;

public class OrderTableShardingAlgorithm  implements PreciseShardingAlgorithm<String> {

    @Override
    public String doSharding(Collection<String> availableTargetNames,
                             PreciseShardingValue<String> shardingValue) {

        String userId = shardingValue.getValue();

        if (userId == null || userId.length() < 2) {
            throw new IllegalArgumentException("invalid userId: " + userId);
        }

        // ⭐ 最后两位直接作为表后缀
        String suffix = userId.substring(userId.length() - 2);

        return "t_order_" + suffix;
    }
}
