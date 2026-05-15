$ErrorActionPreference = "Stop"
mvn spring-boot:run "-Dspring-boot.run.profiles=demo" "-Dspring-boot.run.arguments=--server.port=8081 --spring.devtools.livereload.enabled=false"
