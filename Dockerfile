FROM java:8

WORKDIR /
ADD target/accountserver.jar accountserver.jar
ADD target/lib lib/
ADD target/*.properties ./
ADD target/*.xml ./

EXPOSE 8888
CMD java -jar accountserver.jar

