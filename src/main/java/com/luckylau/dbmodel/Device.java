package com.luckylau.dbmodel;

import com.luckylau.model.Deleted;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class Device extends Deleted {
    private Long id;
    @NotBlank
    @Length(max = 32, message = "名称长度不能超过32个字符")
    private String gateway;
    private String callNumber;
    @Length(max = 250, message = "描述长度不能超过256个字符")
    private String description;
    private Date created;
    private Date updated;
}
