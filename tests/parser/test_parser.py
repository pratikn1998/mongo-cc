"""Unit tests for Java parser."""

import pytest

from src.common import types
from src.parser.code_parser import JavaCodeParser


def test_get_java_files(sample_project_dir):
    """Test parse retrieves all java files in directory."""
    parser = JavaCodeParser(root_dir=sample_project_dir)
    files = parser._get_java_files()
    assert len(files) > 0
    assert all(f.endswith(".java") for f in files)
    
    parser = JavaCodeParser("./non_existent_dir")
    with pytest.raises(FileNotFoundError):
            parser._get_java_files()


def test_parser_returns_chunks(sample_project_dir):
    """Test parser returns chunks."""
    parser = JavaCodeParser(root_dir=sample_project_dir)
    chunks = parser.parse_project()
    
    assert isinstance(chunks, list)
    assert len(chunks) > 0


def test_parsed_chunk_has_required_structure(sample_project_dir):
    """Test that chunk structure is correct."""
    parser = JavaCodeParser(root_dir=sample_project_dir)
    chunks = parser.parse_project()
    chunk = chunks[0]
    expected_fields = [
        "chunk_id", 
        "name", 
        "type", 
        "file_path", 
        "code",
        "start_line", 
        "end_line", 
        "indent", 
        "parent_class",
        "extends", 
        "implements", 
        "methods", 
        "calls", 
        "summary"
    ]
    for field in expected_fields:
        assert hasattr(chunk, field)


def test_parse_class(sample_project_dir):
    """Test that class is parsed correctly."""
    parser = JavaCodeParser(sample_project_dir)

    test_code = """
    public class TestClass extends BaseClass implements Interface1, Interface2 {
        private int field;
        
        public void method1() {
        }
    }
    """

    tree = parser.parser.parse(test_code.encode("utf-8"))
    parser._walk(tree.root_node, test_code, "TestFile.java")
    
    # Assert class was parsed correctly
    assert len(parser.chunks) > 0
    class_chunk = next(c for c in parser.chunks if c.type == types.SymbolType.CLASS)
    assert class_chunk.name == "TestClass"
    assert class_chunk.extends == ["BaseClass"]
    assert class_chunk.methods == ["method1"]
    assert class_chunk.implements == ["Interface1", "Interface2"]


def test_parse_method(sample_project_dir):
    """Test that method is parsed correctly."""
    parser = JavaCodeParser(sample_project_dir)

    test_code = """
    public class MyClass {
        public void greet() {
            System.out.println("Hello");
            helperMethod();
        }

        private void helperMethod() {
        }
    }
    """

    tree = parser.parser.parse(test_code.encode("utf-8"))
    parser._walk(tree.root_node, test_code, "TestFile.java")

    method_chunks = [c for c in parser.chunks if c.type == types.SymbolType.METHOD]
    method_names = [m.name for m in method_chunks]

    assert len(method_chunks) == 2
    assert "greet" in method_names
    assert "helperMethod" in method_names

    greet_chunk = next(m for m in method_chunks if m.name == "greet")
    assert greet_chunk.parent_class == "MyClass"
    assert "helperMethod" in greet_chunk.calls
    assert "System.out.println" not in greet_chunk.calls  # only method names, not expressions
    assert greet_chunk.file_path == "TestFile.java"
    assert greet_chunk.start_line > 0
    assert greet_chunk.end_line >= greet_chunk.start_line
