"""Chunk summary generation.

These summaries are later used for architecture diagram, 
and could also be used to enrich the index later 
in the future for better search & synthesization.

By adding both summary and code to the index, this can
help the LLM answer user queries for their codebase. 
"""

import os 
from typing import List 

import asyncio

from src.common import types
from src.common.logger import get_logger
from src.llm import llm_client, prompts

logger = get_logger(__name__)


MAX_CONCURRENT_REQUESTS = 50
semaphore = asyncio.Semaphore(MAX_CONCURRENT_REQUESTS)

 
async def generate_chunk_summaries(
    model: llm_client.LLMModel, 
    chunk: types.JavaSymbol
):
    """Generate a summary for a chunk."""
    prompt = prompts.CHUNK_SUMMARY_PROMPT.format(
        name=chunk.name,
        file_path=chunk.file_path,
        code=chunk.code
    )
    try:
        async with semaphore:
            summary = await model.generate(prompt)
            chunk.summary = summary
    except Exception as e:
        # TODO: clean up. 
        if "429" in str(e):
            logger.debug(
                "Gemini Quota Error after multiple attempts")
        else:
            logger.error(
                f"Error calling gemini to generate summary for chunk: {str(e)}")
        
   
async def generate_all_chunk_summaries(chunks: List[types.JavaSymbol]) -> None:
    """Generate summaries for all chunks.
    
    Args:
        chunks: List of JavaSymbols, where each element is a code
            chunk that was pared. 
            
    Returns: 
        None - updates each symbol in place. 
    """
    try:
        project_id = os.getenv("PROJECT_ID")
        location = os.getenv("LOCATION")
        model_name = os.getenv("MODEL_NAME")
        
        if not all([project_id, location, model_name]):
            logger.error("Missing required environment variables: PROJECT_ID, LOCATION, or MODEL_NAME")
            return
        
        llm_model = llm_client.LLMModel(
            project_id=project_id, 
            location=location, 
            model_name=model_name,
            system_instruction=prompts.CHUNK_SUMMARY_SYSTEM_INSTRUCTION
        )
        
        # Create tasks for each chunk.
        tasks = [
            generate_chunk_summaries(llm_model, chunk)
            for chunk in chunks
        ]
        
        try:
            await asyncio.gather(*tasks)
        except asyncio.TimeoutError:
            logger.error("Timeout while generating summaries")
        except Exception as e:
            logger.error(f"Error during summary generation: {str(e)}")
    except Exception as e:
        logger.error(f"Failed to generate summaries for chunks: {str(e)}")
        raise
    