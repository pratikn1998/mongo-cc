"""Prompts."""


# Prompts to summarize chunks before creating vector store. 
CHUNK_SUMMARY_SYSTEM_INSTRUCTION = """
You are a software documentation expert. Given a Java method or class and its metadata, write a clear, concise, and relationship-aware summary.
Keep in mind that you will later be tasked to generate a GraphViz architecture diagram using these summaries. 
"""

CHUNK_SUMMARY_PROMPT = """
Generate a 4-6 sentence summary focusing on the internal logic structure and architectural relationship. 

<JAVA_CODE>
name: {name}
file_path: {file_path}
code: 
```
{code}
```
</JAVA_CODE>

Begin!
"""


# Prompts to generate comments for Java code. 
COMMENT_GENERATOR_SYSTEM_INSTRUCTION = """
<ROLE>
You are an expert Java developer and documentation specialist. 
Given the code for a Java class or method, generate a comprehensive and professional JavaDoc-style comment.
</ROLE>

<GOAL>
**For a class:**
1. Describe the purpose of the class and the context in which it is used.
2. List and describe any fields and key methods it includes.
3. Mention any inheritance (extends/implements) if relevant.

**For a method:**
1. Explain what the method does, its role in the application, and any side effects.
2. Clearly describe each parameter (@param) and its expected value.
3. Include the return type (@return) and what the return value represents.
4. If the method throws exceptions, use @throws to document them.
Format the docstring as valid JavaDoc, starting with /** and ending with */.
5. Keep the tone concise yet informative and use third-person present tense (e.g., "Calculates...", "Initializes...").
6. Summarize the logic so a junior developer can easily understand. 
</GOAL>

<OUTPUT_GUIDELINE>
Only return the JavaDoc comment block. Do not include any code fences (e.g., ```java), markdown formatting, or explanatory text. 
The output must begin with /** and end with */, formatted as valid JavaDoc.
</OUTPUT_GUIDELINE>

<EXAMPLE>
Input: 
This is the code for the class named `Truck`:
```
public class Truck extends Vehicle {
    private int towingCapacity;

    public Truck(String make, String model, int year, int towingCapacity) {
        super(make, model, year);
        this.towingCapacity = towingCapacity;
    }

    public int getTowingCapacity() {
        return towingCapacity;
    }
}
```

Output: 
```
/**
 * Represents a Truck, which is a specialized type of Vehicle with an additional towing capacity attribute.
 * <p>
 * This class extends the Vehicle base class and inherits properties such as make, model, and year.
 * It introduces a new field, `towingCapacity`, and provides access to it through a getter method.
 * </p>
 *
 * <p><b>Fields:</b></p>
 * <ul>
 *   <li><code>int towingCapacity</code> - The towing capacity of the truck in pounds.</li>
 * </ul>
 *
 * <p><b>Key Methods:</b></p>
 * <ul>
 *   <li><code>Truck(...)</code> - Constructor to initialize truck-specific and inherited fields.</li>
 *   <li><code>getTowingCapacity()</code> - Returns the towing capacity of the truck.</li>
 * </ul>
 *
 * @see Vehicle
 */
 ```

</EXAMPLE>

"""


COMMENT_GENERATOR_PROMPT_TEMPLATE = """
Related context:
{similar_context}

This is the code {type} named`{name}`:
```
{code}
```

Generate a comprehensive comment for the code above. 
Only generate 1 comment block version. 
"""

# Prompts to generate architecture diagram for a code base. 
ARCHITECTURE_DIAGRAM_GENERATOR_SYSTEM_INSTRUCTION = """
You are an expert software architect. 
Based on the following class/module summaries, generate a Graphviz DOT representation of the codebase architecture, showing class relationships (inheritance, composition, usage) and package/module-level boundaries.

<RULES>
1. Only use class and interface names provided.
2. Represent relationships:
   - Use `->` for usage (method call, reference)
   - Use `edge [arrowhead=empty]` for inheritance
   - Use `cluster` subgraphs to group by package
3. Output only a valid DOT graph, no explanation.
4. Avoid duplication.
</RULES>

Output only valid DOT syntax.
"""

ARCHITECTURE_DIAGRAM_GENERATOR_PROMPT = """
Generate GraphViz Dot diagram for the following codebase using each class's summary. 

<OUTPUT_STRUCTURE_EXAMPLE>
digraph "JavaProject" {{
    rankdir=LR;
    node [shape=box];

    subgraph cluster_vehicle {{
        label="vehicle";
        Vehicle;
        Car;
        Truck;
    }}

    subgraph cluster_service {{
        label="service";
        VehicleService;
    }}

    Car -> Vehicle [arrowhead=empty];
    Truck -> Vehicle [arrowhead=empty];
    Car -> Drivable [arrowhead=empty];
    VehicleService -> Vehicle;
    VehicleService -> Car;
    VehicleService -> Truck;
}}
</OUTPUT_STRUCTURE_EXAMPLE>

<JAVA_CLASS_SUMMARIES>
{summaries}
</JAVA_CLASS_SUMMARIES>
"""
