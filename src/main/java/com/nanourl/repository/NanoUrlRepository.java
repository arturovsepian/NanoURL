package com.nanourl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nanourl.entity.NanoUrl;

public interface NanoUrlRepository extends JpaRepository<NanoUrl, String> {

}
