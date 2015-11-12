package cn.com.eastsoft.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by baolei on 2015/11/12.
 */
public class PropertiesUtil {

    private static PropertiesUtil instance;
    private Properties properties;
    public static PropertiesUtil getInstance(){
        if(null==instance){
            instance = new PropertiesUtil();
        }
        return instance;
    }
    private PropertiesUtil(){
        try {
            String path = PropertiesUtil.class.getClassLoader().
                    getResource("resources.properties").getPath();
            System.out.println(path);
            File file = new File(path);
            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            properties = new Properties();
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }
}
