"""
maint_etl.py

This script manages the full ETL (Extract, Transform, Load) process, 
using the Extract, Transform, and Load classes.
"""

from extract import Extract
from transform import Transform
from load import Load

def main():
    # Step 1: Extract data from MongoDB
    extractor = Extract()
    raw_data = extractor.extract()
     
    # Step 2: Transform extracted data into a relational form
    transformer = Transform()
    transformed_data = transformer.transform(raw_data)
    
    # Step 3: Load transformed data into PostgreSQL database
    loader = Load()
    loader.load(transformed_data)

if __name__ == "__main__":
    main()
