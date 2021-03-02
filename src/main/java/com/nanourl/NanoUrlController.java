package com.nanourl;

import java.util.Date;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
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
	private static final long range = (long) Math.pow(base, len);//3521614606208L
	private static final char[] base62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	private static final String[] schemes = {"http","https"};
	private String indexHtml;
    {
    	try {
	    	Resource resource = new ClassPathResource("static/index.html");
	    	InputStream inputStream = resource.getInputStream();
	    	indexHtml = new String(FileCopyUtils.copyToByteArray(inputStream), StandardCharsets.UTF_8);
		} catch (IOException e) {
			indexHtml = "ERROR";
		}
    }
	
	@Value("${nanourl.prefix}")
	private String prefix;
	
	@PostMapping("/create_url") 
	public ResponseEntity<String> createNanoURL(String longUrl) {
		UrlValidator urlValidator = new UrlValidator(schemes);
		if(!urlValidator.isValid(longUrl)) {
			return ResponseEntity.badRequest().build();
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
		return ResponseEntity.ok().body(new StringBuilder().append(prefix).append(nanoUrl).toString());
	}
	
	@GetMapping("/") 
	public ResponseEntity<String> getIndex(String nanoUrl) {
		return ResponseEntity.ok().body(indexHtml);
	}
	
	@GetMapping("/{nanoURL}") 
	public ResponseEntity<String> getUrl(@PathVariable("nanoURL") String nanoUrl) {
		if(nanoUrl.length() < len) return ResponseEntity.notFound().build();
		if(nanoUrl.charAt(nanoUrl.length() - 1) == '/') nanoUrl =  nanoUrl.substring(0, nanoUrl.length() - 1);
		if(nanoUrl.length() < len) return ResponseEntity.notFound().build();
		String longUrl;
		try {
			longUrl = memcachedClient.get(nanoUrl, SerializationType.PROVIDER);
			if(longUrl == null) {
				Optional<NanoUrl> data = nanoUrlRepository.findById(nanoUrl);
				if(data.isEmpty()) return ResponseEntity.notFound().build();
				longUrl = data.get().getLongUrl();
				memcachedClient.set(nanoUrl, 0, longUrl, SerializationType.PROVIDER);
			}
		} catch (TimeoutException | CacheException e) {
			// TODO log Error
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
		}
		return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION, longUrl).build();
	}
	
	private static String base62Encode(long val) {
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
