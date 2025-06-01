"""Architecture Diagram generator."""

import os 
from typing import Any, List

from graphviz import Source

from src.common.logger import get_logger
from src.llm import llm_client
from src.llm import prompts


logger = get_logger(__name__)


class ArchDiagramGenerator:
    """Generates architecture diagram for project."""
    def __init__(self, root_dir: str, chunks: List[Any]):
        self.root_dir = root_dir
        self.chunks = chunks
        
        self.model = self._load_model()
    
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
            system_instruction=prompts.ARCHITECTURE_DIAGRAM_GENERATOR_SYSTEM_INSTRUCTION
        )
        
    async def process(self):
        """Main function to generate diagram."""
        dot_graph = await self._generate_dot_graph()
        save_dot_graph(dot_graph, self.root_dir)
        
    async def _generate_dot_graph(self) -> str:
        """Genereate DOT graph for GraphViz."""
        readme = load_project_readme(self.root_dir)
        
        # NOTE: Just passing in class summaries to generate
        # the visualization for now for high level diagrams
        # and since class summaries include sub-contents. 
        java_class_summaries = self._load_java_class_summaries()
        
        prompt = prompts.ARCHITECTURE_DIAGRAM_GENERATOR_PROMPT.format(
            summaries=java_class_summaries)
        if readme:
            prompt += f"""
            <README>
            {readme}
            </README>
            """
        dot_graph = await self.model.generate(prompt)
        
        # Ensure string generated can be rendered by GraphViz.
        dot_graph = dot_graph.replace(
            "```", ""
        ).replace(
            "dot", ""
        ).strip()
        
        return dot_graph

    def _load_java_class_summaries(self) -> List[str]:
        """Load class summaries. 
        
        Loads for each java class, the LLM generated summary.
        
        Returns: 
            List of summary strings.
        """
        summaries =  [
            f"""
            <JAVA_CLASS>
            class_path: {chunk.file_path}
            class_summary: {chunk.summary}
            </JAVA_CLASS>
            """
            for chunk in self.chunks
            if chunk.type == "class" and chunk.summary
        ]
        return summaries
        
        
def load_project_readme(root_dir: str) -> str | None:
    """Load Project's main README if exists."""
    try:
        for filename in os.listdir(root_dir):
            if filename.upper().startswith("README"):
                readme_path = os.path.join(root_dir, filename)
                if os.path.isfile(readme_path):
                    with open(readme_path, 'r', encoding='utf-8') as f:
                        return f.read()
        return None
    except Exception as e:
        print(f"Error getting README: {e}")
        return None


def save_dot_graph(dot_graph: str, root_dir: str) -> None:
    """Render a GraphViz DOT string to a PNG file."""
    try:
        graph = Source(dot_graph)
    except Exception as e:
        logger.error(f"Error generating dot graph: {e}")
    output_path = os.path.join(root_dir, "architecture_diagram")
    graph.render(output_path, format="png", cleanup=True)
    