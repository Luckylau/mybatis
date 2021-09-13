package com.lucklau.base;

import com.github.pagehelper.PageInfo;
import com.lucklau.base.Condition.ConditionBuilder;
import com.lucklau.model.IncrementId;
import com.lucklau.model.Pager;
import com.lucklau.utils.ClassUtil;
import com.lucklau.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.lucklau.base.Condition.FIELD_OPERATOR_DELIMETER;
import static com.lucklau.utils.ClassUtil.getDbFieldNames;
import static com.lucklau.utils.RequestUtil.getRequest;

/**
 * luckylau  2018/5/21 下午7:59
 */
public abstract class BaseController<T extends IncrementId, K extends BaseService> {
    @Autowired
    protected K baseService;
    private Class<T> entityClass;

    protected List<T> find() {
        return baseService.find(getConditions().build());
    }

    protected PageInfo<T> find(Pager pager) {
        return baseService.find(getConditions().build(), pager);
    }

    protected ConditionBuilder getConditions() {
        Map<String, String> fields = getDbFieldNames(getEntityClass());
        ConditionBuilder builder = new ConditionBuilder();
        getRequest().getParameterMap().entrySet().stream().filter(e -> fields.containsKey(e.getKey().split(FIELD_OPERATOR_DELIMETER)[0]))
                .forEach(e -> builder.add(e.getKey(), e.getValue()[0]));
        return builder;
    }

    protected ConditionBuilder getConditions(Class<?> entityClass) {
        Map<String, String> fields = getDbFieldNames(entityClass);
        ConditionBuilder builder = new ConditionBuilder();
        getRequest().getParameterMap().entrySet().stream().filter(e -> fields.containsKey(e.getKey().split(FIELD_OPERATOR_DELIMETER)[0]))
                .forEach(e -> builder.add(e.getKey(), e.getValue()[0]));
        return builder;
    }

    private Class<T> getEntityClass() {
        if (this.entityClass == null) {
            entityClass = ClassUtil.getGenericsClass(getClass());
        }
        return this.entityClass;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(DateUtil.getSimpleDateFormat(null), true));
    }

}