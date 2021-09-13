package com.lucklau.controller;

import com.lucklau.base.BaseController;
import com.lucklau.base.Condition;
import com.lucklau.dbmodel.Device;
import com.lucklau.model.Pager;
import com.lucklau.model.Result;
import com.lucklau.service.DeviceService;
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
