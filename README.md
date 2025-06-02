# Code Comprehender

A powerful tool for generating Java code documentation and architecture diagrams.

## Overview

Code Comprehender is a Python-based tool that helps developers understand and document Java codebases by:

- Automatically generating comprehensive code comments
- Creating architecture diagrams
- Analyzing code structure and relationships

## Features

- **AI-Powered Documentation**: Uses Gemini to create meaningful code comments
- **Architecture Visualization**: Generates architecture diagrams using Graphviz
- **Java Code Analysis**: Built-in Java parser using tree-sitter for accurate code analysis

## Project Structure

```
├── src/
│   ├── code_intelligence/  # Code analysis and intelligence features
│   ├── common/             # Shared utilities and helpers
│   ├── llm/                # LLM integration components
│   ├── parser/             # Java code parsing functionality
│   └── main.py             # Main application entry point
├── .env.example            # Example environment variables file
├── pyproject.toml          # Project configuration and dependencies
├── Makefile                # Development automation commands
└── uv.lock                 # Locked dependencies
└── sample_outputs/         # Example outputs from Java project analysis
```

## Workflow Steps

1. **Code Parsing**
   - Load all Java related files.
   - Each code symbol is converted into a `JavaSymbol` object containing metadata
2. **Code Relationship Mapping**
   - For each class and method, the parser also extracts references and relationships across files:
     - Which methods are invoked
     - What classes are extended or implemented
     - How files and packages interconnect
   - This relationship map forms the basis for both enriched comment generation and architectural visualization.
3. **Chunk-Based Summarization**
   - The codebase is split into "chunks" (each corresponding to a class or method).
   - A dedicated LLM prompt is sent to Gemini for each chunk to generate a natural language summary that captures:
     - Local logic and functionality
     - Cross-file dependencies (e.g., calls to other methods)
4. **Vector Store Embedding**
   - Each chunk and its associated metadata (summary, raw code, relationships) are embedded using Gemini’s text embedding model.
   - These are stored in a Pinecone vector store, where each Java project corresponds to a unique namespace.
   - This enables efficient semantic search, retrieval-augmented generation (RAG), and downstream code understanding tasks.
5. **Comment Generation**
   - For each chunk, we retrieve relevant context from the vector store to ground comment generation in both the local code and the broader context collected from the relationship mapping.
6. **Architecture Diagram Generation**
   - Using the LLM generated summaries for class chunks, we generate a a Graphviz-based dot graph.

## Prerequisites

- Python 3.8 or higher
- Make (for using Makefile commands)
- UV package manager
- A valid **Gemini API key**
- A valid **Pinecone API key**

## Installation

1. Clone the repository:

```bash
git clone [repository-url]
cd code-comprehender
```

2. Set up the development environment:

```bash
make install
```

This will:

- Create a virtual environment in `.venv`
- Install all required dependencies
- Set up the project for development

### Available Make Commands

- `make venv`: Creates a virtual environment
- `make install`: Sets up the project and installs dependencies
- `make lint`: Runs the Ruff linter
- `make clean`: Cleans up generated files and caches

## Setup

1. Activate the virtual environment:

```bash
source .venv/bin/activate
```

2. Copy the example enviroment file and respective variables:

```bash
cp .env.example .env
```

## Usage

To run the workflow, execute the following command:

```bash
python -m src.main --input_dir=./test-project
```

With explicit namespace for Vector Store reference:

```bash
python -m src.main --input_dir=./test-project --namespace=test-namespace
```

## Testing

As of now, unit tests are written only for the Java parser module, which is responsible for parsing code into structured chunks ([`JavaSymbol`](src/common/types.py) objects).

```bash
pytest -v
```

## Sample Outputs

Example outputs for sample Java projects are available in ["sample_outputs"](/sample_outputs)
