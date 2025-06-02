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
package org.jboss.as.quickstarts.kitchensink.rest;

import jakarta.validation.ValidationException;
import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.util.List;
import java.util.logging.Logger;

/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the members table.
 */
/**
* `MemberResourceRESTService` is a Spring `@RestController` that exposes RESTful API endpoints for managing `Member` resources.
* It acts as the entry point for client requests related to member operations, including listing, looking up by ID,
* deleting, and creating new members.
*
* This class orchestrates interactions between the presentation layer (REST API), the data access layer (`MemberRepository`),
* and the business logic layer (`MemberRegistration`). It handles request mapping, data validation, and error responses.
*
* <p><b>Fields:</b></p>
* <ul>
*   <li>`Logger log`: Used for logging messages and exceptions within the service.</li>
*   <li>`MemberRepository repository`: Provides data access operations for `Member` entities, such as finding all members,
*       finding by ID, finding by email, and deleting members.</li>
*   <li>`MemberRegistration registration`: Encapsulates the business logic for registering new members.</li>
* </ul>
*
* <p><b>Key Methods:</b></p>
* <ul>
*   <li>`MemberResourceRESTService(Logger log, MemberRepository repository, MemberRegistration registration)`:
*       Constructor that injects dependencies required by the service.</li>
*   <li>`listAllMembers()`: Handles GET requests to `/api/members` to retrieve a list of all registered members.
*       It delegates to `repository.findAll()`.</li>
*   <li>`lookupMemberById(long id)`: Handles GET requests to `/api/members/{id}` to retrieve a specific member by their ID.
*       It calls `repository.findById()` and throws a `ResponseStatusException` with `HttpStatus.NOT_FOUND` if the member is not found.</li>
*   <li>`deleteMemberById(long id)`: Handles DELETE requests to `/api/members/{id}` to remove a member by their ID.
*       It first checks if the member exists using `repository.findById()` and then calls `repository.deleteMemberById()`.
*       Throws `ResponseStatusException` with `HttpStatus.NOT_FOUND` if the member does not exist.</li>
*   <li>`createMember(Member member)`: Handles POST requests to `/api/members` to create a new member.
*       It performs validation using `validateMember()` and then registers the member using `registration.register()`.
*       It handles `ValidationException` (e.g., duplicate email) by returning `HttpStatus.CONFLICT` and other exceptions
*       by returning `HttpStatus.INTERNAL_SERVER_ERROR`.</li>
*   <li>`validateMember(Member member)`: A private helper method that validates the uniqueness of a member's email address.
*       It calls `emailAlreadyExists()` and throws a `ValidationException` if the email is already in use.</li>
*   <li>`emailAlreadyExists(String email)`: Checks if a member with the given email already exists in the repository
*       by calling `repository.findByEmail()`. Returns `true` if a member is found, `false` otherwise.</li>
* </ul>
*
* @see org.springframework.web.bind.annotation.RestController
* @see org.springframework.web.bind.annotation.GetMapping
* @see org.springframework.web.bind.annotation.PostMapping
* @see org.springframework.web.bind.annotation.DeleteMapping
* @see org.springframework.web.bind.annotation.ResponseBody
* @see org.springframework.web.bind.annotation.ResponseStatus
* @see org.springframework.web.bind.annotation.RequestBody
* @see org.springframework.web.bind.annotation.PathVariable
* @see org.springframework.web.bind.annotation.Autowired
* @see org.springframework.web.server.ResponseStatusException
* @see org.springframework.http.HttpStatus
* @see org.jboss.as.quickstarts.kitchensink.data.MemberRepository
* @see org.jboss.as.quickstarts.kitchensink.model.Member
* @see org.jboss.as.quickstarts.kitchensink.service.MemberRegistration
* @see org.jboss.as.quickstarts.kitchensink.util.ValidationException
*/
@RestController
public class MemberResourceRESTService {
    private final Logger log;
    private final MemberRepository repository;
    private final MemberRegistration registration;


    @Autowired
    public MemberResourceRESTService(Logger log, MemberRepository repository, MemberRegistration registration) {
        this.log = log;
        this.repository = repository;
        this.registration = registration;
    }

