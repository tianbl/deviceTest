package cn.com.eastsoft.action.plMessage;

/**
 * Created by baolei on 2015/11/12.
 */
public class ExtenderRes {
    private String ip;
    private String mac;
    private String ssid;

    public ExtenderRes(byte[] bytes) {

        int count = 6;
        int iplen = Integer.parseInt(Integer.toHexString(bytes[count]), 16);
        StringBuffer sb = new StringBuffer();
        for (int ii = 0; ii < iplen; ii++) {
            String str = Integer.toHexString(bytes[++count] & 0xff);
            sb.append(Integer.parseInt(str,16)+"");
            if(ii<3){
                sb.append('.');
            }
        }
        ip = sb.toString();

        int maclen = Integer.parseInt(Integer.toHexString(bytes[++count]&0xff), 16);
        byte[] data = new byte[maclen];
        for (int ii = 0; ii < maclen; ii++) {
            data[ii] = bytes[++count];
        }
        mac = ResMessage.parseByte2HexStr(data,0,data.length);

        int ssidlen = Integer.parseInt(Integer.toHexString(bytes[++count]&0xff), 16);
        data = new byte[ssidlen];
        for(int i=0;i<ssidlen;i++){
            data[i] = bytes[++count];
        }
        ssid = asciiToStr(data);
    }

    public String asciiToStr(byte[] bytes){
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<bytes.length;i++){
            sb.append((char)bytes[i]);
        }
        return sb.toString();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
}
