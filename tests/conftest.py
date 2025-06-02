"""Fixtures."""

import os
import pytest

from src.common.types import JavaSymbol, SymbolType


@pytest.fixture
def sample_project_dir():
    """Returns path of sample project."""
    return os.path.join(
        os.path.dirname(__file__), "data", "vehicle-management")


@pytest.fixture
def sample_chunk():
    """Returns sample JavaSymbol / chunk."""
    return JavaSymbol(
        chunk_id="1",
        name="TestClass",
        type=SymbolType.CLASS,
        file_path="TestClass.java",
        code="public class TestClass {}",
        start_line=1,
        end_line=3,
        indent=0
    )
    