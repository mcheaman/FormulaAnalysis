"""
load.py

This module handles the loading of transformed data into a supabase PostgreSQL database. 
It will insert the data into the appropriate tables.
"""
import os
from supabase import create_client, Client
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

class Load:
    """
    A class that handles loading of data into Supabase PostgreSQL database.
    This class initializes the Supabase client and provides methods to load data into the database.
    """
    
    def __init__(self) -> None:
        self.client: Client = self._connect_to_supabase()

    def _connect_to_supabase(self) -> Client:
        """
        Initialize a new Supabase client and return it
        The Supabase client is your entrypoint to the rest of the Supabase functionality and
        is the easiest way to interact with everything within the Supabase ecosystem.
        Returns:
            Client: Supabase client instance.
        """
        try:
            url = os.environ.get("SUPABASE_URL")
            key = os.environ.get("SUPABASE_KEY")
            return create_client(url, key)
        except Exception as e:
             print(f"Error connecting to supabase: {e}")

    def _load_into_table(self, table_name: str, table_data: list):
        """
        Load every record of table_data into the supabase table corresponding to table_name. 
        Upsert is used to handle both inserts of new records and updates to existing records. 
        Args:
            table_name (str): The name of the table to be upserted into.
            table_data (list): List of records to be uperted
        """
        try:
            response = (
                self.client.table(table_name)
                .upsert(table_data, default_to_null=True)
                .execute()
            )
            return response
        except Exception as e:
             print(f"Error bulk upserting records into {table_name}: {e}")
    
    def load(self, data: dict):
        """
        Load transformed data into PostgreSQL via Supabase.

        Args:
            data (list of dicts): The transformed data to be loaded into PostgreSQL.
            
        Returns:
            None
        """
        try:
            for table_name in data:
                self._load_into_table(table_name, data[table_name])
            print("Data load complete")

        except Exception as e:
            print(f"Error while loading data into supabase database: {e}")


