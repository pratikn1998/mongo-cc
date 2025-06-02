package org.jboss.as.quickstarts.kitchensink.test.config;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

/**
* {@code MongoDBConfig} is a Spring {@code @Configuration} class responsible for setting up and managing a
* MongoDB test container using Testcontainers. This configuration is primarily used within the test environment
* to provide a clean, ephemeral MongoDB instance for integration tests.
*
* <p>It leverages the {@code @Container} annotation from Testcontainers to automatically manage the lifecycle
* of a {@link org.testcontainers.containers.MongoDBContainer}. The static initializer block ensures that
* the MongoDB container starts as soon as this class is loaded, and its dynamically assigned port is
* then made available as a system property.</p>
*
* <p><b>Key Features:</b></p>
* <ul>
*   <li><b>Test Container Management:</b> Declares a static {@link MongoDBContainer} instance,
*       {@code mongoDBContainer}, which is managed by Testcontainers. This ensures a fresh MongoDB
*       instance is available for each test run.</li>
*   <li><b>Dynamic Port Assignment:</b> The static initializer block starts the container and
*       retrieves the dynamically mapped port for MongoDB (default 27017). This port is then
*       set as a system property named "mongodb.container.port".</li>
*   <li><b>Integration with Spring Boot:</b> Although this class is a test configuration, the
*       system property it sets allows the main application's MongoDB configuration (e.g., in
*       {@link org.jboss.as.quickstarts.kitchensink.Main} or via Spring Boot's auto-configuration)
*       to connect to this dynamically provisioned test database.</li>
* </ul>
*
* <p>This setup ensures that tests can run against a real MongoDB instance without requiring a
* pre-installed or persistent database, promoting isolated and reproducible test environments.</p>
*
* @see org.springframework.context.annotation.Configuration
* @see org.testcontainers.containers.MongoDBContainer
* @see org.testcontainers.junit.jupiter.Container
*/
@Configuration
public class MongoDBConfig {
    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest").withExposedPorts(27017);

    static {
        mongoDBContainer.start();
        Integer port = mongoDBContainer.getMappedPort(27017);
        System.setProperty("mongodb.container.port", String.valueOf(port));
    }
}
