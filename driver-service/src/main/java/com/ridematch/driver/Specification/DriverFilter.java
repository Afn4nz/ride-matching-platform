package com.ridematch.driver.Specification;

import com.ridematch.driver.entity.Driver;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
    @Spec(path = "isVerified", params = "isVerified", spec = Equal.class),
    @Spec(path = "status", params = "status", spec =  Equal.class)
})
public interface DriverFilter extends Specification<Driver> {}