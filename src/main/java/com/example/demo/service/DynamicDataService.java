package com.example.demo.service;

import com.example.demo.dao.DynamicDataRepository;
import com.example.demo.model.DynamicData;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class DynamicDataService {


    private final DynamicDataRepository dynamicDataRepository;

    public DynamicDataService(DynamicDataRepository dynamicDataRepository) {
        this.dynamicDataRepository = dynamicDataRepository;
    }

    public void generateData(){

    }

    @Async
    public void saveDynamicData(DynamicData dynamicData){
        System.out.println("save dynamic data");
        //dynamicDataRepository.save(dynamicData);
    }
}

