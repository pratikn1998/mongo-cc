package org.jboss.as.quickstarts.kitchensink;

import org.jboss.as.quickstarts.kitchensink.config.ApplicationConfiguration;
import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
* The `Main` class serves as the entry point for the Spring Boot application.
* It extends {@link org.springframework.boot.web.servlet.support.SpringBootServletInitializer} to enable deployment
* as a traditional WAR file in a servlet container, in addition to being runnable as a standalone JAR.
*
* <p>This class is annotated with {@link org.springframework.boot.autoconfigure.SpringBootApplication},
* which is a convenience annotation that adds:
* <ul>
*     <li>{@link org.springframework.context.annotation.Configuration}: Tags the class as a source of bean definitions.</li>
*     <li>{@link org.springframework.boot.autoconfigure.EnableAutoConfiguration}: Tells Spring Boot to start adding beans
*         based on classpath settings, other beans, and various property settings.</li>
*     <li>{@link org.springframework.context.annotation.ComponentScan}: Tells Spring to look for other components,
*         configurations, and services in the `org.jboss.as.quickstarts.kitchensink` package, allowing it to find
*         controllers, services, and repositories.</li>
* </ul>
* </p>
*
* <p>It explicitly imports {@link org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration}
* to ensure proper configuration of MongoDB connectivity, and uses
* {@link org.springframework.data.mongodb.repository.config.EnableMongoRepositories} to enable Spring Data MongoDB
* repositories, specifically scanning for interfaces like {@link org.jboss.as.quickstarts.kitchensink.data.MemberRepository}.</p>
*
* <p>The `main` method is the standard Java entry point, which initiates the Spring application context
* by calling {@link org.springframework.boot.SpringApplication#run(Class, String...)}, using
* {@link org.jboss.as.quickstarts.kitchensink.config.ApplicationConfiguration} as the primary configuration source.
* Any exceptions during application startup are caught and logged using SLF4J.</p>
*
* @see org.springframework.boot.autoconfigure.SpringBootApplication
* @see org.springframework.boot.web.servlet.support.SpringBootServletInitializer
* @see org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
* @see org.springframework.data.mongodb.repository.config.EnableMongoRepositories
* @see org.jboss.as.quickstarts.kitchensink.config.ApplicationConfiguration
* @see org.jboss.as.quickstarts.kitchensink.data.MemberRepository
*/
@SpringBootApplication
@Import(value = MongoAutoConfiguration.class)
@EnableMongoRepositories(basePackageClasses = MemberRepository.class)
public class Main extends SpringBootServletInitializer {
    /**
    * Serves as the entry point for the Kitchensink Spring Boot application.
    * This method initializes and runs the Spring application context.
    * <p>
    * It attempts to start the application by calling {@code SpringApplication.run()},
    * using {@code ApplicationConfiguration.class} as the primary source for Spring
    * configuration. This effectively bootstraps the entire Spring environment,
    * including component scanning, auto-configuration, and dependency injection.
    * </p>
    * <p>
    * In case of any exceptions during the application startup process, it catches
    * the exception and logs the stack trace using {@code LoggerFactory.getLogger(Main.class)}.
    * This provides basic error handling to prevent the application from failing silently
    * during initialization.
    * </p>
    *
    * @param args Command line arguments passed to the application. These are forwarded
    *             to the Spring application context.
    * @see org.springframework.boot.SpringApplication#run(Class, String...)
    * @see org.jboss.as.quickstarts.kitchensink.ApplicationConfiguration
    * @see org.slf4j.LoggerFactory
    */
    public static void main(String[] args) {
        try {
            SpringApplication.run(ApplicationConfiguration.class, args);
        } catch (Exception e) {
            LoggerFactory.getLogger(Main.class).error(e.getStackTrace().toString(), e);
        }
    }
}
