import sys
import os
import pytest

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))
from transform import transform

def test_transform():
    """
    Test the transformation of MongoDB data to relational format.
    """
    # Sample raw data from MongoDB
    raw_data = {
        'drivers': [
            {'driverNumber': 1, '_id': 'Driver 1', 'team': 'Team A', 'countryCode': 'USA', 'headshotUrl': 'http://example.com'},
            {'driverNumber': 2, '_id': 'Driver 2', 'team': 'Team B', 'countryCode': 'UK'}  # missing headshotUrl
        ],
        'laps': [
            {'sessionKey': 9606, 'driverNumber': 1, 'lapNumber': 1, 'lapDuration': 85.6, 'sector1': 30.5, 'sector2': 30.1, 'sector3': 25.0, 'speedTrapSpeed': 180, 'isPitOutLap': False}
        ],
        'latest_session': [
            {'sessionKey': 9606, 'sessionEndDate': '2024-01-01', 'sessionName': 'Race 1'}
        ],
        'races': [
            {'_id': 9606, 'year': 2024, 'sessionName': 'Race 1', 'countryName': 'Country A', 'circuitName': 'Circuit 1'}
        ],
        'position': [
            {'sessionKey': 9606, 'driverNumber': 1, 'position': 1}
        ]
    }
    
    # Transform the raw data
    transformed_data = transform(raw_data)
    
    # Verify the transformation of drivers
    assert transformed_data['drivers'][0]['full_name'] == 'Driver 1'
    assert transformed_data['drivers'][0]['headshot_url'] == 'http://example.com'
    assert transformed_data['drivers'][1]['headshot_url'] == ''  # Missing field handled

    # Verify the transformation of laps
    assert transformed_data['laps'][0]['lap_duration'] == 85.6
    
    # Verify the transformation of races
    assert transformed_data['races'][0]['year'] == 2024

    # Verify the transformation of position
    assert transformed_data['position'][0]['position'] == 1
