package com.nanourl;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.code.ssm.Cache;
import com.google.code.ssm.api.format.SerializationType;
import com.google.code.ssm.providers.CacheException;
import com.nanourl.entity.NanoUrl;
import com.nanourl.repository.NanoUrlRepository;

@RestController
public class NanoUrlController {
	@Autowired
	private NanoUrlRepository nanoUrlRepository;
	@Autowired
	private Cache memcachedClient;
	private static final int base = 62;
	private static final int len = 7;
	private static final long range = (int) Math.pow(base, len);//3521614606208L
	private static final char[] base62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	String[] schemes = {"http","https"};
	@Value("${nanourl.prefix}")
	private String prefix;
	
	@PostMapping("/create_url") 
	public ResponseEntity<String> createNanoURL(String longUrl) {
		UrlValidator urlValidator = new UrlValidator(schemes);
		if(!urlValidator.isValid(longUrl)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		long r = ThreadLocalRandom.current().nextLong(range);
		String nanoUrl = base62Encode(r);
		Optional<NanoUrl> data = nanoUrlRepository.findById(nanoUrl);
		try {
			while(data.isPresent() || !memcachedClient.add(nanoUrl, 0, longUrl, SerializationType.PROVIDER)) {
				r = ThreadLocalRandom.current().nextLong(range);
				nanoUrl = base62Encode(r);
				data = nanoUrlRepository.findById(nanoUrl);
			}
		} catch (TimeoutException | CacheException e) {
			// TODO log Error
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
		}
		nanoUrlRepository.save(new NanoUrl(nanoUrl, longUrl, new Date()));
		return new ResponseEntity<>(new StringBuilder().append(prefix).append(nanoUrl).toString(), HttpStatus.OK);
	}
	
	@GetMapping("/{nanoURL}") 
	public ResponseEntity<String> getUrl(@PathVariable("nanoURL") String nanoUrl) {
		if(nanoUrl == null || nanoUrl.length() < len) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		if(nanoUrl.charAt(nanoUrl.length() - 1) == '/') nanoUrl =  nanoUrl.substring(0, nanoUrl.length() - 1);
		if(nanoUrl.length() < len) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		String longUrl;
		try {
			longUrl = memcachedClient.get(nanoUrl, SerializationType.PROVIDER);
			if(longUrl == null) {
				Optional<NanoUrl> data = nanoUrlRepository.findById(nanoUrl);
				if(data.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				longUrl = data.get().getLongUrl();
				memcachedClient.set(nanoUrl, 0, longUrl, SerializationType.PROVIDER);
			}
		} catch (TimeoutException | CacheException e) {
			// TODO log Error
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
		}
		return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION, longUrl).build();
	}
	
	private String base62Encode(long val) {
		StringBuilder sb = new StringBuilder();
	    while (val != 0) {
	        sb.append(base62[(int) (val % base)]);
	        val /= base;
	    }
	    while (sb.length() < len) {
	        sb.append(0);
	    }
	    return sb.reverse().toString();
	}
}
