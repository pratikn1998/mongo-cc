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
package org.jboss.as.quickstarts.kitchensink.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import org.jboss.as.quickstarts.kitchensink.data.MemberListProducer;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
* Represents a JSF (JavaServer Faces) controller responsible for managing member-related interactions within the application's view layer.
* <p>
* This class is annotated with {@code @Controller} and {@code @ViewScoped}, indicating its role as a Spring MVC controller
* and that its lifecycle is tied to the current view. It orchestrates member registration and retrieval by collaborating
* with {@link MemberRegistration} for persistence operations and {@link MemberListProducer} for data fetching.
* </p>
* <p>
* The controller maintains the state of a new member being registered (`newMember`) and a list of existing members (`members`).
* It refreshes this data upon initialization or successful registration, ensuring the UI reflects the current state.
* It also handles user input for registration, including basic validation and error reporting via {@link FacesContext},
* providing a robust user experience.
* </p>
*
* <p><b>Fields:</b></p>
* <ul>
*   <li>{@code private final MemberRegistration memberRegistration}: An injected service responsible for handling the business logic of member registration.</li>
*   <li>{@code private final MemberListProducer memberListProducer}: An injected producer responsible for providing and managing the list of members.</li>
*   <li>{@code private Member newMember}: Holds the data for a new member being registered through the UI.</li>
*   <li>{@code private List<Member> members}: Stores the list of all members retrieved from the system, typically displayed in the UI.</li>
* </ul>
*
* <p><b>Key Methods:</b></p>
* <ul>
*   <li>{@code MemberController(MemberRegistration memberRegistration, MemberListProducer memberListProducer)}: Constructor that injects necessary dependencies.</li>
*   <li>{@code refresh()}: Initializes `newMember` and refreshes the list of `members` by calling `MemberListProducer.retrieveAllMembersOrderedByName()` and `MemberListProducer.getMembers()`.</li>
*   <li>{@code register()}: Handles the registration process for a new member, including validation, calling `MemberRegistration.register()`, and providing UI feedback via `FacesContext`.</li>
*   <li>{@code getRootErrorMessage(Exception e)}: A utility method to extract the root cause message from an exception for display.</li>
*   <li>{@code getMembers()}: Returns the list of members.</li>
*   <li>{@code setMembers(List<Member> members)}: Sets the list of members.</li>
*   <li>{@code getNewMember()}: Returns the `Member` object currently being used for new member registration.</li>
*   <li>{@code setNewMember(Member newMember)}: Sets the `Member` object for new member registration.</li>
* </ul>
*
* @see org.springframework.stereotype.Controller
* @see org.springframework.web.context.annotation.ViewScoped
* @see org.springframework.beans.factory.annotation.Autowired
* @see javax.annotation.PostConstruct
* @see javax.faces.context.FacesContext
* @see javax.faces.application.FacesMessage
* @see org.jboss.as.quickstarts.kitchensink.service.MemberRegistration
* @see org.jboss.as.quickstarts.kitchensink.util.MemberListProducer
* @see org.jboss.as.quickstarts.kitchensink.model.Member
*/
@Controller
@ViewScoped
public class MemberController {
    private final MemberRegistration memberRegistration;
    private final MemberListProducer memberListProducer;
    private Member newMember;
    private List<Member> members;

    @Autowired
    public MemberController(MemberRegistration memberRegistration, MemberListProducer memberListProducer) {
        this.memberRegistration = memberRegistration;
        this.memberListProducer = memberListProducer;
    }

    /**
    * Initializes the {@code MemberController} by preparing a new {@code Member} instance for data entry
    * and refreshing the list of existing members. This method is annotated with {@code @PostConstruct},
    * ensuring it is executed immediately after the {@code MemberController} bean has been constructed.
    * <p>
    * The refresh process involves three main steps:
    * <ol>
    *     <li>A new, empty {@code Member} object is instantiated and assigned to {@code newMember}. This
    *         prepares the controller for receiving new member data, typically for a form submission.</li>
    *     <li>The {@code memberListProducer.retrieveAllMembersOrderedByName()} method is called. This
    *         triggers the {@code MemberListProducer} to fetch all members from the data repository,
    *         ordering them by name in ascending order. This ensures the internal list within the producer
    *         is up-to-date.</li>
    *     <li>The updated list of members is then retrieved from the {@code memberListProducer} using
    *         {@code memberListProducer.getMembers()} and assigned to the {@code members} field of this
    *         controller. This makes the current, ordered list of members available for display or further
    *         processing within the controller's scope.</li>
    * </ol>
    * This method is crucial for ensuring that the member list displayed to the user is always current
    * upon component initialization or when a refresh operation is explicitly triggered.
    * </p>
    *
    * @see org.jboss.as.quickstarts.kitchensink.data.MemberListProducer#retrieveAllMembersOrderedByName()
    * @see org.jboss.as.quickstarts.kitchensink.data.MemberListProducer#getMembers()
    * @see org.jboss.as.quickstarts.kitchensink.model.Member
    */
    @PostConstruct
    public void refresh() {
        newMember = new Member();
        memberListProducer.retrieveAllMembersOrderedByName();
        members = memberListProducer.getMembers();
    }

