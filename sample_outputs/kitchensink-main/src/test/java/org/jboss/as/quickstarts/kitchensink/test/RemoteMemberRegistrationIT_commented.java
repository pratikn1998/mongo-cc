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

import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

/**
* `RemoteMemberRegistrationIT` is an integration test class designed to verify the remote registration and management
* of `Member` objects via an HTTP API. It simulates a client-side interaction with the RESTful service
* exposed by `MemberResourceRESTService`.
* <p>
* This class establishes the API endpoint by dynamically determining the server host, defaulting to
* `http://localhost:8080` if no environment variable or system property is set.
* </p>
*
* <p><b>Fields:</b></p>
* <ul>
*   <li><code>private static final Logger log</code>: A logger instance used for logging test execution details.</li>
*   <li><code>private BigInteger createdId</code>: Stores the ID of the `Member` created during a test,
*       used for subsequent cleanup operations.</li>
* </ul>
*
* <p><b>Key Methods:</b></p>
* <ul>
*   <li><code>getHTTPEndpoint()</code>: Constructs the base URI for the API endpoint, resolving the server host.</li>
*   <li><code>getServerHost()</code>: Retrieves the server host from environment variables or system properties.</li>
*   <li><code>testRegister()</code>: Performs a test case for member registration, sending a POST request
*       and asserting the response.</li>
*   <li><code>cleanUp()</code>: Cleans up test data by sending a DELETE request for the created member.</li>
* </ul>
*
* @see org.jboss.as.quickstarts.kitchensink.rest.MemberResourceRESTService
*/
public class RemoteMemberRegistrationIT {

    private static final Logger log = Logger.getLogger(RemoteMemberRegistrationIT.class.getName());

    private BigInteger createdId;

    /**
    * Constructs a {@link URI} for the remote HTTP endpoint used for member API interactions.
    * This method determines the base host for the API and appends the specific path for member resources.
    *
    * <p>The host is determined by first attempting to retrieve it from system environment variables
    * or system properties via {@link #getServerHost()}. If no host is explicitly configured,
    * it defaults to "http://localhost:8080".</p>
    *
    * <p>The full URI is then formed by concatenating the resolved host with the API path "/api/members".
    * This URI is subsequently used by other methods within {@link RemoteMemberRegistrationIT},
    * such as {@link RemoteMemberRegistrationIT#testRegister()} and {@link RemoteMemberRegistrationIT#cleanUp()},
    * to send HTTP requests for creating and deleting member records.</p>
    *
    * @return A {@link URI} object representing the complete HTTP endpoint for the member API.
    * @throws RuntimeException if a {@link URISyntaxException} occurs during the URI construction,
    *                          indicating an invalid URI syntax.
    * @see RemoteMemberRegistrationIT#getServerHost()
    * @see RemoteMemberRegistrationIT#testRegister()
    * @see RemoteMemberRegistrationIT#cleanUp()
    */
    protected URI getHTTPEndpoint() {
        String host = getServerHost();
        if (host == null) {
            host = "http://localhost:8080";
        }
        try {
            return new URI(host + "/api/members");
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
    * Determines the server host address for remote testing.
    * This method first attempts to retrieve the host from the `SERVER_HOST` environment variable.
    * If the environment variable is not set, it falls back to checking the `server.host` system property.
    * This provides flexibility in configuring the target server for integration tests, allowing
    * the host to be specified either via environment variables or system properties.
    *
    * @return The determined server host address as a String, or `null` if neither the environment variable
    *         nor the system property is set.
    * @see RemoteMemberRegistrationIT#getHTTPEndpoint()
    */
    private String getServerHost() {
        String host = System.getenv("SERVER_HOST");
        if (host == null) {
            host = System.getProperty("server.host");
        }
        return host;
    }

    /**
    * Tests the remote registration of a new {@link Member} by sending an HTTP POST request to the API endpoint.
    * <p>
    * This integration test constructs a {@link Member} object with predefined data and converts it into a JSON payload.
    * It then builds an {@link HttpRequest} to the endpoint obtained from {@link #getHTTPEndpoint()},
    * setting the Content-Type header to "application/json" and including the JSON payload in the request body.
    * </p>
    * <p>
    * The test sends the request using {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler)} and
    * asserts that the HTTP response status code is {@code 201 Created}, indicating successful member creation.
    * Finally, it parses the JSON response body to extract the 'id' of the newly created member and stores it
    * in the {@code createdId} field for subsequent cleanup by the {@link #cleanUp()} method.
    * </p>
    * <p>
    * This method simulates a client-side interaction with the {@code /api/members} REST endpoint,
    * which is handled by the {@code createMember} method in {@code MemberResourceRESTService}.
    * </p>
    *
    * @throws Exception if an I/O error occurs when sending or receiving the HTTP request,
    *                   or if the URI is malformed, or if JSON parsing fails.
    * @see RemoteMemberRegistrationIT#getHTTPEndpoint()
    * @see RemoteMemberRegistrationIT#cleanUp()
    * @see org.jboss.as.quickstarts.kitchensink.rest.MemberResourceRESTService#createMember(Member)
    */
    @Test
    public void testRegister() throws Exception {
        Member newMember = new Member();
        newMember.setName("Jane Doe");
        newMember.setEmail("jane@mailinator.com");
        newMember.setPhoneNumber("2125551234");
        JsonObject json = Json.createObjectBuilder()
                .add("name", "Jane Doe")
                .add("email", "jane@mailinator.com")
                .add("phoneNumber", "2125551234").build();
        HttpRequest request = HttpRequest.newBuilder(getHTTPEndpoint())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();
        HttpResponse response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(201, response.statusCode());
        JSONObject jsonObject = new JSONObject(response.body().toString());
        log.info("Member was created: " + jsonObject);
        createdId = new BigInteger(jsonObject.getString("id"));
    }

    /**
    * Cleans up test data after each test method execution.
    * <p>
    * This method is annotated with {@code @AfterEach}, ensuring it runs after every test method within the
    * {@link RemoteMemberRegistrationIT} class. Its primary responsibility is to remove the test member
    * that was created during the execution of the preceding test, identified by the {@code createdId} field.
    * </p>
    * <p>
    * The cleanup process involves constructing and sending an HTTP DELETE request to the REST API endpoint
    * responsible for deleting members. The endpoint is dynamically resolved using {@link #getHTTPEndpoint()}
    * and appended with the {@code createdId}. The request includes a "Content-Type" header set to "application/json".
    * </p>
    * <p>
    * The method logs the attempt to clean up the test member and the HTTP response received from the server.
    * This ensures that the test environment is reset, preventing data leakage and interference between tests.
    * </p>
    *
    * @throws Exception if an I/O error occurs when sending the HTTP request or if the URI is malformed.
    * @see RemoteMemberRegistrationIT#getHTTPEndpoint()
    * @see MemberResourceRESTService#deleteMemberById(long)
    */
    @AfterEach
    public void cleanUp() throws Exception {
        if (createdId != null) {
            log.info("Attempting cleanup of test member " + createdId + "...");
            HttpRequest request = HttpRequest.newBuilder(getHTTPEndpoint().resolve("/api/members/" + createdId))
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();
            HttpResponse response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Cleanup test member response: " + response);
        }
    }
}
