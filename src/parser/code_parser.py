"""Module to parse a code repo."""

import os 
from typing import List, Tuple

from tree_sitter import Language, Node, Parser
import tree_sitter_java as tree_sitter_java

from src.common import types


class JavaCodeParser:
    """Parses Java codebase by traversing the AST per file."""
    def __init__(self, root_dir: str):
        self.root_dir = root_dir
        
        self.parser = load_java_parser()
        
        # Stores symbols or nodes parsed from each Java
        # file with relevant information such as node type,
        # name, etc. 
        self.chunks = []
        
    def parse_project(self) -> None:
        """Main funtion to parse a Java project.
        
        This function will parse each file in a
        directory and parse all relevant classes and
        methods to construct the nodes.
        
        Returns:
            List of `JavaSymbols` as chunks. 
        """
        java_files = self._get_java_files()
        for file_path in java_files:
            self._parse_file(file_path)
        return self.chunks
        
    def _get_java_files(self) -> List[str]:
        """Get file paths of all java files in directory."""
        return [
            os.path.join(root, file)
            for root, _, files in os.walk(self.root_dir)
            for file in files if file.endswith(".java")
        ]
        
    def _parse_file(self, file_path) -> None:
        """Parses elements from a Java file.
        
        This function will read a java file and for each
        relevant portion (class or method) extract information
        into a symbol / node that contains metadata about the 
        current node and its relationship to other methods or classes.
        """
        with open(file_path, "r") as f:
            code = f.read()
            
        # Get tree of file. 
        tree = self.parser.parse(code.encode("utf-8"))
        
        # Traverse through tree to construct symbols and 
        # extract relationships. 
        self._walk(tree.root_node, code, file_path)
        
    def _walk(
        self, 
        node: Node, 
        code: str, 
        file_path: str, 
        parent_class: str = None, 
        class_method_list = None
    ) -> None:
        """Walk through tree to construct symbols."""
        name = self._get_child_text(node, "name", code)
        
        code_start_line = node.start_point[0] + 1
        code_end_line = node.end_point[0] + 1
        code_indent_level = node.start_point[1]

        node_code = code[node.start_byte:node.end_byte]

        # Check if current node is a class.
        if node.type == "class_declaration":
            # Get name of current node.
            # TODO: Clean up because used in both parts of the code. 
            
            extends, implements = self._get_inheritance(node, code)
            # List to store methods a class contains.
            method_list = []
            
            for child in node.children:
                self._walk(
                    child, 
                    code, 
                    file_path, 
                    parent_class=name, 
                    class_method_list=method_list
                )

            self.chunks.append(
                types.JavaSymbol(
                    chunk_id=f"file_path::{name}",
                    name=name,
                    type=types.SymbolType.CLASS,
                    file_path=file_path,
                    code=node_code,
                    start_line=code_start_line,
                    end_line=code_end_line,
                    indent=code_indent_level,
                    extends=extends,
                    implements=implements,
                    methods=method_list
                )
            )
        elif node.type == "method_declaration":
            # Method calls inside. 
            calls = self._extract_method_calls(node, code)
            
            # Add to the parent's method list.  
            if class_method_list is not None:
                class_method_list.append(name)

            self.chunks.append(
                types.JavaSymbol(
                    chunk_id=f"file_path::{parent_class}.{name}",
                    name=name,
                    type=types.SymbolType.METHOD,
                    file_path=file_path,
                    code=node_code,
                    start_line=code_start_line,
                    end_line=code_end_line,
                    indent=code_indent_level,
                    parent_class=parent_class,
                    calls=calls,
                )
            )
        else:
            # Recursively walk through tree. 
            for child in node.children:
                self._walk(
                    child, 
                    code, 
                    file_path, 
                    parent_class, 
                    class_method_list
                )
            
    def _get_child_text(
        self, 
        node: Node, 
        field_name: str, 
        code: str
    ) -> str:
        """Get name of current node."""
        child = node.child_by_field_name(field_name)
        if child:
            return code[child.start_byte:child.end_byte].strip()
        return ""
    
    def _get_inheritance(
        self, 
        node: Node, 
        code: str
    ) -> Tuple[List[str], List[str]]:
        """Get inheritance identifiers for each class.
        
        Returns: 
            Tuple of inheritance where the first element
            is a list of classes that a node extends and
            the second is a list of interfaces that a node 
            implements.
        """
        extends = []
        implements = []
        
        for child in node.children:
            # If a class extends another one. 
            if child.type == "superclass":
                for grandchild in child.children:
                    if grandchild.type == "type_identifier":
                        extends.append(code[grandchild.start_byte:grandchild.end_byte].strip())

            # If a class implements an interface.
            elif child.type == "super_interfaces":
                for grandchild in child.children:
                    if grandchild.type == "type_identifier":
                        implements.append(code[grandchild.start_byte:grandchild.end_byte].strip())

        return extends, implements
    
    def _extract_method_calls(self, node, code) -> List[str]:
        """Extract all methods a method invocates. 
        
        
        Returns:
            List of method names that a parent method will
            call inside. 
        """
        calls = []
        
        # Get all method invocations inside a method.
        def _collect_calls(n):
            if n.type == "method_invocation":
                identifier = n.child_by_field_name("name")
                if identifier:
                    calls.append(code[identifier.start_byte:identifier.end_byte])
            for child in n.children:
                _collect_calls(child)
                
        # Recurse through. 
        _collect_calls(node)
        return calls


def load_java_parser():
    """Loads java parser."""
    language = Language(tree_sitter_java.language())
    return Parser(language)