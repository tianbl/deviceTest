package cn.com.eastsoft.action.plMessage;

/**
 * Created by tianbaolei on 15-11-9.
 */
public class ReqMessage {
    private String type;
    private int contentlen;
    private String content;

    public ReqMessage(){

    }

    public byte[] getMessage(){
        byte[] bytes = new byte[3+contentlen];
        int dectype = Integer.parseInt(this.type,16);
        bytes[0] = (byte) dectype;
        bytes[1] = (byte) (contentlen%256);
        bytes[2] = (byte) (contentlen/256);
        if("0E".equals(type)){
            byte[] bt = hexStringToBytes(content);
            for(int i=3;i<bytes.length;i++){
                bytes[i] = bt[i-3];
            }
        }else if("0F".equals(type)){
            byte[] bt = content.getBytes();
            for(int i=3;i<bytes.length;i++){
                bytes[i] = bt[i-3];
            }
        }else if("1B".equals(type)){
            byte[] bt = content.getBytes();
            for(int i=3;i<bytes.length;i++){
                bytes[i] = bt[i-3];
            }
        }else if("1E".equals(type)){
            byte[] bt = content.getBytes();
            for(int i=3;i<bytes.length;i++){
                bytes[i] = bt[i-3];
            }
        }
        return bytes;
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;

        if(content!=null&&type!=null&&"0E".equals(type)){
            contentlen = hexStringToBytes(content).length;
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        if(type!=null&&"0E".equals(type)){
            contentlen = hexStringToBytes(content).length;
        }else {
            contentlen = content.length();
        }
    }
}
