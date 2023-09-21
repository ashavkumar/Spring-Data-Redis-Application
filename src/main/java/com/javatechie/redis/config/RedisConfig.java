package com.javatechie.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.cluster.nodes}")
    private String clusterNodes;

    @Value("${spring.redis.cluster.timeout}")
    private Long timeout;

    @Value("${spring.redis.cluster.max-redirects}")
    private int maxRedirects;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
/*      //Redis Standalone Architecture -> Single Master and Multiple Slaves
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName("127.0.0.1");
        configuration.setPort(6379);
        System.out.println("LocalHost:"+configuration.getHostName());
        return new JedisConnectionFactory(configuration);
*/
        //Redis Cluster Architecture -> Multiple Masters and Multiple Slaves
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        redisClusterConfiguration.setClusterNodes(getClusterNodes());
        redisClusterConfiguration.setMaxRedirects(maxRedirects);
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration);
        return jedisConnectionFactory;
    }

    private Iterable<RedisNode> getClusterNodes() {
        String[] hostAndPorts = StringUtils.commaDelimitedListToStringArray(clusterNodes);
        System.out.println("Host And Ports:" + hostAndPorts.toString());
        Set<RedisNode> clusterNodes = new HashSet<>();
        for (String hostAndPort : hostAndPorts) {
            int lastScIndex = hostAndPort.lastIndexOf(":");
            if (lastScIndex == -1)
                continue;
            try {
                String host = hostAndPort.substring(0, lastScIndex);
                Integer port = Integer.parseInt(hostAndPort.substring(lastScIndex + 1));
                System.out.println("Host:" + host + " " + "Port:" + port);
                clusterNodes.add(new RedisNode(host, port));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Clusters Nodes:" + clusterNodes);
        return clusterNodes;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new JdkSerializationRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }
}
