"""
extract.py

This module handles the extraction of data from MongoDB. The extracted data 
will be returned as raw data, which will then be transformed in the next step.
"""
import json
from pymongo import MongoClient, errors

def connect_to_mongo(uri, db_name):
    """
    Connect to the MongoDB database.

    Args:
        uri (str): The MongoDB connection URI.
        db_name (str): The name of the database to connect to.

    Returns:
        db (Database): A reference to the MongoDB database.
    """
    try:
        # Create a MongoClient object
        client = MongoClient(uri)
        
        # Access the database
        db = client[db_name]
        
        print("Connected to MongoDB")
        return db

    except errors.ConnectionFailure as e:
        print(f"Error connecting to MongoDB: {e}")
    except errors.ConfigurationError as e:
        print(f"Configuration error: {e}")
    except Exception as e: 
        print(f"An unexpected error occurred: {e}")

def extract_data(db): 
    """
    Extract data from MongoDB.

    Args:
        db (Database): A reference to the MongoDB database.

    Returns:
        extracted_data (list or dict): The raw data extracted from MongoDB.
    """
    try:
        drivers_collection = db['drivers']
        races_collection = db['races']
        position_collection = db['position']
        laps_collection = db['laps']
        latest_session_collection = db['latest_session']
        
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

def extract():
    """
    Extract data from MongoDB.

    Connects to MongoDB, queries the necessary collections, and retrieves raw data.

    Returns:
        extracted_data (dict or list): The raw data extracted from MongoDB.
    """
    # Connect to MongoDB
    uri = "mongodb+srv://mcheaman:QKY4twt6ghg_yzr4vhe@cluster0.5ztoj.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
    db_name = "race_telemetry_db"
    db = connect_to_mongo(uri, db_name)

    # If connection is successful, extract data
    if db is not None:
        extracted_data = extract_data(db)
        return extracted_data


def import_to_json_file(db, filename):
    """
    Extract data and save to json file for verification

    Args:
        db (dict): The extracted data to save.
        filename (str): The name of the file to save the data to.

    Returns:
        None
    """
    data = extract_data(db)
    try:
        with open(filename, 'w', encoding='UTF-8') as f:
            # Write data to file in JSON format
            json.dump(data, f, indent=4)
        print(f"Data saved to {filename}")
    except Exception as e:
        print(f"Error writing to file: {e}")
