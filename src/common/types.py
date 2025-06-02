"""Pydantic models."""

from enum import Enum
from typing import List, Optional, TypeAlias

import langchain_pinecone
from pydantic import BaseModel



VectorStore: TypeAlias = langchain_pinecone.PineconeVectorStore


class SymbolType(str, Enum):
    """Symbol / Chunk types.
    
    Attributes:
        CLASS: Class type.
        METHOD: Method type.
    """
    CLASS = "class"
    METHOD = "method"
    
    
class JavaSymbol(BaseModel):
    """Pydantic model for a `Node` chunk.
    
    Attributes:
        chunk_id: Unique identifier for the chunk.
        name: Name of the chunk.
        type: Type of the chunk.
        file_path: File path of the chunk.
        code: Code of the chunk.
        start_line: Starting line number of the chunk.
        end_line: Ending line number of the chunk.
        indent: Indent level of the chunk.
        parent_class: Parent class of the chunk.
        extends: List of classes extended by the chunk.
        implements: List of interfaces implemented by the chunk.
        methods: List of methods in the chunk.
        calls: List of calls made by the chunk.
        summary: LLM generated summary of the chunk.
    """
    chunk_id: str
    name: str
    type: SymbolType           
    file_path: str
    code: str
    start_line: int
    end_line: int
    indent: int
    parent_class: Optional[str] = None
    extends: List[str] = []
    implements: List[str] = []
    methods: List[str] = []
    calls: List[str] = []
    summary: Optional[str] = None
