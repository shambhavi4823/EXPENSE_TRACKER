FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN javac -cp "lib/*" *.java

EXPOSE 8080

CMD ["sh", "-c", "java -cp '.:lib/*' main"]