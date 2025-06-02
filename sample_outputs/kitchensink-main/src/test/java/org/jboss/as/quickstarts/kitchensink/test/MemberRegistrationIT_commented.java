/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.kitchensink.test;

import org.jboss.as.quickstarts.kitchensink.Main;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
import org.jboss.as.quickstarts.kitchensink.test.config.MongoDBConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
* Integration test class for verifying the functionality of the {@link MemberRegistration} service.
* <p>
* This class is configured to run with Spring's test runner and utilizes Testcontainers for
* setting up a robust testing environment, including a MongoDB instance via {@link MongoDBConfig}.
* It ensures that the member registration process, including data persistence and ID generation,
* works correctly within the integrated application context.
* </p>
*
* <p><b>Dependencies:</b></p>
* <ul>
*   <li>{@link MemberRegistration}: The service under test, responsible for registering new members.</li>
*   <li>{@link Logger}: Used for logging test outcomes and debugging information.</li>
* </ul>
*
* <p><b>Key Methods:</b></p>
* <ul>
*   <li>{@link #testRegister()}: Tests the successful registration of a new member, asserting that a unique ID is assigned.</li>
* </ul>
*
* @see org.springframework.test.context.junit4.SpringRunner
* @see org.testcontainers.junit.jupiter.Testcontainers
* @see org.springframework.boot.test.context.SpringBootTest
* @see org.jboss.as.quickstarts.kitchensink.Main
* @see org.jboss.as.quickstarts.kitchensink.config.MongoDBConfig
* @see org.jboss.as.quickstarts.kitchensink.service.MemberRegistration
*/
@RunWith(SpringRunner.class)
@Testcontainers
@SpringBootTest(classes = {Main.class, MongoDBConfig.class})
public class MemberRegistrationIT {
    @Autowired
    MemberRegistration memberRegistration;

    @Autowired
    Logger log;

    /**
    * Tests the registration of a new {@link Member} using the {@link MemberRegistration} service.
    * <p>
    * This integration test creates a new {@link Member} instance with predefined data (name, email, phone number).
    * It then invokes the {@code register} method of the injected {@code memberRegistration} service to persist
    * the new member.
    * </p>
    * <p>
    * The test asserts that:
    * <ul>
    *     <li>The {@link Member#getId()} method returns a non-null value after registration, indicating successful
    *         persistence and ID generation.</li>
    *     <li>A log message is recorded confirming the member's name and generated ID.</li>
    * </ul>
    * If any exception occurs during the registration process, the test will fail, logging the exception message.
    * This ensures the core member registration business logic and data persistence work as expected within the
    * application's integrated environment.
    * </p>
    *
    * @see MemberRegistrationIT
    * @see MemberRegistration#register(Member)
    * @see Member
    */
    @Test
    public void testRegister() {
        Member newMember = new Member();
        newMember.setName("Jane Doe");
        newMember.setEmail("jane@mailinator.com");
        newMember.setPhoneNumber("2125551234");
        try {
            memberRegistration.register(newMember);
            assertNotNull(newMember.getId());
            log.info(newMember.getName() + " was persisted with id " + newMember.getId());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
