# Stage and thin the application 
# tag::OLimage1[]
FROM openliberty/open-liberty:kernel-java8-openj9-ubi as staging
# end::OLimage1[]

# tag::copyJar[]
COPY --chown=1001:0 target/corecrudol1svc-0.0.1.jar \
                    /staging/fat-corecrudol1svc-0.0.1.jar
# end::copyJar[]

# tag::springBootUtility[]
RUN springBootUtility thin \
 --sourceAppPath=/staging/fat-corecrudol1svc-0.0.1.jar \
 --targetThinAppPath=/staging/thin-corecrudol1svc-0.0.1.jar \
 --targetLibCachePath=/staging/lib.index.cache
# end::springBootUtility[]

# Build the image
# tag::OLimage2[]
FROM openliberty/open-liberty:kernel-java8-openj9-ubi
# end::OLimage2[]

ARG VERSION=1.0
ARG REVISION=SNAPSHOT

LABEL \
  org.opencontainers.image.authors="Rajiv Ranjan" \
  org.opencontainers.image.vendor="Open Liberty" \
  org.opencontainers.image.url="local" \
  org.opencontainers.image.source="https://github.com/OpenLiberty/....." \
  org.opencontainers.image.version="$VERSION" \
  org.opencontainers.image.revision="$REVISION" \
  vendor="Open Liberty" \
  name="corecrudol1svc" \
  version="$VERSION-$REVISION" \
  summary="The CRUD rest services on Open Liberty" \
  description="This image contains the CRUD rest services using Spring Boot running with the Open Liberty runtime."

# tag::serverXml[]
RUN cp /opt/ol/wlp/templates/servers/springBoot2/server.xml /config/server.xml
# end::serverXml[]

# tag::libcache[]
COPY --chown=1001:0 --from=staging /staging/lib.index.cache /lib.index.cache
# end::libcache[]
# tag::thinjar[]
COPY --chown=1001:0 --from=staging /staging/thin-corecrudol1svc-0.0.1.jar \
                    /config/dropins/spring/thin-corecrudol1svc-0.0.1.jar
# end::thinjar[]

RUN configure.sh 
