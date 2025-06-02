package org.jboss.as.quickstarts.kitchensink.config;

import org.jboss.as.quickstarts.kitchensink.model.DatabaseSequence;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
* {@code ApplicationConfiguration} is a Spring {@code @Configuration} class that sets up application-wide beans
* and performs initial data store configurations, specifically for MongoDB.
* <p>
* It implements {@link org.springframework.context.ApplicationListener ApplicationListener}
* for {@link org.springframework.boot.context.event.ApplicationReadyEvent ApplicationReadyEvent}
* to execute setup logic once the application context is fully loaded and ready.
* </p>
* <p>
* This class is responsible for:
* <ul>
*     <li>Ensuring the existence of critical MongoDB collections (e.g., for {@code DatabaseSequence} and {@code Member})
*         at application startup.</li>
*     <li>Defining and providing Spring beans for MongoDB validation integration, specifically
*         {@link org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener ValidatingMongoEventListener}
*         and {@link org.springframework.validation.beanvalidation.LocalValidatorFactoryBean LocalValidatorFactoryBean},
*         which are crucial for integrating Spring Data MongoDB with JSR-303 bean validation.</li>
* </ul>
* </p>
*
* <p><b>Fields:</b></p>
* <ul>
*   <li><code>private final MongoOperations mongoOperations</code>: An instance of {@link org.springframework.data.mongodb.core.MongoOperations MongoOperations}
*       used for interacting with the MongoDB database, including checking for and creating collections.</li>
* </ul>
*
* <p><b>Key Methods:</b></p>
* <ul>
*   <li><code>ApplicationConfiguration(MongoOperations)</code>: Constructor that injects the {@link org.springframework.data.mongodb.core.MongoOperations MongoOperations}
*       instance.</li>
*   <li><code>onApplicationEvent(ApplicationReadyEvent)</code>: Initializes MongoDB collections for {@code DatabaseSequence}
*       and {@code Member} if they do not already exist.</li>
*   <li><code>validatingMongoEventListener(LocalValidatorFactoryBean)</code>: Creates and returns a
*       {@link org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener ValidatingMongoEventListener}
*       bean, integrating validation into MongoDB persistence operations.</li>
*   <li><code>validator()</code>: Creates and returns a {@link org.springframework.validation.beanvalidation.LocalValidatorFactoryBean LocalValidatorFactoryBean}
*       bean, which is the core JSR-303 validator factory.</li>
* </ul>
*
* @see org.springframework.context.annotation.Configuration
* @see org.springframework.context.ApplicationListener
* @see org.springframework.boot.context.event.ApplicationReadyEvent
* @see org.springframework.data.mongodb.core.MongoOperations
* @see org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener
* @see org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
*/
@Configuration
public class ApplicationConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    private final MongoOperations mongoOperations;

    @Autowired
    public ApplicationConfiguration(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    /**
    * Handles the `ApplicationReadyEvent` to perform initial setup of MongoDB collections.
    * This method is invoked by the Spring framework once the application context is fully loaded and ready.
    * It ensures that essential collections, `DatabaseSequence` and `Member`, exist in the MongoDB database.
    * If a collection does not exist, it is programmatically created.
    *
    * @param event The `ApplicationReadyEvent` indicating that the application is ready. This parameter is not directly used
    *              within the method's logic but signifies the event that triggered this method's execution.
    * @see org.springframework.boot.context.event.ApplicationReadyEvent
    * @see org.springframework.context.ApplicationListener
    * @see org.springframework.data.mongodb.core.MongoOperations#collectionExists(Class)
    * @see org.springframework.data.mongodb.core.MongoOperations#createCollection(Class)
    */
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        if (!mongoOperations.collectionExists(DatabaseSequence.class)) {
            mongoOperations.createCollection(DatabaseSequence.class);
        }
        if (!mongoOperations.collectionExists(Member.class)) {
            mongoOperations.createCollection(Member.class);
        }
    }

    /**
    * Configures and provides a {@link ValidatingMongoEventListener} bean to the Spring application context.
    * This listener integrates JSR-303 Bean Validation with Spring Data MongoDB, ensuring that
    * validation constraints defined on domain objects are enforced automatically before
    * objects are saved to MongoDB.
    *
    * <p>This method is annotated with {@code @Bean}, meaning Spring will automatically
    * detect it and use its return value as a bean in the application context.
    * It depends on a {@link LocalValidatorFactoryBean} which is typically provided
    * by the {@link ApplicationConfiguration#validator()} method.</p>
    *
    * @param factory The {@link LocalValidatorFactoryBean} instance, which provides
    *                the underlying JSR-303 validator used by the event listener.
    *                This parameter is automatically injected by Spring.
    * @return A new instance of {@link ValidatingMongoEventListener}, configured with the
    *         provided validator factory, ready to be registered with Spring Data MongoDB.
    * @see ApplicationConfiguration#validator()
    * @see ValidatingMongoEventListener
    * @see LocalValidatorFactoryBean
    */
    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener(final LocalValidatorFactoryBean factory) {
        return new ValidatingMongoEventListener(factory);
    }

    /**
    * Configures and provides a {@link LocalValidatorFactoryBean} as a Spring bean.
    * <p>
    * This method is annotated with {@code @Bean}, indicating that it produces a bean
    * to be managed by the Spring application context. The {@code LocalValidatorFactoryBean}
    * is a central component for integrating Bean Validation (JSR-303/JSR-349) into Spring
    * applications, allowing for declarative validation of objects.
    * </p>
    * <p>
    * The instance returned by this method can be injected into other components,
    * such as {@link org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener},
    * to enable automatic validation during data persistence operations or other
    * application logic.
    * </p>
    *
    * @return A new instance of {@link LocalValidatorFactoryBean}, configured for use
    *         within the Spring application context.
    * @see LocalValidatorFactoryBean
    * @see org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener
    * @see ApplicationConfiguration#validatingMongoEventListener(LocalValidatorFactoryBean)
    */
    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }
}
