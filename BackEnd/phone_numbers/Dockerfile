FROM openjdk
MAINTAINER AbdAlMoniem AlHifnawy
COPY target/phone_numbers-0.0.1-SNAPSHOT.jar phone_numbers-0.0.1-SNAPSHOT.jar
COPY SQLiteDB/customers.db SQLiteDB/customers.db
ENTRYPOINT ["java", "-jar", "/phone_numbers-0.0.1-SNAPSHOT.jar"]