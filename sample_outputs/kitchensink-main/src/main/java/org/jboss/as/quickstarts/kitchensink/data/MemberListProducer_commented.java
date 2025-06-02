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

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Reception;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
* {@code MemberListProducer} is a Spring component responsible for managing and providing an ordered list of {@link Member} objects.
* It acts as a data source for member lists within the application, ensuring the data is always up-to-date and sorted by name.
* <p>
* This class is annotated with {@code @Component}, making it a Spring-managed bean. It relies on a {@link MemberRepository}
* to interact with the underlying data store for member entities.
* </p>
*
* <p><b>Fields:</b></p>
* <ul>
*   <li>{@code memberRepository}: A final instance of {@link MemberRepository} used for data access operations related to {@link Member} entities.</li>
*   <li>{@code members}: A {@link List} of {@link Member} objects, representing the cached list of all members, ordered by name.</li>
* </ul>
*
* <p><b>Key Methods:</b></p>
* <ul>
*   <li>{@code MemberListProducer(MemberRepository memberRepository)}: Constructor that injects the {@link MemberRepository} dependency.</li>
*   <li>{@code getMembers()}: Provides access to the current list of members.</li>
*   <li>{@code onMemberListChanged(Member member)}: An observer method that reacts to changes in {@link Member} objects, triggering a refresh of the member list.</li>
*   <li>{@code retrieveAllMembersOrderedByName()}: Initializes or refreshes the internal list of members by fetching them from the repository, sorted by name.</li>
* </ul>
*
* @see MemberRepository
* @see Member
* @see org.springframework.stereotype.Component
* @see jakarta.annotation.PostConstruct
* @see jakarta.enterprise.event.Observes
*/
@Component
public class MemberListProducer {
    private final MemberRepository memberRepository;
    private List<Member> members;

    @Autowired
    public MemberListProducer(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
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
    * Observes changes to {@link Member} objects and triggers a refresh of the internal member list.
    * <p>
    * This method is an event observer, specifically listening for {@link Member} events.
    * The {@code @Observes} annotation with {@code notifyObserver = Reception.IF_EXISTS} ensures
    * that this method is invoked only if an observer for the {@link Member} event already exists.
    * </p>
    * <p>
    * Upon receiving a {@link Member} event, this method calls {@link #retrieveAllMembersOrderedByName()}
    * to re-fetch the entire list of members from the repository. This ensures that the cached list
    * of members maintained by this producer is always up-to-date with the latest changes in the
    * underlying data store, reflecting any additions, updates, or deletions of individual members.
    * </p>
    *
    * @param member The {@link Member} object that triggered the event. This parameter is used by the
    *               CDI event system to pass the event payload, though its specific content is not
    *               directly used within this method's logic beyond triggering the refresh.
    * @see MemberListProducer#retrieveAllMembersOrderedByName()
    * @see Member
    */
    public void onMemberListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Member member) {
        retrieveAllMembersOrderedByName();
    }

    /**
    * Initializes the list of members by retrieving all members from the repository, ordered alphabetically by name.
    * This method is annotated with {@code @PostConstruct}, ensuring it is executed automatically after the
    * `MemberListProducer` bean has been constructed and all its dependencies have been injected.
    * <p>
    * It populates the `members` field of this producer, making the ordered list of members available
    * for other components that inject `MemberListProducer`.
    * </p>
    * <p>
    * This method is also called by {@link #onMemberListChanged(Member)} to refresh the list whenever a member
    * is added, updated, or removed, ensuring the displayed list is always current.
    * </p>
    *
    * <h3>Logic Explanation:</h3>
    * <ul>
    *     <li>Calls {@code memberRepository.findAllByOrderByNameAsc()} to fetch all `Member` entities.
    *     The `findAllByOrderByNameAsc()` method, defined in {@code MemberRepository},
    *     executes a query to retrieve all members and sorts them in ascending order based on their `name` attribute.</li>
    *     <li>The returned `List<Member>` is then assigned to the `members` field, making it accessible
    *     via {@link #getMembers()}.</li>
    * </ul>
    *
    * @see MemberListProducer#onMemberListChanged(Member)
    * @see MemberListProducer#getMembers()
    * @see org.jboss.as.quickstarts.kitchensink.data.MemberRepository#findAllByOrderByNameAsc()
    */
    @PostConstruct
    public void retrieveAllMembersOrderedByName() {
        members = memberRepository.findAllByOrderByNameAsc();
    }
}
