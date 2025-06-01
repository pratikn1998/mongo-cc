"""Main file for Code Comprehender.

This will take in a project directory containing Java files and
generate enhanced comments for each Java file & generate an architecture
diagram for the codebase. 
"""
import argparse
import os

from dotenv import load_dotenv

from src.code_intelligence import arch_diagram_generator
from src.code_intelligence import comment_generator
from src.code_intelligence import embedder
from src.parser.code_parser import JavaCodeParser
from src.parser.generate_chunk_summaries import generate_all_chunk_summaries
from src.common import utils

# Load enviroment variables. 
if os.getenv("ENV", "DEV") == "DEV":
    load_dotenv()


def parse_args():
    """Parse command line arguments."""
    parser = argparse.ArgumentParser(description="Code Comprehender")
    parser.add_argument(
        "--input_dir",
        type=str,
        required=True,
        help="""Input directory of the codebase to enrich and analyze.""",
    )
    parser.add_argument(
        "--namespace",
        type=str,
        required=False,
        default=None,
        help="""
        Optional namespace to use for the vector store. 
        If not provided, it will be derived from input_dir.
        One namespace per project repository. 
        """,
    )
    return parser.parse_args()


def main():
    # Read
    args = parse_args()
    
    # For dev testing.
    if os.getenv("ENV", "DEV"):
        utils.remove_code_comprehender_outputs(args.input_dir)
    
    # Generate namespace from project root directory
    # if not passed as a CLI arg.
    if not args.namespace:
        args.namespace = utils.generate_namespace(
            args.input_dir)
        
    # Parse code files in project into chunks. 
    parser = JavaCodeParser(root_dir=args.input_dir)
    chunks = parser.parse_project()
    
    # Generate summaries with an LLM for each chunk. 
    generate_all_chunk_summaries(chunks=chunks)
    
    arch_diagram_generator.ArchDiagramGenerator(
        root_dir=args.input_dir,
        chunks=chunks
    ).process()
    
    # Create vector store. 
    vector_store = embedder.load_or_create_vector_store(
        chunks=chunks, 
        namespace=args.namespace
    )
    comment_generator.CommentGenerator(
        vector_store=vector_store,
        namespace=args.namespace,
        chunks=chunks
    ).process()
    
     
if __name__ == "__main__":
    main()
