A POC for Spring Boot Rest microservice with MySQL backen hosted on Open Liberty Server


(1)
 Pure Spring boot with MySQL backend, no containers, no orchestration.

The code base implements a rest service with  5 archetypal operations: GET(a.k.a retrieve),GET ALL, POST (a.k.a create), PUT(a.k.a update or modify),  DELETE.
The opinionated Spring Boot Stack is used. The entier code is a backend services code. No GUI component.

To test the code quickly for the CRUD operations, we shall use curl. Alternatively, GUI best tools like soanUI or Postman or Insomnia can be used. 

One dependency of the services code is that a MySQL backend database with the name corecruddb should be present.

Once you clone the code and build it, you need to start the MySQL server and create a database "corecruddb" before starting the application jar itself. 

This portion of the exercise can be carried out in either Windows or Linux environments, the one condition being that Java runtime and a maven installation should be available. 

Run the following from the base dir of the project (In my case, it is /home/rranjan/bankofarctic1):
mvn clean package
docker-compose -f containers/docker-compose-onlyMySQL.yml up
mysql –hlocalhost  -p3306 -uroot -pp@ssword
CREATE DATABASE corecruddb;
java –jar target/corecrudol1svc-0.0.1.jar
Control-C to kill the above Tomcat server.

If you wish, you can also kill your mysql container by first stopping it:
docker stop mysqldb
docker rm mysqldb

(2) Enter Container (docker)

Now let us move towards docker based application boot. 
First, we need to revert back mysql connection string from localhost to mysqldb hostname. So, we do this little change to application.yml and rebuild the application jar.
mvn clean package

Now, let us create docker images, one each for pure SpringBoot application (embedded Tomcat) and another an IBM WAS Liberty server encapsulating this Spring Boot application:

cp containers/Dockerfile_sb Dockerfile
docker build -t srranjan007/corecrudsb1img . 
The above builds a pure sprinb boot docker image with embedded tomcat.

The following 2 commands build a spring boot application hosted on WAS liberty server.

cp containers/Dockerfile_ol Dockerfile
docker build -t srranjan007/corecrudol1img . 

The following command will show that both your images are ready:
rranjan@ubuntu:~/bankofarctic1$ docker images
REPOSITORY                    TAG                       IMAGE ID            CREATED              SIZE
srranjan007/corecrudol1img    latest                    d88da0095836        About a minute ago   774MB
<none>                        <none>                    5e5b06e73c75        2 minutes ago        816MB
srranjan007/corecrudsb1img    latest                    2cac5ec41782        6 minutes ago        126MB

I remove the Dockerfile with a purpose. If there is a Dockerfile in the base dir of the project source code, Openshift might pick it up for image creation instead of using source code, 
even when it is not what you want. 
rm Dockerfile

Now, let us boot up the entire system. Although both pure spring boot and the liberty profile services are not required simultaneously, it will not harm even if they coexist. In one case, for pure
spring boot, the service port is 9091 (dictated by application.yml) whereas for liberty server, it is the default WAS port of 9080.
docker-compose -f containers/docker-compose.yml up

Now let us send some requests to Tomcat and some to Liberty server (as both are connected to the same MySQLDB instance in this case, they will show changes done by each).

curl http://localhost:9091/clients
curl http://localhost:9080/clients

curl -X POST -H 'Content-Type: application/json' -d '{"id":1,"name":"Rajiv Ranjan","phone":"1111111111","address":"315 Front St","email":"rajiv@bankofarctic.com","balance":"111"}' http://localhost:9091/clients

curl -X POST -H 'Content-Type: application/json' -d '{"id":2,"name":"Julius Caesar","phone":"2222222222","address":"2 Main St","email":"julius@bankofarctic.com","balance":"222"}' http://localhost:9080/clients
curl -X POST -H 'Content-Type: application/json' -d '{"id":3,"name":"Mark Antony","phone":"3333333333","address":"3  Main St","email":"mark@bankofarctic.com","balance":"333"}' http://localhost:9091/clients
curl http://localhost:9091/clients
curl http://localhost:9080/clients
curl -X PUT -H 'Content-Type: application/json' -d '{"id":3,"name":"Mark Antony","phone":"3333333333","address":"9  Fantasy Blvd","email":"mark99@bankofarctic.com","balance":"9999"}' http://localhost:9080/clients/3
curl http://localhost:9091/clients
curl http://localhost:9080/clients
curl http://localhost:9091/clients/3


The 2 docker images have been uploaded to public docker hub, in case you don't wish to go into the hassle of managing the source code and just wish to use docker images directly in a docker friendly platform.
Of course, you will get mysql also at docker hub (in case you use docker-compose above, that guy will anyway take care of that for you). 

docker.io/srranjan007/corecrudol1img
docker.io/srranjan007/corecrudsb1img

Next, we shall try to run the application from OpenShift and/or any other PaaS platform so that services can be accessed from a public IP.

(3) Enter PaaS

