package com.fourweekdays.fourweekdays.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class RedissonConfig {

    @Value("${redis.sentinel.nodes}")
    private String sentinelNodes;

    @Value("${redis.sentinel.master}")
    private String masterName;

    @Value("${redis.password}")
    private String redisPassword;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        config.useSentinelServers()
                .addSentinelAddress(sentinelNodes.split(","))
                .setMasterName(masterName)
                .setPassword(redisPassword)
                .setDatabase(0);

        return Redisson.create(config);
    }
}
