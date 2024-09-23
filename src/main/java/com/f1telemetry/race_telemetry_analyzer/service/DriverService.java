package com.f1telemetry.race_telemetry_analyzer.service;

import com.f1telemetry.race_telemetry_analyzer.model.Driver;
import com.f1telemetry.race_telemetry_analyzer.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Optional<Driver> getDriverById(String id) {
        return driverRepository.findById(id);
    }

    public Driver addDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    public void deleteDriver(String id) {
        driverRepository.deleteById(id);
    }

    public Optional<Driver> getDriverByName(String driverName) {return driverRepository.findByName(driverName);}

    // New method to update the driver by name (or any other field)
    public Driver updateDriver(String name, Driver updatedDriverInfo) {
        Optional<Driver> existingDriver = driverRepository.findByName(name);

        if (existingDriver.isPresent()) {
            Driver driver = existingDriver.get();
            driver.setName(updatedDriverInfo.getName());
            driver.setTeam(updatedDriverInfo.getTeam());
            driver.setCountry_code(updatedDriverInfo.getCountry_code());
            driver.setDriver_number(updatedDriverInfo.getDriver_number());
            driver.setHeadshot_url(updatedDriverInfo.getHeadshot_url());
            return driverRepository.save(driver);
        } else {
            throw new IllegalArgumentException("Driver not found with name: " + name);
        }
    }
}
