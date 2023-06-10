package com.luckylau.base;

import com.luckylau.enums.EnuFieldType;
import com.luckylau.enums.EnuOperatorType;
import com.luckylau.utils.StringUtil;
import lombok.Getter;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.luckylau.enums.EnuFieldType.STRING;
import static com.luckylau.enums.EnuOperatorType.*;
import static com.luckylau.utils.ClassUtil.castType;
import static com.luckylau.utils.EnumUtil.getEnum;
import static com.luckylau.utils.EnumUtil.valid;
import static com.luckylau.utils.StringUtil.splitString2Collection;

public class Condition implements Serializable {

    //字段-操作符-类型 eg:name-eq-string
    public static final String FIELD_OPERATOR_DELIMETER = "-";

    @Getter
    private String field;
    private Object value;
    @Getter
    private Object newValue;
    @Getter
    private EnuOperatorType operator = EQ;
    private EnuFieldType type = STRING;

    public static Condition parseCondition(String parameter, Object value) {
        return new Condition().parse(parameter, value);
    }

    private Condition parse(String parameter, Object value) {
        Assert.notNull(parameter, "condition[parameter]不能为空");
        String[] array = parameter.split(Condition.FIELD_OPERATOR_DELIMETER);
        field = StringUtil.camelToUnderscore(array[0]);
        this.value = value;
        if (array.length == 2) {
            if (valid(EnuFieldType.class, array[1])) {
                type = (EnuFieldType) getEnum(EnuFieldType.class, array[1]);
            } else {
                operator = (EnuOperatorType) getEnum(EnuOperatorType.class, array[1]);
            }
        } else if (array.length == 3) {
            boolean isType = valid(EnuFieldType.class, array[1]);
            type = (EnuFieldType) getEnum(EnuFieldType.class, isType ? array[1] : array[2]);
            operator = (EnuOperatorType) getEnum(EnuOperatorType.class, isType ? array[2] : array[1]);
        }
        if (operator != ISNULL && operator != ISNOTNULL) {
            Assert.notNull(value, "condition[parameter=" + parameter + ",value为空]");
        }
        setNewValue();
        return this;
    }

    public void setNewValue() {
        if (type == null || operator == null) {
            throw new IllegalArgumentException("解析条件参数传入不正确");
        }
        if (operator == ISNULL || operator == ISNOTNULL) {
            return;
        }
        if (operator == LIKE || operator == LLIKE || operator == RLIKE) {
            Assert.isTrue(type == STRING, "非字符串不支持[" + operator.getType() + "]操作");
            String thisVal = value.toString().trim()
                    .replaceAll("_", "\\\\_").replaceAll("%", "\\\\%");
            if (operator == LIKE) {
                newValue = "%" + thisVal + "%";
            } else if (operator == LLIKE) {
                newValue = "%" + thisVal;
            } else {
                newValue = thisVal + "%";
            }
        } else if (operator == IN || operator == NOTIN) {
            newValue = value instanceof String ? splitString2Collection(value.toString().trim(), type.getValue()) : value;
        } else {
            newValue = value instanceof String ? castType(value, type.getValue()) : value;
        }
    }

    public static class ConditionBuilder {
        List<Condition> conditions = new ArrayList<>();

        public ConditionBuilder() {
        }

        public ConditionBuilder(String parameter, Object value) {
            conditions.add(parseCondition(parameter, value));
        }

        public ConditionBuilder add(String parameter, Object value) {
            conditions.add(parseCondition(parameter, value));
            return this;
        }

        public ConditionBuilder addAll(List<Condition> conditions) {
            this.conditions.addAll(conditions);
            return this;
        }

        public List<Condition> build() {
            return conditions;
        }
    }

}
