package com.example.demo.dao;

import com.example.demo.model.DynamicData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DynamicDataRepository extends CrudRepository<DynamicData,Long> {

}
