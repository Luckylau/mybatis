package com.luckylau.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author luckylau
 */
@Getter
@Setter
public class IncrementId implements Serializable {
    private Long id;
}