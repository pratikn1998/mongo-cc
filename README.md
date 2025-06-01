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

## Workflow Architecture

1.

## Prerequisites

- Python 3.8 or higher
- Make (for using Makefile commands)
- UV package manager

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

## Project Structure

```
.
├── src/
│   ├── code_intelligence/  # Code analysis and intelligence features
│   ├── common/            # Shared utilities and helpers
│   ├── llm/              # LLM integration components
│   ├── parser/           # Java code parsing functionality
│   └── main.py           # Main application entry point
├── sample_repos/         # Sample repositories for testing
├── pyproject.toml        # Project configuration and dependencies
├── Makefile             # Development automation commands
└── uv.lock              # Locked dependencies
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

### Available Make Commands

- `make venv`: Creates a virtual environment
- `make install`: Sets up the project and installs dependencies
- `make lint`: Runs the Ruff linter
- `make clean`: Cleans up generated files and caches
