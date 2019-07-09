FROM openjdk:8-jre
VOLUME /tmp
ADD ${systemName}-web.jar ${systemName}-web.jar
RUN bash -c 'touch /${systemName}-web.jar'
EXPOSE ${port}
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xms100m","-Xmx100m","-jar","/${systemName}-web.jar"]