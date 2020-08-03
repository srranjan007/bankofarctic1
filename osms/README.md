Deployment and test on Minishift (microcosm of OpenShift on your PC):

minishift start

You will get a welcome message and a URL to your admin console:
OpenShift server started.
The server is accessible via web console at:
    https://192.168.42.14:8443/console

The defaults for accessing the admin console are:
User:     developer
Password: developer

To login as administrator:
oc login -u system:admin

To set up the environment. go to openshift_minishift folder and use the files here.
. ./setenv.sh

The above will set environment ready with "oc" cli for OpenShift and also make it ready for docker images.  In current RedHat parlance,  podman is used instead of docker.

OpenShift deployment begins with the following:
oc new-project <projecName>

But, instead of executing the above on the command line, we will let it be created through admin console when creating a MySQL instance for this project. MySQL instance creation can also be done on the command line, but we decide to do in this exercise on the admin console. Even on admin console, there is a Create Project button, but we don't use this also. Instead, in the form for MySQL instance creation, we shall enter our brand new project name.

In the MySQL instance creation form, the important default values that we override for the requirements of this application configuration are as follows:

Add to Project: projarctic   (the new project)
Database Service name: mysqldb   (we depart from the default of mysql as the application yml files use this value for connecting to database)
MySQL root user Password: p@ssword (our application yml files use this value for connecting to database. Actually, this can be considered a bad practice of using root user for MySQL. But right now, we don't wish to disturber our current source code and images configuration and live with this.
The right way will be to create a separate userid/password for application connectivity).

Once we click the Create button, it should create a brand new project (projarctic) and a MySQL service in the Minishift environment.

There is one more thing that we need to do. In the MySQL instance, there need to be ready a database by the name of corecruddb, as the spring boot/Liberty application uses this value in the JDBC connectivity string. This database creation could have been done by overriding the default database created when we were creating MySQL instance. But we did not override the default database that is created. Hence, we need to do now. This poses a little challenge, as the MySQL is an openshift pod here. There are a few ways, but I did like this:

oc get pods   
to get the pod name.

Now, open a remote shell inside the pod and log on to mysql and do your stuff.
oc  get pods
oc  rsh <POD_NAME>
mysql -u  <ENTER_MYSQL_USERNAME> -p<PASSWORD>

With the above step, you will be on a mysql command prompt where you can do any database operation. One little precaution, I used root as the ENTER_MYSQL_USERNAME, a bad practice, and this requires I don't write -pp@ssword. I found that if for the root, I give the password, mysql freaks out. So, don't give password option if using root user to access mysql.
create database corecruddb;
exit; #To exit mysql
exit; #To exit the pod shell.

After this, it is simple as the series of steps required has been capsulated in kubernetes config files.
Yes, though we are deploying on openshift, I use kubernetes config files. The benefit of this approach is that it puts up things on a multi-cloud path effortlessly.

oc apply -f myk8_ol.yaml
oc apply -f myk8_sb.yaml

We do the command 2 times above, as we are booting pods for both pure spring boot (tomcat) and the liberty version.

After that we can run the following commands if we wish to see what openshift artefacts are created:
oc get pods
oc get deployments
oc get svc
oc get routes

The last command (routes) give us the virutal IP address that we need to use to invoke the service. In the case of real 
OpenShift deployment, it will be the public IP address provided by the PaaS provider to the end users.


Like done earlier for localhost deployments, we can carry out some quick tests for CRUD operations, using curl or soapUI or postman etc.
Like above, we will use both pure spring boot endpoint and liberty endpoint, to verify both the applications are working as intended. 


curl http://corecrudsb1-route-projarctic.192.168.42.14.nip.io/clients
curl http://corecrudol1-route-projarctic.192.168.42.14.nip.io/clients

curl -X POST -H 'Content-Type: application/json' -d '{"id":1,"name":"Rajiv Ranjan","phone":"1111111111","address":"315 Front St","email":"rajiv@bankofarctic.com","balance":"111"}' http://corecrudsb1-route-projarctic.192.168.42.14.nip.io/clients

curl -X POST -H 'Content-Type: application/json' -d '{"id":2,"name":"Julius Caesar","phone":"2222222222","address":"2 Main St","email":"julius@bankofarctic.com","balance":"222"}' http://corecrudol1-route-projarctic.192.168.42.14.nip.io/clients
curl -X POST -H 'Content-Type: application/json' -d '{"id":3,"name":"Mark Antony","phone":"3333333333","address":"3  Main St","email":"mark@bankofarctic.com","balance":"333"}' http://corecrudsb1-route-projarctic.192.168.42.14.nip.io/clients
curl http://corecrudsb1-route-projarctic.192.168.42.14.nip.io/clients
curl http://corecrudol1-route-projarctic.192.168.42.14.nip.io/clients
curl -X PUT -H 'Content-Type: application/json' -d '{"id":3,"name":"Mark Antony","phone":"3333333333","address":"9  Fantasy Blvd","email":"mark99@bankofarctic.com","balance":"9999"}' http://corecrudol1-route-projarctic.192.168.42.14.nip.io/clients/3
curl http://corecrudsb1-route-projarctic.192.168.42.14.nip.io/clients
curl http://corecrudol1-route-projarctic.192.168.42.14.nip.io/clients
curl http://corecrudol1-route-projarctic.192.168.42.14.nip.io/clients/3


The above tests should prove to us that both our HTTP bartenders, the Tomcat and the Liberty servers, are serving us the JSON goblets nicely.
