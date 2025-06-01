"""Comment Generator."""

import os
import re
from typing import Any, Dict, List

from src.common import types
from src.llm import llm_client
from src.llm import prompts 


class CommentGenerator:
    """Generators comments per chunk.
    
    This will generate comments for each chunk and
    use the current context as well as any related code context
    based on the parsed relationship and similar chunks 
    retrieved from the vector store. 
    """
    def __init__(
        self, 
        vector_store: types.VectorStore, 
        chunks: List[types.JavaSymbol], 
        namespace: str
    ):
        self.vector_store = vector_store
        self.namespace = namespace
        self.chunks = chunks 
        self.model = self._load_model()
        
        # Store comments generated with respect to file + line number & indent
        # of code comment was generated for, and indent
        self.generated_comments = {}
        
    def _load_model(self):
        """Init Gemini instance with system instruction.
        
        Initalize a Gemini instance with a system instruction 
        to generate comments for a code snippet. 
        """
        project_id  = os.getenv("PROJECT_ID")
        location = os.getenv("LOCATION")
        model_name = os.getenv("MODEL_NAME")
        return llm_client.LLMModel(
            project_id=project_id, 
            location=location, 
            model_name=model_name,
            system_instruction=prompts.COMMENT_GENERATOR_SYSTEM_INSTRUCTION
        )
    
    def process(self) -> None:
        """Main function to generate comments for project.        
        
        Returns:
            None. For each java file, this function writes a new 
            `*_commented.java` file without modifying the original
            code. 
        """
        self._generate_comments()
        self._write_comments_to_new_file()

    def _generate_comments(self) -> None:
        """Generate comments for each class or method in a code base.
        
        Returns:
            None. For each java file, this function writes a new 
            `*_commented.java` file without modifying the original
            code. 
        """
        for chunk in self.chunks:    
            chunk_file_path = chunk.file_path
            comment = self._generate_comment(chunk)
            # Add comment generated with respect to what file
            # and where it should be inserted. 
            self.generated_comments.setdefault(chunk_file_path, [])
            
            self.generated_comments[chunk_file_path].append({
                "comment": comment,
                "line_number": chunk.start_line,
                "indent_level": chunk.indent
            })
        
    def _generate_comment(self, chunk) -> str:
        """Generate a code comment. 
        
        Inserts the relevant code snippets into prompt
        to geenerate a comprehensive comment using Gemini.
        """
        query = chunk.code
        # TODO: Add metadata filters to improve search. 
        filter = None
    
        relevant_docs = self.vector_store.similarity_search(
            query=query,
            filter=filter,
            namespace=self.namespace
        )
        prompt = prompts.COMMENT_GENERATOR_PROMPT_TEMPLATE.format(
            similar_context=relevant_docs,
            type=chunk.type,
            name=chunk.name,
            code=chunk.code
        )
        comment_generated = self.model.generate(prompt)
        
        # Post-process model generated comment to be a valid
        # Javadoc. 
        # TODO: Clean up. 
        comment_generated = comment_generated.replace(
            "```", ""
        ).replace(
            "```java", ""
        )
        comment_generated = re.sub(
            r'^\s*java\s*\n?', 
            '', 
            comment_generated, 
            flags=re.MULTILINE
        )
        return comment_generated

    def _write_comments_to_new_file(self):
        """Write generated comments with original code to new file."""
        for file_path, file_comments in self.generated_comments.items():
            formatted_lines = format_file_comments(file_path, file_comments)
            write_lines_to_file(file_path, formatted_lines)

 
def format_file_comments(file_path: str, commments: List[Dict[str, Any]]):
    """Format new java file with generated comments."""
    with open(file_path, "r") as f:
        lines = f.readlines()   
    
    # Sort generated comments with respect to line number of code
    # block they were generated for. 
    comments_sorted = sorted(
        commments, key=lambda x: x["line_number"], reverse=True)
    
    # Insert each comment block at the specified line number.
    # Comments are inserted in reverse line order to prevent line number shifts 
    # caused by earlier insertions affecting the position of later ones.
    for comment in comments_sorted:
        comment_line_num = comment["line_number"]
        comment_block = format_comment_block(
            comment["comment"], comment["indent_level"])
        lines = lines[:comment_line_num - 1] + comment_block + lines[comment_line_num - 1:]
    return lines
    
    
def format_comment_block(comment: str, indent_num: int) -> List[str]:
    """Adjust indentation level for a preformatted JavaDoc comment block.
    
    Returns:
        List of indented lines for each comment line. 
    """
    indent = " " * indent_num
    comment_lines = comment.strip().split('\n')
    block = [f"{indent}{line.strip()}\n" for line in comment_lines]
    return block


def write_lines_to_file(file_path: str, lines: List[str]):
    """Write lines to new file."""
    rel_path = os.path.relpath(file_path)
    base, ext = os.path.splitext(rel_path)
    output_file_path = os.path.normpath(
        os.path.join(base + "_commented" + ext))
    
    # Ensure the output drectories exist.
    os.makedirs(os.path.dirname(output_file_path), exist_ok=True)
    with open(output_file_path, "w") as f:
        f.writelines(lines)
