"""Retry decorator."""

import asyncio
import functools

from src.common.logger import get_logger

logger = get_logger(__name__)


def async_retry(max_retries=3, base_delay=1.0, exceptions=(Exception,)):
    """Retry decorator.
    
    Args:
        max_retries: Maximum number of retries.
        base_delay: Base delay in seconds.
        exceptions: Exceptions to retry on.
    """
    def decorator(func):
        @functools.wraps(func)
        async def wrapper(*args, **kwargs):
            delay = base_delay
            for attempt in range(max_retries):
                try:
                    return await func(*args, **kwargs)
                except exceptions as e:
                    if attempt < max_retries - 1:
                        await asyncio.sleep(delay)
                        delay *= 2
                    else:
                        logger.error(f"Failed after {max_retries} attempts: {str(e)}")
                        raise e
        return wrapper
    return decorator
