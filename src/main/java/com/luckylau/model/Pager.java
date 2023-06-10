package com.luckylau.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author luckylau
 */
@Getter
@Setter
public class Pager {
    private int pageNum = 1;
    private int pageSize = 20;
    private boolean count = true;
    private String orderBy;
}