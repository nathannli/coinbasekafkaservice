"""Utility script for product searching via the Coinbase REST API."""

from coinbase.rest import RESTClient
from dotenv import load_dotenv
import os

def get_required_env(name: str) -> str:
    value = os.getenv(name)
    if not value:
        raise ValueError(f"Missing required environment variable: {name}")
    return value


def get_client():
    api_key = get_required_env("COINBASE_API_KEY")
    api_secret = get_required_env("COINBASE_API_SECRET").replace("\\n", "\n")
    return RESTClient(api_key=api_key, api_secret=api_secret)


def main():
    load_dotenv()

    client = get_client()
    products = client.get_products()
    display_names = sorted(
        product.display_name
        for product in products.products or []
        if product.display_name
    )
    print([display_name for display_name in display_names if 'BTC' in display_name])

if __name__ == "__main__":
    main()
