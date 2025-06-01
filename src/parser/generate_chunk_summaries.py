"""Chunk summary generation.

These summaries are later used for architecture diagram, 
and could also be used to enrich the index later 
in the future for better search & synthesization.

By adding both summary and code to the index, this can
help the LLM answer user queries for their codebase. 
"""

from concurrent.futures import as_completed, ThreadPoolExecutor
import os 
from typing import List 

from src.common import types
from src.common.logger import get_logger
from src.llm import llm_client, prompts

logger = get_logger(__name__)


def generate_chunk_summaries(
    model: llm_client.LLMModel, 
    chunk: types.JavaSymbol
):
    """Generate a summary for a chunk."""
    try:
        prompt = prompts.CHUNK_SUMMARY_PROMPT.format(
            name=chunk.name,
            file_path=chunk.file_path,
            code=chunk.code
        )
        summary = model.generate(prompt)
        chunk.summary = summary
    except Exception:
        logger.error("Error generating summary for chunk.")
        
   
    
def generate_all_chunk_summaries(chunks: List[types.JavaSymbol]) -> None:
    """Generate summaries for all chunks.
    
    Args:
        chunks: List of JavaSymbols, where each element is a code
            chunk that was pared. 
            
    Returns: 
        None - udpates each symbol in place. 
    """
    project_id  = os.getenv("PROJECT_ID")
    location = os.getenv("LOCATION")
    model_name = os.getenv("MODEL_NAME")
    
    llm_model = llm_client.LLMModel(
        project_id=project_id, 
        location=location, 
        model_name=model_name,
        system_instruction=prompts.CHUNK_SUMMARY_SYSTEM_INSTRUCTION
    )
    
    # Generate summaries for each chunk in paralle.
    with ThreadPoolExecutor(max_workers=8) as executor:
        futures = [
            executor.submit(
                generate_chunk_summaries, 
                llm_model, 
                chunk
            ) 
            for chunk in chunks
        ]

        for future in as_completed(futures):
            _ = future.result()
    