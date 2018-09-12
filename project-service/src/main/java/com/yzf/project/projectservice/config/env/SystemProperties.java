package com.yzf.project.projectservice.config.env;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * @author zhengjianhui on 18/3/16
 */
@Configuration
public class SystemProperties {

    @Autowired
    private Environment env;

    private Properties properties;

    @PostConstruct
    private void readProperty() {
        try {
            properties = this.getConfigProperties();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Properties getConfigProperties() throws IOException {
        String theDevEnvironment = "dev";
        String path;
        String[] selector = env.getActiveProfiles();
        List<Resource> resources;
        if (theDevEnvironment.equals(selector[0])) {
            ResourcePatternResolver loader = new PathMatchingResourcePatternResolver();
            path = "classpath*:properties/dev/*.properties";
            resources = Arrays.asList(loader.getResources(path));


        } else {
            path = System.getProperties().getProperty("user.dir") + "/config";
            File dir = new File(path);
            if (!dir.isDirectory()) {
                throw new RuntimeException("配置文件存放目录异常, path : " + path);
            }

            File[] files = dir.listFiles();
            resources = new ArrayList(files.length);
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (!file.getName().endsWith(".properties")) {
                    continue;
                }

                Resource resource = new FileSystemResource(file.getPath());
                resources.add(resource);

            }
        }

        Properties props = new Properties();
        loadProperties(resources, props);

        return props;
    }


    private void loadProperties(List<Resource> resources, Properties props) throws IOException {
        for (Resource resource : resources) {
            try {
                PropertiesLoaderUtils.fillProperties(props, resource);
            } catch (IOException e) {
                throw e;
            }
        }
    }

    public String getValueByKey(String key) {
        return this.properties.getProperty(key);
    }

}
