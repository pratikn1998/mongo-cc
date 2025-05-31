"""Utility Functions."""

from google.cloud import secretmanager


def get_secret_value(secret_id: str, project_id: str) -> str:
    """Get secret value from GCP Secret Manager.
    
    Args:
        secret_id: Secret key. 
        project_id: GCP project id. 
        
    Returns: 
        String of secret value.
    """
    client = secretmanager.SecretManagerServiceClient()
    secret_name = f"projects/{project_id}/secrets/{secret_id}/versions/latest"
    response = client.access_secret_version(name=secret_name)
    secret_value = response.payload.data.decode("UTF-8")
    return secret_value
