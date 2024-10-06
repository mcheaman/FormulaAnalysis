"""
extract.py

This module handles the extraction of data from MongoDB. The extracted data 
will be returned as raw data, which will then be transformed in the next step.
"""
import json
import os
from pymongo import MongoClient, errors
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

class Extract:
    """
    A class that handles the extraction of data from MongoDB. 
    This class initializes the MongoDB connection and provides methods to extract data.
    """

    def __init__(self):
        """
        Initializes the MongoDB client using environment variables.
        """
        self.client = None
        self.db = None
        self._connect_to_mongo()

    def _connect_to_mongo(self):
        """
        Private method to connect to the MongoDB database.
        """
        try:
            user = os.getenv('MONGO_DB_USER')
            password = os.getenv('MONGO_DB_PASSWORD')
            db_name = os.getenv('MONGO_DB_NAME')

            uri = f"mongodb+srv://{user}:{password}@cluster0.5ztoj.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
            # Create MongoClient and connect to the database
            self.client = MongoClient(uri)
            self.db = self.client[db_name]
            print("Connected to MongoDB")

        except errors.ConnectionFailure as e:
            print(f"Error connecting to MongoDB: {e}")
        except errors.ConfigurationError as e:
            print(f"Configuration error: {e}")
        except Exception as e:
            print(f"An unexpected error occurred: {e}")

    def extract(self):
        """
        Extract data from all MongoDB collections.

        Returns:
            dict: The raw data extracted from MongoDB.
        """
        try:
            drivers_collection = self.db['drivers']
            races_collection = self.db['races']
            position_collection = self.db['position']
            laps_collection = self.db['laps']
            latest_session_collection = self.db['latest_session']

            drivers = list(drivers_collection.find({}))
            races = list(races_collection.find({}))
            position = list(position_collection.find({}))
            laps = list(laps_collection.find({}))
            latest_session = list(latest_session_collection.find({}))

            extracted_data = {
                'drivers': drivers,
                'races': races,
                'position': position,
                'laps': laps,
                'latest_session': latest_session
            }
            print("Data extraction complete")
            return extracted_data
        except Exception as e:
            print(f"Error extracting data: {e}")

    def save_to_json_file(self, filename):
        """
        Save the extracted data to a JSON file for verification.

        Args:
            filename (str): The name of the file to save the data to.
        """
        data = self.extract()
        try:
            with open(filename, 'w', encoding='UTF-8') as f:
                json.dump(data, f, indent=4)
            print(f"Data saved to {filename}")
        except Exception as e:
            print(f"Error writing to file: {e}")

