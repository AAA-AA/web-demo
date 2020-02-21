package com.megvii.sng.dzh.webdemo.service;

import com.google.common.collect.Lists;
import com.megvii.sng.dzh.webdemo.domain.CountObj;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalculateService {

    public List<Integer> countResult(CountObj countObj) {
        return Lists.newArrayList(countObj.getNum1() + countObj.getNum2());
    }


}
