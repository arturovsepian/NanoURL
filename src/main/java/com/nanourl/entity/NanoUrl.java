package com.nanourl.entity;

import com.oracle.nosql.spring.data.core.mapping.NosqlId;
import com.oracle.nosql.spring.data.core.mapping.NosqlTable;

import lombok.Data;

@Data
@NosqlTable
public class NanoUrl {

	@NosqlId
	private String shortUrl;
	
	private String longUrl;
	
	public NanoUrl(String shortUrl, String longUrl) {
		this.shortUrl = shortUrl;
		this.longUrl = longUrl;
	}

}
