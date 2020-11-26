package com.nanourl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.ssm.Cache;
import com.google.code.ssm.CacheFactory;
import com.google.code.ssm.config.AbstractSSMConfiguration;
import com.google.code.ssm.config.DefaultAddressProvider;
import com.google.code.ssm.providers.spymemcached.MemcacheClientFactoryImpl;
import com.google.code.ssm.providers.spymemcached.SpymemcachedConfiguration;

@Configuration
//@EnableCaching
public class MemCachedConfig extends AbstractSSMConfiguration {

	@Value("${spring.data.memcached.servers:placeholder}")
	private String servers;
	
	@Bean
	@Override
	public CacheFactory defaultMemcachedClient() {
		final SpymemcachedConfiguration conf = new SpymemcachedConfiguration();
		conf.setUseBinaryProtocol(true);
		conf.setConsistentHashing(true);

		final CacheFactory cf = new CacheFactory();
		cf.setCacheClientFactory(new MemcacheClientFactoryImpl());
		cf.setAddressProvider(new DefaultAddressProvider(servers));
		cf.setConfiguration(conf);
		return cf;
	}

	/*@Bean
	public CacheManager cacheManager() throws Exception {
		SSMCacheManager cacheManager = new SSMCacheManager();
		Cache cache = this.defaultMemcachedClient().getObject();
		cacheManager.setCaches(Arrays.asList(new SSMCache(cache, 0, true)));
		return cacheManager;
	}*/

	@Bean
	public Cache memcachedClient() throws Exception {
		CacheFactory cf = this.defaultMemcachedClient();
		cf.afterPropertiesSet();
		return cf.getObject();
	}
}
