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
package org.jboss.as.quickstarts.kitchensink.service;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.DatabaseSequence;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Objects;
import java.util.logging.Logger;

/**
* Represents a service component responsible for registering new {@link Member} objects within the application.
* <p>
* This class is annotated with {@code @Service}, indicating it is a Spring service component.
* It orchestrates the process of assigning a unique ID to a member and persisting the member
* data using a {@link MemberRepository}. It also utilizes {@link MongoOperations} to manage
* the generation of unique sequence numbers for member IDs.
* </p>
*
* <p><b>Fields:</b></p>
* <ul>
*   <li><code>log</code>: A {@link Logger} instance used for logging messages related to member registration operations.</li>
*   <li><code>mongoOperations</code>: An instance of {@link MongoOperations} for direct interaction with MongoDB,
*   specifically used for sequence generation.</li>
*   <li><code>memberRepository</code>: An instance of {@link MemberRepository} for performing CRUD operations
*   on {@link Member} entities.</li>
* </ul>
*
* <p><b>Key Methods:</b></p>
* <ul>
*   <li>{@link #MemberRegistration(MongoOperations, MemberRepository, MongoClient)}: Constructor for dependency injection.</li>
*   <li>{@link #register(Member)}: Handles the registration of a new member, including ID generation and persistence.</li>
*   <li>{@link #generateSequence(String)}: A private utility method to generate unique sequence numbers for member IDs.</li>
* </ul>
*
* @see org.springframework.stereotype.Service
* @see org.springframework.data.mongodb.core.MongoOperations
* @see org.jboss.as.quickstarts.kitchensink.data.MemberRepository
* @see org.jboss.as.quickstarts.kitchensink.model.Member
*/
@Service
public class MemberRegistration {
    private final Logger log;

    private final MongoOperations mongoOperations;

    private final MemberRepository memberRepository;

    @Autowired
    public MemberRegistration(final MongoOperations mongoOperations, final MemberRepository memberRepository, MongoClient mongo) {
        log = Logger.getLogger(getClass().getName());
        this.mongoOperations = mongoOperations;
        this.memberRepository = memberRepository;
    }

    /**
    * Registers a new {@link Member} in the system by assigning a unique ID and persisting it to the database.
    * <p>
    * This method first generates a unique sequence ID for the member using {@link #generateSequence(String)}
    * with {@link Member#SEQUENCE_NAME}. This ensures that each registered member has a distinct identifier.
    * After assigning the ID, it attempts to insert the member data into the database via the
    * {@code memberRepository.insert(member)} method.
    * </p>
    * <p>
    * Error handling is in place to catch {@link com.mongodb.MongoWriteException} which may occur during the
    * database insertion process. If such an exception is caught, it indicates a problem with writing to MongoDB,
    * and a generic {@link Exception} is thrown with the localized message of the original exception.
    * </p>
    * <p>
    * This method is typically called by REST endpoints, such as
    * {@link org.jboss.as.quickstarts.kitchensink.rest.MemberResourceRESTService#createMember(Member)},
    * to handle the persistence aspect of member creation. It is also tested by
    * {@link org.jboss.as.quickstarts.kitchensink.test.MemberRegistrationIT#testRegister()}.
    * </p>
    *
    * @param member The {@link Member} object to be registered. This object will have its ID field populated
    *               upon successful registration.
    * @throws Exception If an error occurs during the ID generation or the database insertion process,
    *                   particularly if a {@link com.mongodb.MongoWriteException} is encountered.
    */
    public void register(Member member) throws Exception {
        member.setId(generateSequence(Member.SEQUENCE_NAME));
        try {
            memberRepository.insert(member);
        } catch (MongoWriteException e) {
            throw new Exception(e.getLocalizedMessage());
        }

    }

    /**
    * Generates a unique sequence number for a given sequence name by interacting with a MongoDB instance.
    * This method atomically increments a named sequence counter stored in the "database_sequences" collection.
    * If the sequence does not exist, it is created with an initial value of 1.
    *
    * @param sequenceName The name of the sequence to generate a number for (e.g., "member_id_sequence").
    * @return A {@link BigInteger} representing the next unique sequence number. Returns {@link BigInteger#ONE}
    *         if the counter object is null after the find and modify operation, which should ideally not happen
    *         due to the upsert option.
    * @see org.jboss.as.quickstarts.kitchensink.model.DatabaseSequence
    * @see org.springframework.data.mongodb.core.MongoOperations#findAndModify(org.springframework.data.mongodb.core.query.Query, org.springframework.data.mongodb.core.query.Update, org.springframework.data.mongodb.core.FindAndModifyOptions, java.lang.Class)
    */
    private BigInteger generateSequence(String sequenceName) {
        DatabaseSequence counter = mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(sequenceName)),
                new Update().inc("sequence", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                DatabaseSequence.class
        );
        return !Objects.isNull(counter) ? counter.getSequence() : BigInteger.ONE;
    }
}
