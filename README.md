NanoURL (URL shortener like TinyURL) in Orcale Cloud free tier

![Imgur](https://i.imgur.com/OHuXMvW.png)

 * DB - Oracle Autonomous Transaction Processing
 * 2 Memcached instances
 * 2 Spring Boot applications on embedded Tomcat
 * 1 Nginx

Everything runs in Oracle Cloud using always free services (ATP DB and 2 compute VMs running Ubuntu).  
Nginx serves static content (index page with the form to create short URL), does load balancing between 2 web server nodes and ssl termination.

Deployment was done without containers to avoid any possible performance overhead since VMs have limited memory and CPU resources (only 1 Gb of RAM and 1/8 OCPU each). SQL DB (Oracle ATP) was chosen because free NoSQL DB was not available in my region.

# Getting Started
  
How to set up. I will list only some major steps skipping a lot of things like opening ports on your compute instances, setting up connection to ATP.

	1. Get ATP DB
	2. Create user and table(details can be found in db/user.sql and db/schema.sql)
	3. Get 2 compute instances
	4. Install java and memcached on both instances and nginx on one of them. You will need to reconfigure -l parameter for memcached to allow connections from both VMs (it is 127.0.0.1 by default, you can use your private ip address)
	5. Configure nginx for LB (sample config is attached) and ssl if you want and add index.html
	6. Update application.properties with your user/password and server details
	7. Build jar file and copy it on you VMs
	8. Start web servers:
		java -DTNS_ADMIN=<path to your ATP wallet> -jar /usr/local/NanoURL-0.0.3-SNAPSHOT.jar

Now you can go to http(s)://your_vm_public_ip and check how it works.  

Some performance tests. Without SSL/TLS it can handle around 500 redirects in case of cache hit and 300 in case of cache miss. With SSL/TLS CPU becomes a bottleneck for NGINX and it can only process about 100 requests. Using Oracle Load Balancer with SSL termination 2 servers can handle around 300-400 redirects.

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.4.0/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.4.0/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.4.0/reference/htmlsingle/#boot-features-developing-web-applications)
* [Java Connectivity with Autonomous Database (ATP or ADW) using 19c and 18.3 JDBC](https://www.oracle.com/database/technologies/java-connectivity-to-atp.html)
* [OCI: Security Rules](https://docs.oracle.com/en-us/iaas/Content/Network/Concepts/securityrules.htm#stateful)
* [OCI: Getting Started with Autonomous](https://blogs.oracle.com/oraclemagazine/getting-started-with-autonomous)
* [OCI: Details of the Always Free Resources](https://docs.oracle.com/en-us/iaas/Content/FreeTier/resourceref.htm)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)

