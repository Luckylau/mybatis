package com.lucklau.generate;

import com.google.common.io.CharStreams;
import com.lucklau.metadata.DbColumn;
import com.lucklau.metadata.DbDefaultValue;
import com.lucklau.metadata.DbTable;
import com.lucklau.model.Deleted;
import com.lucklau.utils.ClassUtil;
import com.lucklau.utils.CommonUtil;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.*;

import static com.lucklau.utils.StringUtil.*;

/**
 * Created by luckylau on 2018/5/13 下午4:20
 * 基于Mybatis的基本sqlMqpper动态生成器
 */
public class MybatisCRUDGenerator {
    private static final String ID_WHERE_SQL = " where id = #{id}";
    private static final String IDS_WHERE_SQL = " where id in <include refid=\"common.idsForEach\"/>";
    private static String templateFile = "/dynamicSql.template";

    public static <T> String buildMapper(Class<T> clazz) {
        String tableName = Optional.ofNullable(clazz.getAnnotation(DbTable.class))
                .map(DbTable::value).orElse(camelToUnderscore(lowerFirstLetter(clazz.getSimpleName())));
        Map<String, String> columns = ClassUtil.getDbFieldNames(clazz);
        List<Field> fields = ClassUtil.getDbFields(clazz);

        Map<String, Object> context = new HashMap<>();

        context.put("insert", insertSql(tableName, columns, false));
        context.put("insertBatch", insertBatchSql(tableName, columns, false));
        context.put("replaceInsert", insertSql(tableName, columns, true));
        context.put("replaceInsertBatch", insertBatchSql(tableName, columns, true));

        context.put("update", updateSql(tableName, columns, false));
        context.put("updateBatch", updateSql(tableName, columns, true));
        context.put("updateAll", updateAllSql(tableName, fields, false));
        context.put("updateAllBatch", updateAllSql(tableName, fields, true));

        boolean deleted = Deleted.class.isAssignableFrom(clazz);
        context.put("delete", deleteSql(tableName, false, deleted));
        context.put("deleteById", deleteSql(tableName, false, deleted));
        context.put("deleteByIds", deleteSql(tableName, true, deleted));

        context.put("findById", findByIdSql(tableName, columns));
        context.put("findByIds", findByIdsSql(tableName, columns));
        context.put("find", findSql(tableName, clazz, columns));
        context.put("findIds", findIdsSql(tableName, clazz, columns));
        context.put("findAll", findAllSql(tableName, clazz, columns));

        context.put("tableName", tableName);
        context.put("entityClass", clazz.getName());
        context.put("daoClass", clazz.getName().replace("dbmodel", "dao") + "Dao");
        try {
            return parseTemplate("${", "}"
//							, String.join("\r\n", Files.readAllLines(Paths.get(MybatisCRUDGenerator.class.getClassLoader().getResource(templateFile).toURI())))
                    , CharStreams.toString(new InputStreamReader(MybatisCRUDGenerator.class.getResourceAsStream(templateFile)))
                    , context);
        } catch (Exception e) {
            CommonUtil.throwAs(e);
            return null;
        }
    }

    private static String insertSql(String tableName, Map<String, String> columns, boolean isReplaced) {
        StringBuilder insertSql = new StringBuilder();
        StringBuilder valueSql = new StringBuilder();
        insertSql.append(isReplaced ? "replace" : "insert");
        insertSql.append(" into ").append(tableName).append("(");
        insertSql.append("<trim suffixOverrides=','>");
        valueSql.append("<trim suffixOverrides=','>");
        for (Map.Entry<String, String> column : columns.entrySet()) {
            if ("id".equals(column.getKey()) || "deleted".equals(column.getKey())) {
                continue;
            }
            if ("created".equals(column.getKey()) || "updated".equals(column.getKey())) {
                insertSql.append(wrap(camelToUnderscore(column.getValue()))).append(",");
                valueSql.append("NOW(),");
                continue;
            }
            insertSql.append("<if test=\"").append(column.getKey()).append(" != null\" >");
            insertSql.append(wrap(camelToUnderscore(column.getValue()))).append(",");
            insertSql.append("</if>");
            valueSql.append("<if test=\"").append(column.getKey()).append(" != null\" >");
            valueSql.append("#{").append(column.getKey()).append("},");
            valueSql.append("</if>");
        }
        valueSql.append("</trim>");
        insertSql.append("</trim>) values (").append(valueSql).append(")");
        return insertSql.toString();
    }

