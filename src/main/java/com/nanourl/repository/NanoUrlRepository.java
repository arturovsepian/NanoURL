package com.nanourl.repository;

import com.nanourl.entity.NanoUrl;
import com.oracle.nosql.spring.data.repository.NosqlRepository;

public interface NanoUrlRepository extends NosqlRepository<NanoUrl, String> {

}
