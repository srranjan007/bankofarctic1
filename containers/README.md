Enter Containers (docker):

While you are reading this documentation, to run the docker commands mentioned below, you will need to go back again to the previous folder (bankofarctic1, where the code was clone)

Now let us move towards docker based application boot. 
First, we need to revert back mysql connection string from localhost to mysqldb hostname. So, we do this little change to application.yml and rebuild the application jar.
cd ..
mvn clean package

Now, let us create docker images, one each for pure SpringBoot application (embedded Tomcat) and another an IBM WAS Liberty server encapsulating this Spring Boot application:

cp containers/Dockerfile_sb Dockerfile
docker build -t srranjan007/corecrudsb1img . 
The above builds a pure spring boot docker image with embedded tomcat.

The following 2 commands build a spring boot application hosted on WAS liberty server.
cp containers/Dockerfile_ol Dockerfile
docker build -t srranjan007/corecrudol1img . 

The following command will show that both your images are ready:
rranjan@ubuntu:~/bankofarctic1$ docker images
REPOSITORY                    TAG                       IMAGE ID            CREATED              SIZE
srranjan007/corecrudol1img    latest                    d88da0095836        About a minute ago   774MB
<none>                        <none>                    5e5b06e73c75        2 minutes ago        816MB
srranjan007/corecrudsb1img    latest                    2cac5ec41782        6 minutes ago        126MB

I delete the Dockerfile below for a reason (after creating the image). If there is a Dockerfile in the base dir of the project source code, Openshift might pick it up for image creation instead of using source code, even when it is not what you want, if you are using OpenShift Source to Image deploy(S2I) tool, which used to be the default way for OpenShift.
rm Dockerfile

Now, let us boot up the entire system. Although both pure spring boot and the liberty profile services are not required simultaneously, it will not harm even if they coexist. In one case, for pure spring boot, the service port is 9091 (dictated by application.yml) whereas for liberty server, it is the default WAS port of 9080.
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

Next you can move to folder osms if you wish to work with OpenShift/Minishift deploy.
