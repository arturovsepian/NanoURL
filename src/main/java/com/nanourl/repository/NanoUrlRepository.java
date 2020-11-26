package com.nanourl.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;

import com.nanourl.entity.NanoUrl;

public interface NanoUrlRepository extends CassandraRepository<NanoUrl, String> {

}
