package com.example.demo.dao;

import com.example.demo.model.Location;
import org.springframework.data.repository.CrudRepository;

public interface LocationRepository extends CrudRepository<Location,Integer> {
}
