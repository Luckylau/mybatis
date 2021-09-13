package com.lucklau.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author luckylau
 */
@Getter
@Setter
public class Deleted extends IncrementId {
    //0:正常 1:已删除
    private Integer deleted;
}