package org.jboss.as.quickstarts.kitchensink.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

/**
* Represents a sequence counter stored in a database, specifically designed for MongoDB.
* This class is mapped to the "database_sequences" collection as indicated by the `@Document` annotation.
* It serves as a model for managing unique, incrementing numerical sequences within the application's data layer,
* often used for generating unique identifiers for other documents.
*
* <p><b>Fields:</b></p>
* <ul>
*   <li><code>id</code>: A {@link String} that serves as the unique identifier for the sequence document within the collection.
*   This field is annotated with `@Id`, marking it as the primary key.</li>
*   <li><code>sequence</code>: A {@link java.math.BigInteger} that stores the actual numerical sequence value.
*   Using {@link java.math.BigInteger} allows for very large sequence numbers, preventing overflow issues.</li>
* </ul>
*
* <p><b>Key Methods:</b></p>
* <ul>
*   <li>{@link #getId()}: Provides read access to the unique identifier of the sequence.</li>
*   <li>{@link #setId(String)}: Allows setting the unique identifier of the sequence.</li>
*   <li>{@link #getSequence()}: Provides read access to the current numerical value of the sequence.</li>
*   <li>{@link #setSequence(java.math.BigInteger)}: Allows updating the numerical value of the sequence.</li>
* </ul>
*/
@Document(collection = "database_sequences")
public class DatabaseSequence {
    @Id
    private String id;

    private BigInteger sequence;

    /**
    * Retrieves the unique identifier of this `DatabaseSequence` instance.
    * This method provides read access to the `id` field, which serves as the primary key
    * or unique name for the sequence within the system.
    *
    * @return The unique identifier (ID) of the database sequence as a {@link String}.
    */
    public String getId() {
        return id;
    }

    /**
    * Sets the unique identifier for this `DatabaseSequence` instance.
    * <p>
    * This method is a standard setter, allowing external components to assign or update
    * the `id` field of the `DatabaseSequence` object. The `id` typically represents
    * the name or key of the sequence in the database, used for tracking and generating
    * unique numbers.
    * </p>
    *
    * @param id The string value to be set as the unique identifier for this sequence.
    */
    public void setId(String id) {
        this.id = id;
    }

    /**
    * Retrieves the current sequence value.
    * <p>
    * This method provides read-only access to the {@code sequence} field, which stores a numerical identifier
    * managed by this {@code DatabaseSequence} instance. It is a simple accessor method, allowing other parts
    * of the application to obtain the current sequence value without modifying it.
    * </p>
    *
    * @return A {@link BigInteger} representing the current sequence value.
    */
    public BigInteger getSequence() {
        return sequence;
    }

    /**
    * Sets the sequence value for this `DatabaseSequence` object.
    * This method provides a mechanism to update the internal `sequence` field,
    * which typically represents a unique or incremental identifier.
    *
    * @param sequence The `BigInteger` value to be set as the new sequence.
    *                 This value will replace the current sequence held by the object.
    */
    public void setSequence(BigInteger sequence) {
        this.sequence = sequence;
    }
}
