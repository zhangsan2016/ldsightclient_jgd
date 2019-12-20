package com.ldsight.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldgd on 2019/4/19.
 * 功能： uuid集合(备用)
 * 说明：
 */

public class UuidSet {

    public static List<String> UUID_SET = new ArrayList<String>();

    static {
        UUID_SET.add("1,0,0,0,0,0,0,0,0,0,108,100,115,104,111,119");
        UUID_SET.add("1,0,0,0,0,0,0,0,0,0,0,97,100,109,105,110");
        UUID_SET.add("1,0,0,0,0,0,0,0,0,0,0,0,0,0,122,106");
        UUID_SET.add("1,0,0,0,0,0,0,0,0,0,0,0,0,97,122,102");
    }

}
