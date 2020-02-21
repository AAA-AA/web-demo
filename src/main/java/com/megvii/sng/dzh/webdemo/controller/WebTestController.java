package com.megvii.sng.dzh.webdemo.controller;

import com.google.common.collect.Lists;
import com.megvii.sng.dzh.webdemo.domain.CountObj;
import com.megvii.sng.dzh.webdemo.service.CalculateService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
@RequestMapping(value = "/webApi")
@Slf4j
public class WebTestController {

    @Autowired
    private CalculateService calculateService;

    private AtomicBoolean atomicBoolean = new AtomicBoolean();

    @ApiOperation(value = "加法接口")
    @RequestMapping(value = "/count", method = RequestMethod.POST)
    @ResponseBody
    public List<Integer> count(@RequestBody CountObj countObj) {
        return calculateService.countResult(countObj);
    }

    @ApiOperation(value = "耗cpu接口")
    @RequestMapping(value = "/hover", method = RequestMethod.GET)
    @ResponseBody
    public void hover() throws InterruptedException {
        atomicBoolean.set(!atomicBoolean.get());
        while (atomicBoolean.get()) {
            log.info("循环打印........");
            TimeUnit.MICROSECONDS.sleep(1);
        }
    }


}