    /**
    * Handles the registration of a new member within the JSF application context.
    * This method performs client-side validation for empty fields, delegates the
    * actual registration to the {@code MemberRegistration} service, and provides
    * user feedback via JSF {@code FacesMessage}s.
    *
    * <p>
    * The method first obtains the current {@code FacesContext} to manage UI messages.
    * It then checks if any of the required fields (name, email, phone number) of the
    * {@code newMember} object are empty. If validation fails, an error message is
    * added to the {@code FacesContext}.
    * </p>
    * <p>
    * If the initial validation passes, it attempts to register the {@code newMember}
    * by calling {@code memberRegistration.register(newMember)}.
    * <ul>
    *     <li>Upon successful registration, an informational {@code FacesMessage} is
    *         added, and the {@link #refresh()} method is called to update the list
    *         of members displayed in the UI.</li>
    *     <li>If an exception occurs during registration, the method catches it,
    *         extracts a user-friendly root error message using {@link #getRootErrorMessage(Exception)},
    *         and adds an error {@code FacesMessage} to inform the user of the failure.</li>
    * </ul>
    * </p>
    *
    * @throws Exception if an unexpected error occurs during the registration process
    *                   that is not caught and handled internally.
    * @see MemberRegistration#register(Member)
    * @see #refresh()
    * @see #getRootErrorMessage(Exception)
    * @see FacesContext#getCurrentInstance()
    * @see FacesContext#addMessage(String, FacesMessage)
    * @see FacesMessage
    */
    public void register() throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (newMember.getName().isEmpty() || newMember.getEmail().isEmpty() || newMember.getPhoneNumber().isEmpty()) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid member details", "One or more member details is blank"));
        }
        try {
            memberRegistration.register(newMember);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
            facesContext.addMessage(null, msg);
            refresh();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registration unsuccessful");
            facesContext.addMessage(null, msg);
        }
    }

    /**
    * Extracts the most specific error message from an exception chain.
    * <p>
    * This private helper method traverses the {@link Throwable#getCause() cause} hierarchy of a given
    * {@link Exception} to find the root cause or the deepest available message. It iteratively updates
    * the error message with the {@link Throwable#getLocalizedMessage() localized message} of each
    * cause encountered, ensuring that the final returned message is the most granular one from the
    * exception chain. If the input exception is null, a default "Registration failed" message is returned.
    * </p>
    * <p>
    * This method is primarily used by the {@link MemberController#register()} method to provide
    * user-friendly error feedback when a member registration fails due to underlying exceptions.
    * </p>
    *
    * @param e The exception from which to extract the root error message.
    * @return A {@code String} representing the most specific error message found in the exception's cause chain,
    *         or a default "Registration failed" message if the input exception is null.
    */
    private String getRootErrorMessage(Exception e) {
        String errorMessage = "Registration failed";
        if (e == null) {
            return errorMessage;
        }

        Throwable cause = e;
        while (cause != null) {
            errorMessage = cause.getLocalizedMessage();
            cause = cause.getCause();
        }

        return errorMessage;
    }

    /**
    * Retrieves the current list of members managed by this controller.
    * <p>
    * This method acts as a simple accessor for the internal `members` field, providing a read-only view
    * of the `Member` objects. It is typically used by the presentation layer or other components
    * that need to display or process the list of members.
    * </p>
    * <p>
    * While this method directly returns the internal list, the actual population or manipulation
    * of this list is handled by other methods within the `MemberController`, such as `setMembers(List<Member> members)`,
    * which allows for dependency injection or state management.
    * </p>
    *
    * @return A {@link List} of {@link Member} objects currently held by this controller.
    * @see MemberController#setMembers(List)
    * @see org.jboss.as.quickstarts.kitchensink.data.MemberListProducer#getMembers()
    * @see org.jboss.as.quickstarts.kitchensink.rest.MemberResourceRESTService#listAllMembers()
    */
    public List<Member> getMembers() {
        return members;
    }

    /**
    * Sets the list of members managed by this controller.
    * This method is typically used for dependency injection or to update the internal state
    * of the controller with a new collection of {@link Member} objects.
    * It directly assigns the provided list to the `members` field, making the new list
    * available for retrieval via {@link #getMembers()}.
    *
    * @param members The {@link List} of {@link Member} objects to be set. This list will replace
    *                any previously held list of members.
    */
    public void setMembers(List<Member> members) {
        this.members = members;
    }

    /**
    * Retrieves the currently managed {@link Member} object.
    * <p>
    * This method acts as a getter for the `newMember` field, providing access to the
    * {@link Member} instance that the controller is currently working with. This
    * {@link Member} object is typically initialized in the {@link #refresh()} method
    * and can be set externally via {@link #setNewMember(Member)}.
    * </p>
    *
    * @return The {@link Member} object currently held by the controller, which may be
    *         a newly instantiated member for data entry or a member being prepared
    *         for an operation like persistence or display.
    */
    public Member getNewMember() {
        return newMember;
    }

    /**
    * Sets the `newMember` field of this controller.
    * <p>
    * This method is a simple setter used to inject or update the {@link Member} object
    * that the controller will manage or operate upon. It is typically used to prepare
    * a new {@link Member} instance for persistence (e.g., registration) or for display
    * operations within the user interface.
    * </p>
    *
    * @param newMember The {@link Member} object to be set as the current new member
    *                  instance for the controller. This object will likely be used
    *                  in subsequent operations such as registration or form submission.
    */
    public void setNewMember(Member newMember) {
        this.newMember = newMember;
    }
}
