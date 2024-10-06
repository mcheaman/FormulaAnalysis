"""
transform.py

This module handles the transformation of raw MongoDB data into a relational format suitable 
for loading into a PostgreSQL database. The transformation process will clean and structure 
the data appropriately.
"""

class Transform:
    """
    A class that handles the transformation of raw MongoDB data into a relational format
    suitable for loading into PostgreSQL.
    """
    
    def __init__(self):
        """
        Initializes the TransformData class.
        """

    def transform(self, data):
        """
        Transform raw data into a relational format.

        Cleans and structures the data extracted from MongoDB, converting it into a format 
        suitable for relational databases (PostgreSQL).

        Args:
            data (dict): The raw data extracted from MongoDB.

        Returns:
            transformed_data (dict of dicts): The transformed data in relational format, 
            ready to be loaded into PostgreSQL.
        """
        transformed_data = {}

        # Transform races data
        transformed_data['races'] = [
            {
                'session_key': race['_id'],
                'year': race['year'],
                'session_name': race['sessionName'],
                'country_name': race['countryName'],
                'circuit_name': race['circuitName']
            }
            for race in data['races']
        ]
        
        # Transform drivers data
        transformed_data['drivers'] = [
            {
                'driver_number': driver['driverNumber'],
                'full_name': driver['_id'],
                'team': driver['team'],
                'country_code': driver['countryCode'],
                'headshot_url': driver.get('headshotUrl', '')
            }
            for driver in data['drivers']
        ]

        # Transform laps data
        transformed_data['laps'] = [
            {
                'session_key': lap['sessionKey'],
                'driver_number': lap['driverNumber'],
                'lap_number': lap['lapNumber'],
                'lap_duration': lap['lapDuration'],
                'sector1': lap['sector1'],
                'sector2': lap['sector2'],
                'sector3': lap['sector3'],
                'speed_trap_speed': lap['speedTrapSpeed'],
                'is_pit_out_lap': lap['isPitOutLap']
            }
            for lap in data['laps']
        ]

        # Transform positions data
        transformed_data['positions'] = [
            {
                'session_key': position['sessionKey'],
                'driver_number': position['driverNumber'],
                'position': position['position']
            }
            for position in data['position']
        ]

        # Transform latest session data
        transformed_data['latest_session'] = [
            {
                'session_key': session['sessionKey'],
                'session_end_date': session['sessionEndDate'],
                'session_name': session['sessionName']
            }
            for session in data['latest_session']
        ]
        print("Data transformation complete")
        return transformed_data
