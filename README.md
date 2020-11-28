Cassandra cluster + Memcached + Docker + Docker Compose + Spring Boot + REST + Tomcat + Nginx = NanoURL (URL shortener like TinyURL)
 * 3 nodes in Cassandra cluster with replication and sharding
 * 2 Memcached instances
 * 4 containers running Tomcat
 * 1 Nginx

Everything running on Docker on Ubuntu 20.04 LTS which runs on VirtualBox under Windows 10 on a laptop.  
Nginx serves static content (index page with the form to create short URL) and does load balancing between 4 web server nodes. Only nginx port 80 is exposed to outside world.

# Getting Started
  
How to set up:  

	1. Install Docker and Docker Compose  	
	2. Pull the following images:  		
		REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE  		
		nginx               latest              bc9a0695f571        46 hours ago        133MB  		
		memcached           latest              08ea1a3256c4        2 days ago          82.4MB  		
		cassandra           latest              f1469bc7ea02        7 days ago          405MB  		
		tomcat              latest              e0bd8b34b4ea        7 days ago          649MB  		
	3. cd to Docker folder which contains yaml files, docker files and some other files to build custom images  	
	4. Start Cassandra and Memcached:  
		sudo docker-compose -f cassandra.yml up -d  	
	5. Build war file and copy it to docker folder  	
	6. Create our web service image:  
		sudo docker image build -t nanows -f ./webservice.txt .  	
	7. Start web servers:  
		sudo docker-compose -f web.yml up -d  	
	8. Create nginx with custom config and index.html:  
		sudo docker image build -t nginxlb -f ./nginx.txt .  	
	9. Start nginx:  
		sudo docker run --name nginxlb --rm -d --network docker_backend -p 80:80 nginxlb  

You should see the following result:  

sudo docker ps  
```
  CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                                         NAMES  
  ac3298b79295        nginxlb             "/docker-entrypoint.…"   19 seconds ago      Up 18 seconds       0.0.0.0:80->80/tcp                            nginxlb  
  271c3dd26094        nanows:latest       "catalina.sh run"        2 minutes ago       Up 2 minutes        8080/tcp                                      nano-ws2  
  f4edd675b927        nanows:latest       "catalina.sh run"        2 minutes ago       Up 2 minutes        8080/tcp                                      nano-ws4  
  18002cc65788        nanows:latest       "catalina.sh run"        2 minutes ago       Up 2 minutes        8080/tcp                                      nano-ws1  
  fed1bfafd3b0        nanows:latest       "catalina.sh run"        2 minutes ago       Up 2 minutes        8080/tcp                                      nano-ws3  
  7f9ae5bf3398        cassandra:latest    "docker-entrypoint.s…"   6 minutes ago       Up 6 minutes        7000-7001/tcp, 7199/tcp, 9042/tcp, 9160/tcp   nano-cassandra1  
  36e67179670c        memcached:latest    "docker-entrypoint.s…"   6 minutes ago       Up 6 minutes        11211/tcp                                     nano-memcached1  
  ae49a328dc49        cassandra:latest    "docker-entrypoint.s…"   6 minutes ago       Up 6 minutes        7000-7001/tcp, 7199/tcp, 9042/tcp, 9160/tcp   nano-cassandra2  
  381d9745d08b        cassandra:latest    "docker-entrypoint.s…"   6 minutes ago       Up 6 minutes        7000-7001/tcp, 7199/tcp, 9042/tcp, 9160/tcp   nano-cassandra3  
  0c0d15e469d9        memcached:latest    "docker-entrypoint.s…"   6 minutes ago       Up 6 minutes        11211/tcp                                     nano-memcached2  
```  
Now you can go to http://localhost and check how it works.  

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.4.0/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.4.0/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.4.0/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Data for Apache Cassandra](https://docs.spring.io/spring-boot/docs/2.4.0/reference/htmlsingle/#boot-features-cassandra)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)

