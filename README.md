The goal of this Java (Spring Boot) code base is to do a simple POC which uses Restful microservice and then test the functionality with IBM OpenLiberty server (as well as "pure" Spring Boot with embedded Tomcat) in a cloud-native fashion. The application will be later containarized as docker images and deployed and tested on OpenShift. 

The application code base goes well beyond a typical Hello World. The usual spectrum of CRUD methods - GET, POST, PUT, DELETE - are implemented, the usual serialization/deserialization  between JSON and DTO takes place, and the data is persisted in a MySQL database. 
The idea is to keep it simple to bring forth the technical scaffolding clearly and yet serve as a starting point for further, more advanced POC/POT.  

To keep entire documentation modular, this page only deals with build of Spring Boo/MySQL application into a jar and its test on localhost, without entering into any aspect of docker or OpenShift or Kubernetes.
The steps can be carried out on Windows desktop or Linux environment after cloning the code. The requirements are the existence of JDK 8 (or higher) and maven install.
You also need a MySQL database to connect to. And you need to create a database with a particular name "corecruddb", which is hardcoded in Spring boot application.yml.

The codebase cloned should not need any change with one caveat. The application.yml files hard-codes the MySQL host name as "mysqldb". This was done to make it and the  created docker images friendly for Docker deploy (using docker-compose) or OpenShift deploy or K8 deploy.

As your MySQL instance might have some different hostname (like "localhost"), you might need to make this change to application.yml. However, if you can take the risk of putting mysqldb in your /etc/hosts, this change might not be required. 

If you wish to go beyond testing with Spring Boot jar, and create docker images and test it with docker images, you need to look into the companion README in the "containers" folder.
However, this programmer has uploaded the built docker images on public docker hub also, and you can directly pull from there and test it without doing the Java build outlined on this page.

Beyond docker images, if you wish to test on OpenShift, you need to navigate to the osms folder (os for openshift, ms for minishift) for the companion README and the associated config files. For openshift/minishift deploy, you can circumvent the the build described on this page and directly work with the images pulled from the public docker hub repository. 

Pure Spring boot with MySQL backend, no containers, no orchestration:

The code base implements a rest service with  5 archetypal operations: GET(a.k.a retrieve),GET ALL, POST (a.k.a create), PUT(a.k.a update or modify),  DELETE.
The opinionated Spring Boot Stack is used. The entier code is a backend services code. No GUI component.

Once you clone the code and build it, you need to start the MySQL server and create a database "corecruddb" before starting the application jar itself. 

Run the following from the base dir of the project (In my case, it is /home/rranjan/bankofarctic1):
mvn clean package

After that, set up your Mysql instance with a database "corecruddb" which can be accessed by the user "root" using the password "p@ssword".
On this page, we are avoiding any docker operation. But in case you are already in a docker environment and you wish to avoid a full-fledged MySql install and instead just go a short-cut Mysql docker 
set up, you can use the following 3 commands:
docker-compose -f containers/docker-compose-onlyMySQL.yml up
mysql –hlocalhost  -p3306 -uroot -pp@ssword
CREATE DATABASE corecruddb;

Once Mysql instance with database corecruddb set up, you can start the application and test it.
java –jar target/corecrudol1svc-0.0.1.jar
Control-C when you wish to stop your app (killing the above Tomcat server).

If you have a docker MySql, you can also kill your mysql container by first stopping it:
docker stop mysqldb
docker rm mysqldb
 
While your application is still running, you can test the functionality:

curl http://localhost:9091/clients

curl -X POST -H 'Content-Type: application/json' -d '{"id":1,"name":"Rajiv Ranjan","phone":"1111111111","address":"315 Front St","email":"rajiv@bankofarctic.com","balance":"111"}' http://localhost:9091/clients
curl -X POST -H 'Content-Type: application/json' -d '{"id":2,"name":"Julius Caesar","phone":"2222222222","address":"2 Main St","email":"julius@bankofarctic.com","balance":"222"}' http://localhost:9091/clients
curl -X POST -H 'Content-Type: application/json' -d '{"id":3,"name":"Mark Antony","phone":"3333333333","address":"3  Main St","email":"mark@bankofarctic.com","balance":"333"}' http://localhost:9091/clients
curl http://localhost:9091/clients
curl -X DELETE http://localhost:9091/clients/2
curl -X PUT -H 'Content-Type: application/json' -d '{"id":3,"name":"Mark Antony","phone":"3333333333","address":"9  Fantasy Blvd","email":"mark99@bankofarctic.com","balance":"9999"}' http://localhost:9091/clients/3
curl http://localhost:9091/clients
curl http://localhost:9091/clients/3

In case you are doing curl on Windows, there might be a need to escape the quote symbols. If you are a copy-paste master like me, I am putting Windows-friendly curl commands below for your copy-paste pleasure:
On windows:
curl http://localhost:9091/clients
curl -X POST -H "Content-Type: application/json" -d "{\"id\":1,\"name\":\"Rajiv Ranjan\",\"phone\":\"1111111111\",\"address\":\"315 Front St\",\"email\":\"rajiv@bankofarctic.com\",\"balance\":\"111\"}" http://localhost:9091/clients

curl -X POST -H "Content-Type: application/json" -d "{\"id\":2,\"name\":\"Julius Caesar\",\"phone\":\"2222222222\",\"address\":\"2 Main St\",\"email\":\"julius@bankofarctic.com\",\"balance\":\"222\"}" http://localhost:9091/clients
curl -X POST -H "Content-Type: application/json" -d "{\"id\":3,\"name\":\"Mark Antony\",\"phone\":\"3333333333\",\"address\":\"3  Main St\",\"email\":\"mark@bankofarctic.com\",\"balance\":\"333\"}" http://localhost:9091/clients
curl http://localhost:9091/clients
curl -X PUT -H "Content-Type: application/json" -d "{\"id\":3,\"name\":\"Mark Antony\",\"phone\":\"3333333333\",\"address\":\"9  Fantasy Blvd\",\"email\":\"mark99@bankofarctic.com\",\"balance\":\"9999\"}" http://localhost:9091/clients/3
curl http://localhost:9091/clients
curl http://localhost:9091/clients/3
curl –X DELETE http://localhost:9091/clients/2
curl http://localhost:9091/clients

Move to conainers folder and/or osms folders for docker and openshift related stuff, if and when you wish.
