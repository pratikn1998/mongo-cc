"""Pydantic models."""

from enum import Enum
from typing import List, Optional

import langchain_pinecone
from pydantic import BaseModel


class SymbolType(str, Enum):
    """Symbol / Chunk types."""
    CLASS = "class"
    METHOD = "method"
    
    
class JavaSymbol(BaseModel):
    """Pydantic model for a `Node` chunk."""
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
    
VectorStore = langchain_pinecone.PineconeVectorStore