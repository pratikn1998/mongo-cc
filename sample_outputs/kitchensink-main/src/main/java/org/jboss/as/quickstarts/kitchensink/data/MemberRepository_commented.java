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
package org.jboss.as.quickstarts.kitchensink.data;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface MemberRepository extends MongoRepository<Member, String> {
    /**
    * Defines a contract for retrieving a {@link Member} entity by its unique identifier.
    * This method is a core component of the data access layer, enabling the application
    * to query and retrieve specific member records from a persistent store.
    * It is part of the {@link MemberRepository} interface, which is responsible for
    * providing CRUD (Create, Read, Update, Delete) operations for {@link Member} entities.
    *
    * <p>
    * Implementations of this method are expected to interact with a database or other
    * data source to locate and return the {@link Member} corresponding to the provided ID.
    * </p>
    *
    * @param id The {@link BigInteger} unique identifier of the {@link Member} to be retrieved.
    * @return The {@link Member} entity if found, or {@code null} if no member with the given ID exists.
    * @see MemberRepository
    * @see MemberResourceRESTService#lookupMemberById(long)
    * @see MemberResourceRESTService#deleteMemberById(long)
    */
    Member findById(BigInteger id);

    /**
    * Defines a contract for retrieving a {@link Member} entity from a persistent store based on its email address.
    * <p>
    * This method is a core component of the data access layer, enabling the application to query and fetch
    * specific member details using a unique identifier (email). It is typically implemented by a data repository
    * or DAO (Data Access Object) to interact with a database or other data source.
    * </p>
    * <p>
    * The {@code findByEmail} method is utilized by services such as {@link org.jboss.as.quickstarts.kitchensink.rest.MemberResourceRESTService#emailAlreadyExists(String)}
    * to check for the existence of a member with a given email, which in turn supports validation logic
    * like that found in {@link org.jboss.as.quickstarts.kitchensink.rest.MemberResourceRESTService#validateMember(Member)}
    * to prevent duplicate member registrations.
    * </p>
    *
    * @param email The email address of the member to find. This is expected to be a unique identifier for a member.
    * @return The {@link Member} object if found, or {@code null} if no member with the specified email exists.
    * @see Member
    * @see org.jboss.as.quickstarts.kitchensink.rest.MemberResourceRESTService#emailAlreadyExists(String)
    * @see org.jboss.as.quickstarts.kitchensink.rest.MemberResourceRESTService#validateMember(Member)
    */
    Member findByEmail(String email);

    /**
    * Defines a query method to retrieve all {@link Member} entities from the persistence layer.
    * The results are ordered in ascending alphabetical order based on the `name` attribute of the {@link Member} entity.
    * <p>
    * This method is part of the {@link MemberRepository} interface and leverages Spring Data JPA's query derivation
    * mechanism, where the method name itself dictates the generated SQL query, abstracting the underlying database interaction.
    * </p>
    * <p>
    * It is notably used by {@link MemberListProducer#retrieveAllMembersOrderedByName()} to populate a list of members
    * for display or further processing within the application, ensuring the list is always sorted by name.
    * </p>
    *
    * @return A {@link List} of {@link Member} objects, sorted by their `name` in ascending order.
    * @see Member
    * @see MemberRepository
    * @see MemberListProducer#retrieveAllMembersOrderedByName()
    */
    List<Member> findAllByOrderByNameAsc();

    /**
    * Defines a contract for deleting a {@link Member} entity from the persistence layer.
    * This method is part of the data access operations provided by the {@link MemberRepository} interface.
    * It facilitates the removal of a member record using its unique identifier.
    *
    * <p>
    * Implementations of this method are expected to interact with a data store (e.g., a database)
    * to locate and remove the {@code Member} corresponding to the provided ID.
    * </p>
    *
    * <p>
    * This method is typically called by service layers or REST endpoints, such as
    * {@link org.jboss.as.quickstarts.kitchensink.rest.MemberResourceRESTService#deleteMemberById(long)},
    * to fulfill requests for member deletion. Before calling this method, it is common practice
    * to first verify the existence of the member using {@link #findById(BigInteger)} to handle
    * cases where the member does not exist.
    * </p>
    *
    * @param id The {@link BigInteger} identifier of the member to be deleted.
    * @return The {@link Member} object that was deleted, or {@code null} if no member with the given ID was found.
    *         The return value allows for confirmation of the deletion or further processing of the deleted entity.
    * @see MemberRepository
    * @see org.jboss.as.quickstarts.kitchensink.rest.MemberResourceRESTService#deleteMemberById(long)
    * @see #findById(BigInteger)
    * @see #deleteMemberByEmail(String)
    */
    Member deleteMemberById(BigInteger id);

    /**
    * Deletes a {@link Member} entity from the persistence layer based on their unique email address.
    * This method defines a contract for data access operations within the {@link MemberRepository} interface,
    * providing a mechanism to remove member records from the system.
    *
    * @param email The unique email address of the member to be deleted.
    * @return The {@link Member} object that was deleted, or {@code null} if no member with the given email was found.
    *         The exact behavior for the return value (e.g., returning the deleted entity vs. void) depends on the
    *         specific implementation of this repository method.
    * @see MemberRepository
    * @see Member
    * @see #findByEmail(String)
    * @see #deleteMemberById(java.math.BigInteger)
    */
    Member deleteMemberByEmail(String email);
}
