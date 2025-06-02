"""
This file deinfes a class for interacting with a LLM (specificall Gemini).
"""

from typing import Any, Dict

from google import genai
from google.genai import types

from src.common.retry import async_retry


class LLMModel:
    """Abstraction from LLM client libraries.
    
    Args: 
        model_name (str): Model name.
        project_id (str): GCP Project ID.
        location (str): Location to init Gemini client.
        system_instruction (str): System instruction for the model.

    Keyword Args:
        - response_mime_type (str): Expected response format.
        - temperature (float): Sampling temperature for generation.
        - top_p (float): Top-p.
        - max_output_tokens (int): Maximum number of tokens to generate.
        - thinking_budget (int): Optional quota or token budget to control reasoning steps.
    """
    def __init__(
        self, 
        location: str, 
        project_id: str, 
        model_name: str = "gemini-2.5-flash-preview-05-20", 
        system_instruction: str = None,
        **kwargs
    ):
        self.model_name = model_name
        self.location = location 
        self.project_id = project_id
        self.system_instruction = system_instruction
        
        self.response_mime_type = kwargs.get("response_mime_type", "text/plain")
        self.temperature = kwargs.get("temperature", 0.0)
        self.top_p = kwargs.get("top_p", 0.95)
        self.max_output_tokens = kwargs.get("max_output_tokens", 8192)
        self.thinking_budget = kwargs.get("thinking_budget", 0)
        
        self.client = self._init_client()
        self.generation_config = self._load_generation_config()
        
    def _init_client(self):
        """Init Gemini client."""
        client = genai.Client(
            project=self.project_id,
            location=self.location,
            vertexai=True,
        )
        return client
    
    def _load_generation_config(self) -> types.GenerateContentConfig:
        """Load generation config for each Gemini Call."""
        return types.GenerateContentConfig(
            system_instruction=self.system_instruction,
            temperature=self.temperature,
            top_p=self.top_p,
            response_mime_type=self.response_mime_type,
            thinking_config=types.ThinkingConfig(
                thinking_budget=self.thinking_budget
            )
        )

    @async_retry(max_retries=3, base_delay=1.0, exceptions=(Exception,))
    async def generate(self, prompt: str) -> str:
        """Send request to Gemini.

        Args:
            prompt: The prompt to send to Gemini.
            
        Returns: 
            Gemini response as text.
        """
        response = await self.client.aio.models.generate_content(
            model=self.model_name,
            contents=prompt,
            config=self.generation_config,
        )
        return self._parse_response(response)
    
    @staticmethod
    def _parse_response(response: types.GenerateContentResponse):
        """Parse response from Gemini.

        Args:
            response: The response from Gemini.

        Returns:
            Union[str, Any]: The parsed response.
        """
        return response.text
    
    def get_response_metadata(self, response: types.GenerateContentResponse) -> Dict[str, Any]:
        """Get metadata from Gemini response."""
        usage_metadata = response.usage_metadata
        
        input_tokens = usage_metadata.prompt_token_count
        output_tokens = usage_metadata.candidates_token_count
        total_tokens = usage_metadata.total_token_count
        
        return {
            "num_input_tokens": input_tokens,
            "num_output_tokens": output_tokens,
            "total_tokens": total_tokens
        }
        