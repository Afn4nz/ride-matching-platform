package com.ridematch.driver.repository;

import com.ridematch.driver.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository
        extends JpaRepository<Driver, Long>, JpaSpecificationExecutor<Driver> {}
