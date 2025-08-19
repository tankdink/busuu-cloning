package systems.bt.reconn.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.Arrays;

@Configuration
@Slf4j
public class RedisCacheConfig {

    @Value("${cache.redis.server}")
    private String[] redisServers;

    @Value("${cache.redis.db.qr:0}")
    private int database;

    @Value("${cache.redis.cluster:false}")
    private boolean isCluster;

    @Value("${cache.redis.connection-pool-size:64}")
    private int connectionPoolSize;

    @Value("${cache.redis.connection-minimum-idle-size:24}")
    private int connectionMinimumIdleSize;

    @Value("${cache.redis.subscription-connection-pool-size:8}")
    private int subscriptionConnectionPoolSize;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(ObjectMapper objectMapper) {
        if (redisServers == null || redisServers.length == 0) {
            throw new IllegalArgumentException("Redis servers must be configured!");
        }

        Config config = new Config();
        config.setCodec(new JsonJacksonCodec(objectMapper));

        if (isCluster) {
            ClusterServersConfig cluster = config.useClusterServers()
                    .addNodeAddress(Arrays.stream(redisServers).map(this::normalizeAddress).toArray(String[]::new))
                    .setMasterConnectionPoolSize(connectionPoolSize)
                    .setMasterConnectionMinimumIdleSize(connectionMinimumIdleSize)
                    .setSubscriptionConnectionPoolSize(subscriptionConnectionPoolSize);

            String password = extractPassword(redisServers[0]);
            if (password != null) {
                cluster.setPassword(password);
            }
        } else {
            String address = normalizeAddress(redisServers[0]);
            SingleServerConfig single = config.useSingleServer()
                    .setAddress(address)
                    .setDatabase(database)
                    .setConnectionPoolSize(connectionPoolSize)
                    .setConnectionMinimumIdleSize(connectionMinimumIdleSize)
                    .setSubscriptionConnectionPoolSize(subscriptionConnectionPoolSize);

            String password = extractPassword(redisServers[0]);
            if (password != null) {
                single.setPassword(password);
            }
        }

        log.info("RedissonClient initialized: servers={}, cluster={}, db={}",
                Arrays.toString(redisServers), isCluster, database);

        return Redisson.create(config);
    }

    private String normalizeAddress(String address) {
        if (!address.startsWith("redis://") && !address.startsWith("rediss://")) {
            return "redis://" + address;
        }
        return address;
    }

    private String extractPassword(String uri) {
        try {
            URI parsed = URI.create(uri);
            if (parsed.getUserInfo() != null && parsed.getUserInfo().contains(":")) {
                return parsed.getUserInfo().split(":", 2)[1];
            }
        } catch (Exception e) {
            log.error("Failed to extract password from URI [{}]", uri, e);
        }
        return null;
    }
}
