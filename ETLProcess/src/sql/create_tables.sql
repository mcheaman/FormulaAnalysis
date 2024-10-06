-- Create drivers table
CREATE TABLE IF NOT EXISTS drivers (
    driver_number INT PRIMARY KEY,
    full_name TEXT NOT NULL,
    team TEXT,
    country_code TEXT,
    headshot_url TEXT
);

-- Create laps table
CREATE TABLE IF NOT EXISTS laps (
    session_key INT NOT NULL,
    driver_number INT NOT NULL,
    lap_number INT NOT NULL,
    lap_duration FLOAT,
    sector1 FLOAT,
    sector2 FLOAT,
    sector3 FLOAT,
    speed_trap_speed INT,
    is_pit_out_lap BOOLEAN,
    PRIMARY KEY (session_key, driver_number, lap_number)
);

-- Create latest_session table
CREATE TABLE IF NOT EXISTS latest_session (
    session_key INT PRIMARY KEY,
    session_end_date DATE,
    session_name TEXT
);

-- Create races table
CREATE TABLE IF NOT EXISTS races (
    session_key INT PRIMARY KEY,
    year INT NOT NULL,
    session_name TEXT,
    country_name TEXT,
    circuit_name TEXT
);

-- Create positions table
CREATE TABLE IF NOT EXISTS positions (
    session_key INT NOT NULL,
    driver_number INT NOT NULL,
    position INT,
    PRIMARY KEY (session_key, driver_number)
);
