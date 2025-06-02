"""Test chunk summary generation."""

import os
import pytest
from unittest.mock import AsyncMock, patch

from src.parser.generate_chunk_summaries import generate_all_chunk_summaries


@pytest.mark.asyncio
@patch("src.parser.generate_chunk_summaries.llm_client.LLMModel.generate", new_callable=AsyncMock)
@patch.dict(os.environ, {
    "PROJECT_ID": "dummy_project",
    "LOCATION": "us-central1",
    "MODEL_NAME": "gemini-2.5-flash"
})
async def test_generate_all_chunk_summaries_success(mock_generate, sample_chunk):
    mock_generate.return_value = "This is a test summary."
    chunks = [sample_chunk]

    await generate_all_chunk_summaries(chunks)

    assert chunks[0].summary == "This is a test summary."
    mock_generate.assert_awaited_once()
