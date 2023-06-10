package com.luckylau.controller;

import com.luckylau.base.BaseController;
import com.luckylau.base.Condition;
import com.luckylau.dbmodel.Device;
import com.luckylau.model.Pager;
import com.luckylau.model.Result;
import com.luckylau.service.DeviceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @Author luckylau
 * @Date 2019/6/13
 */
@Controller
@RequestMapping("/devices")
public class DeviceController extends BaseController<Device, DeviceService> {

    @RequestMapping(method = RequestMethod.GET)
    public Result findPageList(Pager pager) {
        List<Condition> conditions = getConditions()
                .add("deleted", false)
                .build();

        return Result.success(baseService.find(conditions, pager));
    }
}
