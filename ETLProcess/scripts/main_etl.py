"""
main_etl.py

This module orchestrates the ETL process by calling the extract, transform, and load functions 
in the correct order. The ETL pipeline extracts data from MongoDB, transforms it into a 
relational format, and loads it into a PostgreSQL database.
"""

from extract import extract
from transform import transform
from load import load

def main():
    """
    The main ETL pipeline.

    This function orchestrates the ETL process by extracting data from MongoDB, transforming it into 
    a relational format, and loading it into PostgreSQL.
    
    Returns:
        None
    """
    # Step 1: Extract data from MongoDB
    extracted_data = extract()
    
    # Step 2: Transform the extracted data
    transformed_data = transform(extracted_data)
    
    # Step 3: Load the transformed data into PostgreSQL
    load(transformed_data)

if __name__ == "__main__":
    main()
