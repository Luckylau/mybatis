package com.luckylau.generate;

import com.google.common.io.CharStreams;
import com.luckylau.model.IncrementId;
import com.luckylau.utils.CommonUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.luckylau.utils.ClassUtil.getClassBySupperClass;

/**
 * Created by luckylau on 2018/5/13 下午4:00
 */
@Slf4j
public class DynamicMapperSqlSessionBean extends SqlSessionFactoryBean {
    private String dataSource;

    public DynamicMapperSqlSessionBean(String dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void setMapperLocations(Resource[] mapperLocations) {
        try {
            List<Class<IncrementId>> classes = getClassBySupperClass(IncrementId.class, dataSource + "dbmodel");
            List<MapperSplitter> splitters = classes.stream()
                    .map(x -> {
                        String mapperString = MybatisCRUDGenerator.buildMapper(x);
                        log.debug("mapper:{},{}", x, mapperString);

                        return new MapperSplitter(mapperString);
                    })
                    .collect(Collectors.toList());
            Resource[] resources = mergeMapperResources(mapperLocations, splitters);
            log.debug("resources:{}", resources);

            super.setMapperLocations(resources);
        } catch (Exception e) {
            CommonUtil.throwAs(e);
        }
    }

    private Resource[] mergeMapperResources(Resource[] sources, List<MapperSplitter> dynamicResources) throws Exception {
        List<Resource> mergedResources = new ArrayList<>();
        Map<String, MapperSplitter> mapperMap = new HashMap<>();
        for (Resource resource : sources) {
            MapperSplitter splitter = new MapperSplitter(CharStreams.toString(new InputStreamReader(resource.getInputStream())), resource);
            mapperMap.put(splitter.getNamespace(), splitter);
        }
        for (MapperSplitter dynamicMapper : dynamicResources) {
            MapperSplitter mapper = mapperMap.get(dynamicMapper.getNamespace());
            if (mapper != null) {
                String finalMapper = dynamicMapper.getMapperText().replace(dynamicMapper.getSqlCrud(), dynamicMapper.getSqlCrud() + mapper.getSqlCrud());
                mapperMap.remove(mapper.getNamespace());
                mergedResources.add(new InputStreamResource(new ByteArrayInputStream(finalMapper.getBytes("UTF-8")), mapper.getNamespace()));
            } else {
                mergedResources.add(new InputStreamResource(new ByteArrayInputStream(dynamicMapper.getMapperText().getBytes("UTF-8")), dynamicMapper.getNamespace()));
            }
        }
        for (MapperSplitter splitter : mapperMap.values()) {
            mergedResources.add(splitter.getResource());
        }
        return mergedResources.toArray(new Resource[]{});
    }

    @Getter
    private static class MapperSplitter {
        private static Pattern pattern = Pattern.compile("<mapper *namespace *= *\"(.+?)\" *>([\\S\\s]*)</mapper>");
        private String mapperText;
        private String namespace;
        private String sqlCrud;
        private Resource resource;

        MapperSplitter(String mapperText, Resource resource) {
            this(mapperText);
            this.resource = resource;
        }

        MapperSplitter(String mapperText) {
            this.mapperText = mapperText;
            Matcher matcher = pattern.matcher(mapperText);
            if (matcher.find()) {
                this.namespace = matcher.group(1);
                this.sqlCrud = matcher.group(2);
            } else {
                throw new RuntimeException();
            }
        }
    }

}
