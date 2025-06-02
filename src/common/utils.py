"""Utility Functions."""

import os 
from pathlib import Path


def generate_namespace(input_dir: str) -> str:
    """Generate namespace for index."""
    resolved_path = Path(input_dir).resolve()
    parts = resolved_path.parts
    for part in parts[::-1]:
        if part not in (resolved_path.root, resolved_path.drive):
            namespace = part
            return namespace    


def remove_code_comprehender_outputs(root_dir: str) -> None:
    """Removes *_commented.java files from a project. 
    
    Used for testing workflow. 
    """
    for dirpath, _, filenames in os.walk(root_dir):
        for filename in filenames:
            if filename.endswith("_commented.java") or filename.endswith("architecture_diagram.png"):
                file_path = os.path.join(dirpath, filename)
                os.remove(file_path)

                
                    