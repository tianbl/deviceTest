package cn.com.eastsoft.action.plMessage;

/**
 * Created by tianbaolei on 15-11-9.
 */
public class ResMessage {
    private String type;
    private Integer length;
    private String MAC;
    private String SN;
    private String D_KEY;
    private String DAK;
    public String queryRes;

    public ResMessage(byte[] bytes){
        type = parseByte2HexStr(bytes,0,1);
        byte[] lenbyte = new byte[2];
        lenbyte[0] = bytes[2];
        lenbyte[1] = bytes[1];
        length = Integer.parseInt(parseByte2HexStr(lenbyte,0,2),16);


        if("03".equals(type)){      //get mac
            MAC = parseByte2HexStr(bytes,6,6);
            queryRes = MAC;
        }else if("11".equals(type)){    //get sn
            SN = asciiToStr(bytes,3,length);
            queryRes = SN;
        }else if("1D".equals(type)){    //get d_key
            D_KEY = asciiToStr(bytes, 3, length);
            queryRes = D_KEY;
        }else if("20".equals(type)){    //get dak
            DAK = asciiToStr(bytes, 3, length);
            queryRes = DAK;
        }
    }

    /**
     *
     */
    public String asciiToStr(byte[] bytes,int offset,int len){
        StringBuffer sb = new StringBuffer();
        for(int i=offset;i<offset+len;i++){
            sb.append((char)bytes[i]);
        }
        return sb.toString();
    }

    /**
     * 将二进制转换成16进制,限制条件需要二进制的16进制数据
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte[] buf,int offset,int len) {
        StringBuffer sb = new StringBuffer();
        for (int i = offset; i < offset+len; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getD_KEY() {
        return D_KEY;
    }

    public void setD_KEY(String d_KEY) {
        D_KEY = d_KEY;
    }

    public String getDAK() {
        return DAK;
    }

    public void setDAK(String DAK) {
        this.DAK = DAK;
    }
}
