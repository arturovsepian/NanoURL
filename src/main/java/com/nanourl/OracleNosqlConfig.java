package com.nanourl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.oracle.nosql.spring.data.config.AbstractNosqlConfiguration;
import com.oracle.nosql.spring.data.config.NosqlDbConfig;
import com.oracle.nosql.spring.data.repository.config.EnableNosqlRepositories;

import oracle.nosql.driver.kv.StoreAccessTokenProvider;

@Configuration
@EnableNosqlRepositories
public class OracleNosqlConfig extends AbstractNosqlConfiguration {
	
	@Value("${spring.data.oracle.nosql.server}")
	private String server;

	@Bean
    public NosqlDbConfig nosqlDbConfig() {
        return new NosqlDbConfig(server, new StoreAccessTokenProvider());
    }
}
