package com.ldsight;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by ldgd on 2019/3/2.
 * 功能：
 * 说明：
 */

public class check {

    public static void main(String[] args) {

        String json = "{\"b\":true,\"msg\":null,\"data\":\"{\\\"ElectricityBoxStatus\\\":[{\\\"ID\\\":\\\"262\\\",\\\"UUID\\\":\\\"1,1,99,99,99,99,99,99,99,99,99,99,99,99,99,10\\\",\\\"TYPE\\\":\\\"1\\\",\\\"STATE\\\":\\\"2\\\",\\\"PRIVATEDIC\\\":\\\"False\\\",\\\"IPADDRESS\\\":\\\"218.204.252.21\\\",\\\"PORT\\\":\\\"61838\\\",\\\"PROTOCOL\\\":\\\"8\\\",\\\"STATENAME\\\":\\\"2G\\\",\\\"TYPENAME\\\":\\\"电箱\\\",\\\"Total_rated_power\\\":\\\"\\\",\\\"Version\\\":\\\"255-255-255\\\",\\\"FirDimming\\\":\\\"0\\\",\\\"SecDimming\\\":\\\"0\\\",\\\"FirIllu\\\":\\\"0\\\",\\\"SecIllu\\\":\\\"0\\\",\\\"Rel_State\\\":\\\"31\\\",\\\"Time\\\":\\\"19-3-2-2-11-26-7\\\",\\\"Gprs_csq\\\":\\\"31\\\",\\\"Illu\\\":\\\"11083\\\",\\\"Confirm\\\":\\\"\\\",\\\"A_v\\\":\\\"242.19\\\",\\\"B_v\\\":\\\"242.16\\\",\\\"C_v\\\":\\\"241.75\\\",\\\"A_c\\\":\\\"0.00\\\",\\\"B_c\\\":\\\"0.30\\\",\\\"C_c\\\":\\\"0.00\\\",\\\"Frequency\\\":\\\"49.97\\\",\\\"Tot_p_fac\\\":\\\"0.79\\\",\\\"Tot_view_p\\\":\\\"1.39\\\",\\\"Tot_act_deg\\\":\\\"116706.90\\\",\\\"Tot_react_deg\\\":\\\"29187.10\\\",\\\"A_act_p\\\":\\\"0.00\\\",\\\"B_act_p\\\":\\\"73.80\\\",\\\"C_act_p\\\":\\\"0.00\\\",\\\"Tot_act_p\\\":\\\"72.00\\\",\\\"A_react_p\\\":\\\"0.00\\\",\\\"B_react_p\\\":\\\"42949636.00\\\",\\\"C_react_p\\\":\\\"0.00\\\",\\\"Tot_react_p\\\":\\\"42949616.00\\\",\\\"Dimming\\\":\\\"\\\",\\\"Temp\\\":\\\"\\\",\\\"Loop_num\\\":\\\"\\\",\\\"Fir_tt_Sec\\\":\\\"17:30\\\",\\\"Fir_tp_Sec\\\":\\\"80\\\",\\\"Sec_tt_Sec\\\":\\\"20:0\\\",\\\"Sec_tp_Sec\\\":\\\"100\\\",\\\"Thir_tt_Sec\\\":\\\"23:0\\\",\\\"Thir_tp_Sec\\\":\\\"60\\\",\\\"Four_tt_Sec\\\":\\\"1:0\\\",\\\"Four_tp_Sec\\\":\\\"30\\\",\\\"Fif_tt_Sec\\\":\\\"5:0\\\",\\\"Fif_tp_Sec\\\":\\\"80\\\",\\\"Six_tt_Sec\\\":\\\"6:0\\\",\\\"Six_tp_Sec\\\":\\\"0\\\",\\\"Fir_tt_Fir\\\":\\\"17:30\\\",\\\"Fir_tp_Fir\\\":\\\"80\\\",\\\"Sec_tt_Fir\\\":\\\"20:0\\\",\\\"Sec_tp_Fir\\\":\\\"100\\\",\\\"Thir_tt_Fir\\\":\\\"23:0\\\",\\\"Thir_tp_Fir\\\":\\\"60\\\",\\\"Four_tt_Fir\\\":\\\"1:0\\\",\\\"Four_tp_Fir\\\":\\\"30\\\",\\\"Fif_tt_Fir\\\":\\\"5:0\\\",\\\"Fif_tp_Fir\\\":\\\"80\\\",\\\"Six_tt_Fir\\\":\\\"6:0\\\",\\\"Six_tp_Fir\\\":\\\"0\\\",\\\"cap_pic\\\":\\\"\\\",\\\"elec_par_sw\\\":\\\"\\\",\\\"Fir_tt_Sec_Group3\\\":\\\"\\\",\\\"Fir_tp_Sec_Group3\\\":\\\"\\\",\\\"Sec_tt_Sec_Group3\\\":\\\"\\\",\\\"Sec_tp_Sec_Group3\\\":\\\"\\\",\\\"Thir_tt_Sec_Group3\\\":\\\"\\\",\\\"Thir_tp_Sec_Group3\\\":\\\"\\\",\\\"Four_tt_Sec_Group3\\\":\\\"\\\",\\\"Four_tp_Sec_Group3\\\":\\\"\\\",\\\"Fif_tt_Sec_Group3\\\":\\\"\\\",\\\"Fif_tp_Sec_Group3\\\":\\\"\\\",\\\"Six_tt_Sec_Group3\\\":\\\"\\\",\\\"Six_tp_Sec_Group3\\\":\\\"\\\",\\\"Fir_tt_Fir_Group0\\\":\\\"\\\",\\\"Fir_tp_Fir_Group0\\\":\\\"\\\",\\\"Sec_tt_Fir_Group0\\\":\\\"\\\",\\\"Sec_tp_Fir_Group0\\\":\\\"\\\",\\\"Thir_tt_Fir_Group0\\\":\\\"\\\",\\\"Thir_tp_Fir_Group0\\\":\\\"\\\",\\\"Four_tt_Fir_Group0\\\":\\\"\\\",\\\"Four_tp_Fir_Group0\\\":\\\"\\\",\\\"Fif_tt_Fir_Group0\\\":\\\"\\\",\\\"Fif_tp_Fir_Group0\\\":\\\"\\\",\\\"Six_tt_Fir_Group0\\\":\\\"\\\",\\\"Six_tp_Fir_Group0\\\":\\\"\\\",\\\"Fir_tt_Sec_Group0\\\":\\\"\\\",\\\"Fir_tp_Sec_Group0\\\":\\\"\\\",\\\"Sec_tt_Sec_Group0\\\":\\\"\\\",\\\"Sec_tp_Sec_Group0\\\":\\\"\\\",\\\"Thir_tt_Sec_Group0\\\":\\\"\\\",\\\"Thir_tp_Sec_Group0\\\":\\\"\\\",\\\"Four_tt_Sec_Group0\\\":\\\"\\\",\\\"Four_tp_Sec_Group0\\\":\\\"\\\",\\\"Fif_tt_Sec_Group0\\\":\\\"\\\",\\\"Fif_tp_Sec_Group0\\\":\\\"\\\",\\\"Six_tt_Sec_Group0\\\":\\\"\\\",\\\"Six_tp_Sec_Group0\\\":\\\"\\\",\\\"Fir_tt_Fir_Group1\\\":\\\"\\\",\\\"Fir_tp_Fir_Group1\\\":\\\"\\\",\\\"Sec_tt_Fir_Group1\\\":\\\"\\\",\\\"Sec_tp_Fir_Group1\\\":\\\"\\\",\\\"Thir_tt_Fir_Group1\\\":\\\"\\\",\\\"Thir_tp_Fir_Group1\\\":\\\"\\\",\\\"Four_tt_Fir_Group1\\\":\\\"\\\",\\\"Four_tp_Fir_Group1\\\":\\\"\\\",\\\"Fif_tt_Fir_Group1\\\":\\\"\\\",\\\"Fif_tp_Fir_Group1\\\":\\\"\\\",\\\"Six_tt_Fir_Group1\\\":\\\"\\\",\\\"Six_tp_Fir_Group1\\\":\\\"\\\",\\\"Fir_tt_Sec_Group1\\\":\\\"\\\",\\\"Fir_tp_Sec_Group1\\\":\\\"\\\",\\\"Sec_tt_Sec_Group1\\\":\\\"\\\",\\\"Sec_tp_Sec_Group1\\\":\\\"\\\",\\\"Thir_tt_Sec_Group1\\\":\\\"\\\",\\\"Thir_tp_Sec_Group1\\\":\\\"\\\",\\\"Four_tt_Sec_Group1\\\":\\\"\\\",\\\"Four_tp_Sec_Group1\\\":\\\"\\\",\\\"Fif_tt_Sec_Group1\\\":\\\"\\\",\\\"Fif_tp_Sec_Group1\\\":\\\"\\\",\\\"Six_tt_Sec_Group1\\\":\\\"\\\",\\\"Six_tp_Sec_Group1\\\":\\\"\\\",\\\"Fir_tt_Fir_Group2\\\":\\\"\\\",\\\"Fir_tp_Fir_Group2\\\":\\\"\\\",\\\"Sec_tt_Fir_Group2\\\":\\\"\\\",\\\"Sec_tp_Fir_Group2\\\":\\\"\\\",\\\"Thir_tt_Fir_Group2\\\":\\\"\\\",\\\"Thir_tp_Fir_Group2\\\":\\\"\\\",\\\"Four_tt_Fir_Group2\\\":\\\"\\\",\\\"Four_tp_Fir_Group2\\\":\\\"\\\",\\\"Fif_tt_Fir_Group2\\\":\\\"\\\",\\\"Fif_tp_Fir_Group2\\\":\\\"\\\",\\\"Six_tt_Fir_Group2\\\":\\\"\\\",\\\"Six_tp_Fir_Group2\\\":\\\"\\\",\\\"Fir_tt_Sec_Group2\\\":\\\"\\\",\\\"Fir_tp_Sec_Group2\\\":\\\"\\\",\\\"Sec_tt_Sec_Group2\\\":\\\"\\\",\\\"Sec_tp_Sec_Group2\\\":\\\"\\\",\\\"Thir_tt_Sec_Group2\\\":\\\"\\\",\\\"Thir_tp_Sec_Group2\\\":\\\"\\\",\\\"Four_tt_Sec_Group2\\\":\\\"\\\",\\\"Four_tp_Sec_Group2\\\":\\\"\\\",\\\"Fif_tt_Sec_Group2\\\":\\\"\\\",\\\"Fif_tp_Sec_Group2\\\":\\\"\\\",\\\"Six_tt_Sec_Group2\\\":\\\"\\\",\\\"Six_tp_Sec_Group2\\\":\\\"\\\",\\\"Fir_tt_Fir_Group3\\\":\\\"\\\",\\\"Fir_tp_Fir_Group3\\\":\\\"\\\",\\\"Sec_tt_Fir_Group3\\\":\\\"\\\",\\\"Sec_tp_Fir_Group3\\\":\\\"\\\",\\\"";

        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();

       /* JsonObject data = jsonObject.getAsJsonObject("data");
        System.out.println("data = " + data.toString());*/

/*   String unescapeStr =   StringEscapeUtils.unescapeJava(json);
        System.out.println("jsonObject = " + unescapeStr);*/



    }
}
