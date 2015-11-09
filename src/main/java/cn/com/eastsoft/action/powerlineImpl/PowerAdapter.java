package cn.com.eastsoft.action.powerlineImpl;

import cn.com.eastsoft.action.PowerLine;
import cn.com.eastsoft.ui.MainJFrame;

/**
 * Created by tianbaolei on 15-11-9.
 */
public class PowerAdapter extends PowerLine{

    @Override
    public boolean info_set() {
        MainJFrame.showMssageln("电力线适配器信息设置");
        return false;
    }

    @Override
    public boolean wan_Lan_test() {
        MainJFrame.showMssageln("电力线适配器wan和lan口设置");
        return false;
    }

    @Override
    public boolean carrier_test() {
        MainJFrame.showMssageln("电力线适配器载波测试");
        return false;
    }
}