    /**
    * Retrieves a list of all {@link Member} entities.
    * <p>
    * This method serves as a RESTful endpoint, mapped to HTTP GET requests at the "/api/members" path.
    * It queries the underlying data store to fetch all available members.
    * </p>
    *
    * @return A {@link List} of {@link Member} objects, representing all members found in the system.
    *         The list is returned as the response body.
    * @see org.jboss.as.quickstarts.kitchensink.rest.MemberResourceRESTService#lookupMemberById(long)
    * @see org.jboss.as.quickstarts.kitchensink.data.MemberListProducer#retrieveAllMembersOrderedByName()
    * @see org.jboss.as.quickstarts.kitchensink.controller.MemberController#getMembers()
    */
    @GetMapping({"/api/members"})
    @ResponseBody
    public List<Member> listAllMembers() {
        return repository.findAll();
    }

    /**
    * Retrieves a single {@link Member} by their unique identifier.
    * This method handles HTTP GET requests to the `/api/members/{id}` endpoint, where `{id}` must be a numeric value.
    * It serves as a RESTful endpoint to fetch specific member details.
    *
    * @param id The unique identifier (ID) of the member to be retrieved. This value is extracted from the URL path.
    * @return The {@link Member} object corresponding to the provided ID. The object is returned as the response body.
    * @throws ResponseStatusException If no member is found for the given ID, a {@link HttpStatus#NOT_FOUND NOT_FOUND} (404)
    *                                 status is returned with a "Member not found" message. This exception is logged
    *                                 using the internal logger.
    *
    * <h3>Method Logic:</h3>
    * <ol>
    *     <li>Converts the `long` type `id` to a `BigInteger` to match the `repository`'s expected parameter type.</li>
    *     <li>Calls {@code repository.findById(BigInteger.valueOf(id))} to query the data store for a member with the specified ID.</li>
    *     <li>Checks if the returned {@link Member} object is {@code null}.</li>
    *     <li>If the member is {@code null}, indicating no member was found, a {@link ResponseStatusException} with
    *         {@link HttpStatus#NOT_FOUND} is created, logged, and then thrown. The log message incorrectly references
    *         "deleteMemberById" but is intended for "lookupMemberById".</li>
    *     <li>If a member is found, the {@link Member} object is returned.</li>
    * </ol>
    *
    * @see MemberResourceRESTService#deleteMemberById(long)
    * @see MemberRepository#findById(java.math.BigInteger)
    */
    @GetMapping("/api/members/{id:[0-9]+}")
    @ResponseBody
    public Member lookupMemberById(@PathVariable("id") long id) {
        Member member = repository.findById(BigInteger.valueOf(id));
        if (member == null) {
            ResponseStatusException e = new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found");
            log.throwing(MemberResourceRESTService.class.getName(), "deleteMemberById", e);
            throw e;
        }
        return member;
    }

    /**
    * Handles HTTP DELETE requests to remove a member by their ID.
    * This method is mapped to the `/api/members/{id}` endpoint, where `{id}` must be a numeric value.
    * <p>
    * It first attempts to find the member using the provided ID. If the member does not exist,
    * it throws a {@link org.springframework.web.server.ResponseStatusException} with an HTTP 404 (Not Found) status.
    * If the member is found, it proceeds to delete the member from the repository.
    * </p>
    *
    * @param id The unique identifier of the member to be deleted. This value is extracted from the URL path.
    * @throws ResponseStatusException If no member is found for the given ID, an HTTP 404 Not Found exception is thrown.
    *                                 The exception is logged before being thrown.
    * @see MemberResourceRESTService#lookupMemberById(long)
    * @see org.jboss.as.quickstarts.kitchensink.data.MemberRepository#findById(java.math.BigInteger)
    * @see org.jboss.as.quickstarts.kitchensink.data.MemberRepository#deleteMemberById(java.math.BigInteger)
    */
    @DeleteMapping("/api/members/{id:[0-9]+}")
    public void deleteMemberById(@PathVariable("id") long id) {
        Member member = repository.findById(BigInteger.valueOf(id));
        if (member == null) {
            ResponseStatusException e = new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found");
            log.throwing(MemberResourceRESTService.class.getName(), "deleteMemberById", e);
            throw e;
        }
        repository.deleteMemberById(BigInteger.valueOf(id));
    }

