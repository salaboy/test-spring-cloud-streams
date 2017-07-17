# test-spring-cloud-streams

This project is a reproducer for testing Spring Cloud Streams. 
I'm trying to find out if there is a bug or a bad configuration from my side. 
This test uses RabbitMQ that's why you need to make sure that is started (Docker Compose provided)


In order to reproduce you should:
1) cd docker/
2) docker-compose up -d
3) cd ..
4) mvn clean install 
