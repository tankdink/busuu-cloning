package com.busuu.app.configs;

import com.busuu.app.entities.CacheKey;
import com.busuu.app.entities.Chapter;
import com.busuu.app.entities.Language;
import com.busuu.app.entities.Lesson;
import com.busuu.app.entities.Level;
import com.busuu.app.entities.Role;
import com.busuu.app.entities.Word;
import org.hibernate.cache.jcache.ConfigSettings;
import org.redisson.Redisson;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.net.URI;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;

    @Value("${cache.redis.server}")
    private String[] redisServer;

    @Value("${cache.redis.db}")
    private int db;

    @Value("${cache.redis.cluster}")
    private boolean isCluster = false;

    @Value("${cache.redis.expiration}")
    private Long expiration;

    @Value("${cache.redis.connection-pool-size}")
    private int connectionPoolSize;

    @Value("${cache.redis.connection-minimum-idle-size}")
    private int connectionMinimumIdleSize;

    @Value("${cache.redis.subscription-connection-pool-size}")
    private int subscriptionConnectionPoolSize;

    @Bean
    public javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration() {
        MutableConfiguration<Object, Object> jcacheConfig = new MutableConfiguration<>();

        URI redisUri = URI.create(redisServer[0]);

        Config config = new Config();
        config.setCodec(new org.redisson.codec.SerializationCodec());
        if (isCluster) {
            ClusterServersConfig clusterServersConfig = config
                    .useClusterServers()
                    .setMasterConnectionPoolSize(connectionPoolSize)
                    .setMasterConnectionMinimumIdleSize(connectionMinimumIdleSize)
                    .setSubscriptionConnectionPoolSize(subscriptionConnectionPoolSize)
                    .addNodeAddress(redisServer);

            if (redisUri.getUserInfo() != null) {
                clusterServersConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(':') + 1));
            }
        } else {
            SingleServerConfig singleServerConfig = config
                    .useSingleServer()
                    .setDatabase(db)
                    .setConnectionPoolSize(connectionPoolSize)
                    .setConnectionMinimumIdleSize(connectionMinimumIdleSize)
                    .setSubscriptionConnectionPoolSize(subscriptionConnectionPoolSize)
                    .setAddress(redisServer[0]);

            if (redisUri.getUserInfo() != null) {
                singleServerConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(':') + 1));
            }
        }
        jcacheConfig.setStatisticsEnabled(true);
        jcacheConfig.setExpiryPolicyFactory(
                CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, expiration))
        );
        return RedissonConfiguration.fromInstance(Redisson.create(config), jcacheConfig);
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cm) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cm);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer(javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration) {
        return cm -> {
            createCache(cm, "default-update-timestamps-region", jcacheConfiguration);
            createCache(cm, "default-query-results-region", jcacheConfiguration);

            // Cache Entities
            createCache(cm, Level.class.getName(), jcacheConfiguration);
            createCache(cm, Role.class.getName(), jcacheConfiguration);
            createCache(cm, Lesson.class.getName(), jcacheConfiguration);
            createCache(cm, Word.class.getName(), jcacheConfiguration);
            createCache(cm, Chapter.class.getName(), jcacheConfiguration);
            createCache(cm, Language.class.getName(), jcacheConfiguration);

            // Cache Queries
            createCache(cm, "word-cache", jcacheConfiguration);

            // service
            createCache(cm, CacheKey.USER_PRINCIPAL.getName(), jcacheConfiguration);
        };
    }

    private void createCache(
            javax.cache.CacheManager cm,
            String cacheName,
            javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration
    ) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }

}