    private static String insertBatchSql(String tableName, Map<String, String> columns, boolean isReplaced) {
        StringBuilder insertSql = new StringBuilder();
        insertSql.append(isReplaced ? "replace" : "insert");
        insertSql.append(" into ").append(tableName).append("(");
        StringBuilder valueSql = new StringBuilder("<foreach collection=\"entities\" item=\"entity\" index=\"index\" separator=\",\"> (");
        for (Map.Entry<String, String> column : columns.entrySet()) {
            if ("id".equals(column.getKey()) || "deleted".equals(column.getKey())) {
                continue;
            }
            if ("created".equals(column.getKey()) || "updated".equals(column.getKey())) {
                insertSql.append(wrap(camelToUnderscore(column.getValue()))).append(",");
                valueSql.append("NOW(),");
                continue;
            }
            insertSql.append(wrap(camelToUnderscore(column.getValue()))).append(",");
            valueSql.append("#{entity.").append(column.getKey()).append("},");
        }
        insertSql.deleteCharAt(insertSql.length() - 1);
        valueSql.deleteCharAt(valueSql.length() - 1).append(") </foreach>");
        return insertSql.append(") values ").append(valueSql).toString();
    }

    private static String updateSql(String tableName, Map<String, String> columns, boolean batch) {
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(tableName).append(" <set>");
        for (Map.Entry<String, String> column : columns.entrySet()) {
            if ("id".equals(column.getKey()) || "createdby".equals(column.getKey()) || "created".equals(column.getKey()) || "updated".equals(column.getKey())) {
                continue;
            }
            sb.append("<if test=\"").append(batch ? "entity." + column.getKey() : column.getKey()).append("!= null\"> ");
            sb.append(wrap(camelToUnderscore(column.getValue()))).append("=");
            sb.append("#{").append(batch ? "entity." + column.getKey() : column.getKey()).append("},");
            sb.append("</if>");
        }
        sb.append("</set>");
        sb.append(batch ? IDS_WHERE_SQL : ID_WHERE_SQL);
        return sb.toString();
    }

    private static String updateAllSql(String tableName, List<Field> fields, boolean batch) {
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(tableName).append(" <set>");
        for (Field field : fields) {
            String column = field.getName();
            if ("id".equals(column) || "createdby".equals(column) || "created".equals(column) || "updated".equals(column)) {
                continue;
            }
            sb.append(wrap(Optional.ofNullable(field.getAnnotation(DbColumn.class)).
                    map(DbColumn::value).orElse(camelToUnderscore(column)))).append("=");
            sb.append("<choose>");
            sb.append("<when test=\"").append(batch ? "entity." + column : column).append("!= null\"> ");
            sb.append("#{").append(batch ? "entity." + column : column).append("},");
            sb.append("</when>");
            sb.append("<otherwise>");
            sb.append(Optional.ofNullable(field.getAnnotation(DbDefaultValue.class)).map(DbDefaultValue::value).orElse(null)).append(",");
            sb.append("</otherwise>");
            sb.append("</choose>");
        }
        sb.append("</set>");
        sb.append(batch ? IDS_WHERE_SQL : ID_WHERE_SQL);
        return sb.toString();
    }

    private static String deleteSql(String tableName, boolean batch, boolean deleted) {
        StringBuilder deleteSql = new StringBuilder();
        if (deleted) {
            deleteSql.append("update ").append(tableName).append(" set deleted = 1")
                    .append(batch ? IDS_WHERE_SQL : ID_WHERE_SQL);
        } else {
            deleteSql.append("delete from ").append(tableName)
                    .append(batch ? IDS_WHERE_SQL : ID_WHERE_SQL);
        }
        return deleteSql.toString();
    }

    private static String findByIdSql(String tableName, Map<String, String> columns) {
        return getSelectSql(tableName, columns).append(ID_WHERE_SQL).toString();
    }

    private static String findByIdsSql(String tableName, Map<String, String> columns) {
        return getSelectSql(tableName, columns).append(IDS_WHERE_SQL).toString();
    }

    private static <T> String findSql(String tableName, Class<T> clazz, Map<String, String> columns) {
        StringBuilder findSql = getSelectSql(tableName, columns).append(" where")
                .append(Deleted.class.isAssignableFrom(clazz) ? " deleted = 0" : " 1 = 1")
                .append("<include refid=\"common.dynamicConditions\"/>");
        return findSql.toString();
    }

    private static <T> String findIdsSql(String tableName, Class<T> clazz, Map<String, String> columns) {
        StringBuilder findSql = getSelectSql(tableName, columns).append(" where")
                .append(Deleted.class.isAssignableFrom(clazz) ? " deleted = 0" : " 1 = 1")
                .append("<include refid=\"common.dynamicConditions\"/>");
        return findSql.toString();
    }

    private static <T> String findAllSql(String tableName, Class<T> clazz, Map<String, String> columns) {
        StringBuilder findSql = getSelectSql(tableName, columns);
        if (Deleted.class.isAssignableFrom(clazz)) {
            findSql.append(" where deleted = 0");
        }
        return findSql.toString();
    }

    private static StringBuilder getSelectSql(String tableName, Map<String, String> columns) {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, String>> entries = columns.entrySet().iterator();
        sb.append("select * ");
        while (entries.hasNext()) {
            Map.Entry<String, String> column = entries.next();
            if (!column.getKey().equals(column.getValue())) {
                sb.append(", ").append(wrap(camelToUnderscore(column.getValue()))).append(" as ").append(column.getKey());
            }
        }
        return sb.append(" from ").append(tableName);
    }

}