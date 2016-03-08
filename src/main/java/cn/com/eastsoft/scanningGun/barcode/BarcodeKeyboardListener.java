package cn.com.eastsoft.scanningGun.barcode;

import java.util.HashMap;
import java.util.Map;

import cn.com.eastsoft.ui.powerline.GeneralSet;


/**
 *扫码枪模拟的键盘按钮事件监听（0-9键和回车键）
 * 关键算法：条形码扫描器在很短的时间内输入了至少 barcodeMinLength 个字符以上信息，并且以“回车”作为结束字符，并且一次扫描要在 maxScanTime 毫秒内完成
 * 字符数及扫描时间可根据具体情况设置
 * @author tbl
 */
public class BarcodeKeyboardListener{
    //条形码数据缓充区
    private StringBuilder barcode;
    //扫描开始时间
    private long start;
    private Map<Integer,Character> keyToLetter=new HashMap<Integer,Character>();
    //一次扫描的最长时间
    private static int maxScanTime=3000;
    //条形码的最短长度
    private static int barcodeMinLength=6;

    /**
     * 初始键盘代码和字母的对于关系
     */
    public BarcodeKeyboardListener(){
        keyToLetter.put(46,'.');
        keyToLetter.put(48,'0');
        keyToLetter.put(49,'1');
        keyToLetter.put(50,'2');
        keyToLetter.put(51,'3');
        keyToLetter.put(52,'4');
        keyToLetter.put(53,'5');
        keyToLetter.put(54,'6');
        keyToLetter.put(55,'7');
        keyToLetter.put(56,'8');
        keyToLetter.put(57,'9');

        keyToLetter.put(58,':');
        keyToLetter.put(13,'\n');
        keyToLetter.put(32,' ');
        char[] letter = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        for(int i=0;i<letter.length;i++){
            keyToLetter.put(65+i,letter[i]);
        }
    }
    /**
     * 此方法响应扫描枪事件
     * @param keyCode 
     */
    public void onKey(int keyCode) {
        //获取输入的是那个数字
        char letter = '\0';
        if(barcode==null){
            //开始进入扫描状态
            barcode=new StringBuilder();
            //记录开始扫描时间
            start=System.currentTimeMillis();
        }
        //需要判断时间
        long cost=System.currentTimeMillis()-start;
        if(cost > maxScanTime){
             //开始进入扫描状态
            barcode=new StringBuilder();
            //记录开始扫描时间
            start=System.currentTimeMillis();
        }
        //数字键0-9 A-Z :
        if ((keyCode >= 48 && keyCode <= 58)||(keyCode>=65&&keyCode<=90)) {
            letter = keyToLetter.get(keyCode);
            barcode.append(letter);
        }else if(keyCode==190){     //同一个键位上只能检测到键位+shift输出的字符值，切是加128之后的值，因此此处之取‘.’的ascii
            letter = keyToLetter.get(46);
            barcode.append(letter);
        }else if(keyCode==186){
//            System.out.println("冒号=="+keyCode);
            letter = keyToLetter.get(186-128);
            barcode.append(letter);
        }

//        System.out.println("接收到的扫描数据："+barcode.toString());
        if(GeneralSet.getInstance().checkCodeInfo(barcode.toString(),true)>0){
        	//当匹配成功后重新为缓冲分配内存，匹配串在checkCodeInfo中被加入生产者队列
            //完成一次扫描后后者超时都需要清空缓冲重新捕捉数据
        	barcode=new StringBuilder();
//        	BarcodeBuffer.product(barcode.toString());
        }
        if (keyCode == 13) {
            //条形码扫描器在很短的时间内输入了至少 barcodeMinLength 个字符以上信息，并且以“回车”作为结束字符
            //进入这个方法表示是“回车”
            //那么判断回车之前输入的字符数，至少 barcodeMinLength 个字符
            //并且一次扫描要在 maxScanTime 毫秒内完成
            if(barcode.length() >= barcodeMinLength && cost < maxScanTime){
                cost=System.currentTimeMillis()-start;
                System.out.println("耗时："+cost);
                System.out.println(barcode.toString());
                ///System.out.println("================================");
                //将数据加入缓存阻塞队列
                //BarcodeBuffer.product(barcode.toString());
            }
            //清空原来的缓冲区
            barcode=new StringBuilder();
        }
    }
}
