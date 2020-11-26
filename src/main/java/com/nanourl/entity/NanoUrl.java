package com.nanourl.entity;

import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

@Data
@Table("nanourl")
public class NanoUrl {

	@Column
	@PrimaryKey
	private String shortUrl;
	
	@Column
	private String longUrl;
	
	@Column
	private Date createTime;
	
	public NanoUrl(String shortUrl, String longUrl, Date createTime) {
		this.shortUrl = shortUrl;
		this.longUrl = longUrl;
		this.createTime = createTime;
	}

	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public String getLongUrl() {
		return longUrl;
	}

	public void setLongUrl(String longUrl) {
		this.longUrl = longUrl;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
