package cn.com.eastsoft.ui;

import cn.com.eastsoft.util.ToolUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Para {
    //二维码匹配正则表达式
    public final static String regex1 = "(SN)(\\d{1,24})([A-Z' ''\n''\r']{1,8})"
            + "(\\d{1,10})([A-Z' ''\n''\r']{1,8})(\\d{6})$";
    public final static String regex2 = "(SN)(\\w{1,24})(JUSER)(\\d{1,10})(JPWD)(\\d{6})$";

    //电力线适配器二维码
    public static String labelInfoRex;
    public static String[] mapKey;
    public static int[] matcherIndex;
    public static Map<String, String> mapRex;

    public static String labelInfoRexOld;
    public static String[] mapKeyOld;
    public static int[] matcherIndexOld;

    public static void setRex(int moduleSelect) {
        mapRex = new HashMap();
        try {
            String path = Para.class.getClassLoader().
                    getResource("resources.properties").getPath();
            System.out.println(path);
            File file = new File(path);
            if(false==file.exists()){
                MainJFrame.showMssageln("非开发环境中运行,读取程序根目录中的resources.properties配置文件...");
                path = ToolUtil.getNowPath()+"resources.properties";
                file = new File(path);
            }
            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            Properties properties = new Properties();
            properties.load(inputStream);

            String modulename = null;
            if (moduleSelect == 0) {
                modulename = "powerlineAdapter.";
            } else if (moduleSelect == 1) {
                modulename = "wirelessExpander.";
            } else if (moduleSelect == 2) {
                modulename = "wirelessRouter.";
            }
            String tmp = null;
            String[] labelV = {"", "Old"};

            labelInfoRex = properties.getProperty(modulename + "labelInfoRex");
            labelInfoRexOld = properties.getProperty(modulename + "labelInfoRexOld");

            tmp = properties.getProperty(modulename + "labelInfoKey");
            mapKey = (tmp == null ? null : tmp.split("-"));
            tmp = properties.getProperty(modulename + "labelInfoKeyOld");
            mapKeyOld = (tmp == null ? null : tmp.split("-"));

            tmp = properties.getProperty(modulename + "matcherIndex");
            if (tmp != null) {
                String[] index = tmp.split("-");
                matcherIndex = new int[index.length];
                for (int i = 0; i < index.length; i++) {
                    matcherIndex[i] = Integer.parseInt(index[i]);
                }
            }

            tmp = properties.getProperty(modulename + "matcherIndexOld");
            if (tmp != null) {
                String[] index = tmp.split("-");
                matcherIndexOld = new int[index.length];
                for (int i = 0; i < index.length; i++) {
                    matcherIndexOld[i] = Integer.parseInt(index[i]);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test1(String labelInfo) {

        Pattern pattern1 = Pattern.compile(labelInfoRex);
        Matcher matcher1 = pattern1.matcher(labelInfo);
        int count = matcher1.groupCount();
        System.out.println("匹配数量=" + count);

        if (matcher1.find()) {
            for (int i = 1; i <= count; i++) {
                System.out.println(i + ":" + matcher1.group(i));
            }
        } else {
            System.out.println("else");
        }
    }
}
