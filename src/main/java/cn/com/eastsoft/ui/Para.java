package cn.com.eastsoft.ui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Para
{
	//二维码匹配正则表达式
	public final static String regex1 = "(SN)(\\d{1,24})([A-Z' ''\n''\r']{1,8})"
			+ "(\\d{1,10})([A-Z' ''\n''\r']{1,8})(\\d{6})$";
	public final static String regex2 = "(SN)(\\w{1,24})(JUSER)(\\d{1,10})(JPWD)(\\d{6})$";

	//电力线适配器二维码
	public static String labelInfoRex;
	public static String[] mapKey;
	public static Map<String,String> mapRex;

	public static void setRex(int moduleSelect){
		mapRex = new HashMap();
		try {
			String path = Para.class.getClassLoader().
					getResource("resources.properties").getPath();
			System.out.println(path);
			File file = new File(path);
			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			Properties properties = new Properties();
			properties.load(inputStream);

			String modulename = null;
			if(moduleSelect==0){
				modulename = "powerlineAdapter.";
			}else if(moduleSelect==1){
				modulename = "wirelessExpander.";
			}else if(moduleSelect==2){
				modulename = "wirelessRouter.";
			}
			labelInfoRex = properties.getProperty(modulename+"labelInfoRex");
			mapKey = properties.getProperty(modulename+"labelInfoKey").split("-");
			for (String str:mapKey){
				mapRex.put(str,properties.getProperty(modulename+str));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
