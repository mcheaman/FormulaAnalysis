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
    public List<Driver> addDrivers(List<Driver> driversToAdd) {
        return driverRepository.saveAll(driversToAdd);
    }

    public void deleteDriver(String id) {
        driverRepository.deleteById(id);
    }

    public Optional<Driver> getDriverByName(String driverName) {return driverRepository.findByFullName(driverName);}

}
