package com.nanourl.entity;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table
public class NanoUrl {

	@Id
	private String shortUrl;
	
	private String longUrl;
	
	public NanoUrl(String shortUrl, String longUrl) {
		this.shortUrl = shortUrl;
		this.longUrl = longUrl;
	}
	
	public NanoUrl() {
	}

}
