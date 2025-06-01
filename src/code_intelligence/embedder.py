"""Create embeddings."""

import os 

from langchain_core.documents import Document
from langchain_google_genai import GoogleGenerativeAIEmbeddings
from langchain_pinecone import PineconeVectorStore
import pinecone

from src.common import types


def create_symbol_document(chunk: types.JavaSymbol):
    """Create a document to embed and store in a Vector store. """
    page_content = chunk.code + "\n\n" + (chunk.summary or "")
    
    # NOTE: I added this metadata if we wanted to turn this workflow
    # into an agent to let users also ask questions about their 
    # existing or updated codebase. 
    metadata = {
        "chunk_id": chunk.chunk_id,
        "name": chunk.name,
        "type": chunk.type.value,
        "file_path": chunk.file_path,
        "start_line": chunk.start_line,
        "end_line": chunk.end_line,
        "parent_class": chunk.parent_class or "",
        "has_extends": len(chunk.extends) > 0,
        "has_implements": len(chunk.implements) > 0,
        "num_methods": len(chunk.methods),
        "num_calls": len(chunk.calls)
    }
    return Document(page_content=page_content, metadata=metadata)


def load_or_create_vector_store(
    chunks: types.JavaSymbol, 
    namespace: str
) -> PineconeVectorStore:
    """Create a vector store from chunks.
    
    Returns:
        Pinecone Vector Store. 
    """
    index_name = os.getenv("INDEX_NAME", "code-comprehender")
    
    pc = pinecone.Pinecone(
        api_key=os.getenv("PINECONE_API_KEY"),
    )
    
    # Create index if doesn't exist. 
    # This will create 1 index for the code-comprehender
    # tool, and for each Java project associate a namespace
    # to be able to retrieve vectors associated with each Java
    # project. 
    if not pc.has_index(index_name):
        dim = 3072
        pc.create_index(
            name=index_name, 
            spec=pinecone.ServerlessSpec(cloud="aws", region="us-east-1"), 
            dimension=dim
        )    
        
    # TODO: Temp for 1 time project insertions. Doesn't handle upserts
    # yet / changes to a code base yet. 
    index = pc.Index(index_name)
    namespace_description = index.describe_index_stats()
    existing_namespaces = namespace_description.get(
        "namespaces", {})
    
    # Load embeddings to use. 
    embeddings = GoogleGenerativeAIEmbeddings(
        model="models/gemini-embedding-exp-03-07",
        google_api_key=os.getenv("GEMINI_API_KEY")
    )

    # Create a namespace for project and add in documents
    # for java chunks. 
    if namespace not in existing_namespaces:
        # Create a langchain document for each chunk. 
        documents = [create_symbol_document(chunk) for chunk in chunks]
        vector_store = PineconeVectorStore.from_documents(
            documents=documents,
            index_name=index_name,
            embedding=embeddings,
            namespace=namespace
        )
    else:
        vector_store = PineconeVectorStore(
            index_name="code-comprehender",
            namespace="vehicle-management",
            embedding=embeddings
        )
    return vector_store
