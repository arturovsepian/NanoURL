package com.nanourl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;

@Configuration
public class CassandraConfig extends AbstractCassandraConfiguration {

	@Value("${spring.data.cassandra.contact-points:placeholder}")
	private String contactPoints;

	@Value("${spring.data.cassandra.keyspace:placeholder}")
	private String keySpace;
	
	@Value("${spring.data.cassandra.local-datacenter}")
	private String dataCenter;

	@Override
	protected String getContactPoints() {
		return contactPoints;
	}

	@Override
	protected String getKeyspaceName() {
		return keySpace;
	}

	@Override
	protected String getLocalDataCenter() {
		return dataCenter;
	}
}
