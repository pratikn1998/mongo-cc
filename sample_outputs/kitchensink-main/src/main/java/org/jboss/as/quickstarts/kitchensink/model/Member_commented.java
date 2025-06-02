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
package org.jboss.as.quickstarts.kitchensink.model;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigInteger;

/**
* Represents a Member entity, designed for persistence in a MongoDB collection named "members".
* This class implements {@link Serializable}, allowing its instances to be converted into a byte stream
* for storage or transmission across networks.
* <p>
* It defines core attributes for a member, including a unique identifier, email, name, and phone number.
* Each attribute is annotated with validation constraints to ensure data integrity upon creation or update.
* The `email` field is specifically marked as unique and indexed to prevent duplicate entries in the database.
* </p>
* <p>
* This class serves as a foundational data structure within the application, typically used by data access objects
* (DAOs) or repositories, such as {@code MemberRepository}, to manage member information.
* It is also consumed by RESTful services like {@link MemberResourceRESTService} for exposing member data
* and by service layers like {@link MemberRegistration} for business logic related to member management.
* </p>
*
* <p><b>Fields:</b></p>
* <ul>
*   <li>{@code SEQUENCE_NAME}: A constant string used to identify the sequence for generating unique IDs for members.</li>
*   <li>{@code id}: A {@link BigInteger} representing the unique identifier for the member. Annotated with {@code @Id} for MongoDB primary key mapping.</li>
*   <li>{@code email}: A {@link String} storing the member's email address. It is {@code @NotEmpty}, {@code @Email}, and {@code @Indexed(unique = true)}.</li>
*   <li>{@code name}: A {@link String} storing the member's name. It is {@code @NotEmpty}, has a {@code @Size} constraint (1 to 25 characters), and a {@code @Pattern} to ensure it does not contain numbers.</li>
*   <li>{@code phoneNumber}: A {@link String} storing the member's phone number. It is {@code @NotNull}, has a {@code @Size} constraint (10 to 12 characters), and {@code @Digits} to ensure it contains only digits.</li>
* </ul>
*
* <p><b>Key Methods:</b></p>
* <ul>
*   <li><code>getId()</code>: Retrieves the unique identifier of the member.</li>
*   <li><code>setId(BigInteger id)</code>: Sets the unique identifier of the member.</li>
*   <li><code>getEmail()</code>: Retrieves the email address of the member.</li>
*   <li><code>setEmail(String email)</code>: Sets the email address of the member.</li>
*   <li><code>getName()</code>: Retrieves the name of the member.</li>
*   <li><code>setName(String name)</code>: Sets the name of the member.</li>
*   <li><code>getPhoneNumber()</code>: Retrieves the phone number of the member.</li>
*   <li><code>setPhoneNumber(String phoneNumber)</code>: Sets the phone number of the member.</li>
* </ul>
*
* @see java.io.Serializable
* @see org.springframework.data.mongodb.core.mapping.Document
* @see org.springframework.data.annotation.Id
* @see org.springframework.data.annotation.Transient
* @see org.springframework.data.mongodb.core.index.Indexed
* @see javax.validation.constraints.NotEmpty
* @see javax.validation.constraints.Email
* @see javax.validation.constraints.Size
* @see javax.validation.constraints.Pattern
* @see javax.validation.constraints.NotNull
* @see javax.validation.constraints.Digits
* @see org.jboss.as.quickstarts.kitchensink.rest.MemberResourceRESTService
* @see org.jboss.as.quickstarts.kitchensink.service.MemberRegistration
*/
@Document(collection = "members")
public class Member implements Serializable {

    @Transient
    public static final String SEQUENCE_NAME = "members_sequence";

    @Id
    private BigInteger id;

    @NotEmpty
    @Email
    @Indexed(unique = true)
    private String email;

    @NotEmpty
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
    private String name;

    @NotNull
    @Size(min = 10, max = 12)
    @Digits(fraction = 0, integer = 12)
    private String phoneNumber;

    /**
    * Retrieves the unique identifier of this Member.
    * <p>
    * This method provides read-only access to the `id` field, which is of type {@link BigInteger}.
    * The ID serves as a primary key or unique identifier for the Member entity within the system,
    * likely used for persistence, lookup, and identification purposes.
    * </p>
    *
    * @return The {@link BigInteger} representing the unique identifier of this Member.
    * @see Member#setId(BigInteger)
    */
    public BigInteger getId() {
        return id;
    }

    /**
    * Sets the unique identifier for this Member.
    * This method updates the `id` field of the Member object with the provided BigInteger value.
    * It is typically used when creating a new Member or when retrieving a Member from a data store
    * where the ID has been assigned.
    *
    * @param id The BigInteger value representing the unique identifier to be set for this Member.
    */
    public void setId(BigInteger id) {
        this.id = id;
    }

    /**
    * Retrieves the email address of this {@link Member}.
    * <p>
    * This method provides read access to the {@code email} field, which stores the member's email address.
    * It is a standard getter method, essential for encapsulating the member's data and allowing other
    * parts of the application to access this information.
    * </p>
    * <p>
    * The email address is a unique identifier for a member and is often used for lookup operations,
    * for example, by the {@code MemberRepository.findByEmail(String email)} method to retrieve a
    * {@link Member} instance based on their email.
    * </p>
    *
    * @return The email address of the member as a {@link String}.
    * @see Member#setEmail(String)
    * @see org.jboss.as.quickstarts.kitchensink.data.MemberRepository#findByEmail(String)
    */
    public String getEmail() {
        return email;
    }

    /**
    * Sets the email address for this Member.
    * <p>
    * This method updates the `email` field of the Member object with the provided value.
    * It is a standard setter method, adhering to JavaBean conventions, and is crucial for
    * modifying a Member's contact information within the application.
    * </p>
    *
    * @param email The new email address to be set for the member. This should be a valid
    *              email format, though validation is typically handled at a higher layer
    *              (e.g., during data input or persistence).
    * @see Member#getEmail()
    */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
    * Retrieves the name of this member.
    * <p>
    * This method provides read access to the `name` field, which stores the member's full name.
    * It is a fundamental part of the `Member` model, supporting data encapsulation by
    * controlling access to the `name` property and ensuring that other parts of the
    * application can retrieve a member's name in a standardized way.
    * </p>
    *
    * @return The name of the member as a {@link String}.
    */
    public String getName() {
        return name;
    }

    /**
    * Sets the name of this Member.
    * This method is a simple setter designed to update the `name` field of a `Member` object.
    * It directly assigns the provided `String` argument to the instance variable `this.name`.
    * This method is a fundamental part of the `Member` class's encapsulation, allowing controlled modification
    * of a member's name attribute. Architecturally, it supports the data integrity of `Member` instances
    * by providing a public interface for name assignment.
    *
    * @param name The new name to be set for this Member.
    */
    public void setName(String name) {
        this.name = name;
    }

    /**
    * Retrieves the phone number of the member.
    * <p>
    * This method provides read-only access to the `phoneNumber` field, encapsulating the internal
    * representation of the member's phone number. It is a standard accessor (getter) method
    * used to retrieve contact information associated with a {@link Member} object.
    * </p>
    *
    * @return The phone number of the member as a {@link String}.
    * @see Member#setPhoneNumber(String)
    * @see Member#getEmail()
    * @see Member#getName()
    */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
    * Sets the phone number of this Member.
    * This method updates the `phoneNumber` field with the provided value.
    * It is a standard setter method used to modify the phone contact information associated with a Member object.
    *
    * @param phoneNumber The new phone number to be set for this Member.
    */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
