package org.jboss.as.quickstarts.kitchensink.utils;

import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.logging.Logger;

/**
* {@code Resources} is a Spring configuration class responsible for providing common application resources
* as Spring beans. It is annotated with {@code @Configuration}, indicating that it contains bean definitions.
* <p>
* This class centralizes the creation and configuration of shared utilities, such as loggers, ensuring
* consistent instantiation and availability throughout the application's Spring context.
* </p>
*
* <p><b>Key Methods:</b></p>
* <ul>
*   <li>{@link #produceLogger(InjectionPoint)}: Creates and configures a {@link Logger} instance.</li>
* </ul>
*
* @see org.springframework.context.annotation.Configuration
* @see org.springframework.context.annotation.Bean
* @see org.springframework.context.annotation.Scope
* @see org.jboss.weld.injection.spi.InjectionPoint
* @see java.util.logging.Logger
*/
@Configuration
public class Resources {

    /**
    * Produces a {@link Logger} instance for injection, dynamically named after the class
    * into which it is being injected.
    * <p>
    * This method is annotated with {@code @Bean} and {@code @Scope("prototype")},
    * meaning that a new {@link Logger} instance will be created for each injection point
    * where a {@link Logger} is required. This ensures that log messages are correctly
    * attributed to their originating class.
    * </p>
    *
    * @param injectionPoint The {@link InjectionPoint} provides metadata about where the
    *                       logger is being injected. This is used to determine the
    *                       declaring class of the injection point.
    * @return A {@link Logger} instance whose name corresponds to the fully qualified
    *         name of the class where it is being injected.
    * @see org.jboss.as.quickstarts.kitchensink.utils.Resources
    * @see java.util.logging.Logger
    * @see org.springframework.beans.factory.InjectionPoint
    */
    @Bean
    @Scope("prototype")
    public Logger produceLogger(InjectionPoint injectionPoint) {
        Class<?> classOnWired = injectionPoint.getMember().getDeclaringClass();
        return Logger.getLogger(classOnWired.getName());
    }
}