    /**
     * Creates a new member from the values provided. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.
     */
    /**
    * Handles the creation of a new {@link Member} entity via a POST request to the `/api/members` endpoint.
    * This method performs validation on the incoming {@link Member} object and then attempts to register it.
    *
    * <p>The process involves:</p>
    * <ol>
    *     <li>Validating the provided {@code member} data using {@link #validateMember(Member)}. This step
    *         ensures data integrity, particularly checking for unique email addresses.</li>
    *     <li>If validation passes, the {@code member} is registered using the {@code registration.register(member)}
    *         method, which persists the new member to the data store.</li>
    *     <li>Error handling for specific scenarios:
    *         <ul>
    *             <li>If a {@link ValidationException} occurs (e.g., due to a duplicate email), a {@link ResponseStatusException}
    *                 with {@link HttpStatus#CONFLICT} is thrown, indicating that the request could not be processed
    *                 because of a conflict with existing data.</li>
    *             <li>For any other unexpected exceptions during the creation process, a {@link ResponseStatusException}
    *                 with {@link HttpStatus#INTERNAL_SERVER_ERROR} is thrown, providing a generic error message.</li>
    *         </ul>
    *     </li>
    * </ol>
    *
    * @param member The {@link Member} object received in the request body, containing the details for the new member.
    * @return The created {@link Member} object if the registration is successful.
    * @throws ResponseStatusException If a {@link ValidationException} occurs (status {@link HttpStatus#CONFLICT})
    *                                 or if any other unexpected error occurs during member creation (status {@link HttpStatus#INTERNAL_SERVER_ERROR}).
    * @see MemberResourceRESTService#validateMember(Member)
    * @see org.jboss.as.quickstarts.kitchensink.service.MemberRegistration#register(Member)
    */
    @PostMapping("/api/members")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Member createMember(@RequestBody Member member) {
        try {
            validateMember(member);
            registration.register(member);
        } catch (ValidationException e) {
            ResponseStatusException error = new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use by another member");
            log.throwing(this.getClass().getName(), "createMember", error);
            throw error;
        } catch (Exception e) {
            ResponseStatusException error = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            log.throwing(this.getClass().getName(), "createMember", error);
            throw error;
        }
        return member;
    }

    /**
     * <p>
     * Validates the given Member variable and throws validation exception if the error is caused because an existing member with the same email is registered
     * </p>
     *
     * @param member Member to be validated
     * @throws ValidationException If member with the same email already exists
     */
    /**
    * Validates the given {@link Member} object, primarily by checking the uniqueness of its email address.
    * This method is a private utility used internally by the REST service to enforce business rules before
    * a member is registered.
    * <p>
    * It retrieves the email from the provided {@code Member} and then calls {@link #emailAlreadyExists(String)}
    * to determine if another member with the same email is already registered in the system.
    * If a duplicate email is found, a {@link ValidationException} is thrown, indicating that the member
    * cannot be created with the provided email.
    * </p>
    * <p>
    * This validation step is crucial for maintaining data integrity and preventing duplicate entries
    * based on email addresses.
    * </p>
    *
    * @param member The {@link Member} object to be validated.
    * @throws ValidationException If a member with the same email address already exists in the system.
    *                             This exception is caught by calling methods (e.g., {@link MemberResourceRESTService#createMember(Member)})
    *                             and typically translated into an appropriate HTTP response status.
    * @see MemberResourceRESTService#emailAlreadyExists(String)
    * @see MemberResourceRESTService#createMember(Member)
    */
    private void validateMember(Member member) throws ValidationException {
        // Check the uniqueness of the email address
        String email = member.getEmail();
        if (emailAlreadyExists(email)) {
            ValidationException e = new ValidationException("Member already exists using email: " + email);
            log.throwing(this.getClass().getName(), "validateMember", e);
            throw e;
        }
    }

    /**
     * Checks if a member with the same email address is already registered. Returns a more friendly error response
     *
     * @param email The email to check
     * @return True if the email already exists, and false otherwise
     */
    /**
    * Checks if a member with the given email address already exists in the system.
    * This method queries the {@link MemberRepository} to determine the uniqueness of an email.
    *
    * @param email The email address to check for existence.
    * @return {@code true} if a member with the specified email already exists; {@code false} otherwise.
    *         Returns {@code false} even if an exception occurs during the repository lookup, as exceptions are ignored.
    * @see MemberResourceRESTService#validateMember(Member)
    * @see org.jboss.as.quickstarts.kitchensink.data.MemberRepository#findByEmail(String)
    */
    public boolean emailAlreadyExists(String email) {
        Member member = null;
        try {
            member = repository.findByEmail(email);
        } catch (Exception e) {
            // ignore
        }
        return member != null;
    }
}
