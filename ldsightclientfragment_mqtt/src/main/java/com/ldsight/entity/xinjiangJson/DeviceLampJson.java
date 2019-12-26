package com.ldsight.entity.xinjiangJson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ldgd on 2019/9/24.
 * 功能：
 * 说明：
 */

public class DeviceLampJson {


    private int errno;
    private String errmsg;
    private DataBeanX data;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public static class DataBeanX {


        private double count;
        private double totalPages;
        private double pageSize;
        private double currentPage;
        private List<DeviceLamp> data;

        public double getCount() {
            return count;
        }

        public void setCount(double count) {
            this.count = count;
        }

        public double getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(double totalPages) {
            this.totalPages = totalPages;
        }

        public double getPageSize() {
            return pageSize;
        }

        public void setPageSize(double pageSize) {
            this.pageSize = pageSize;
        }

        public double getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(double currentPage) {
            this.currentPage = currentPage;
        }

        public List<DeviceLamp> getData() {
            return data;
        }

        public void setData(List<DeviceLamp> data) {
            this.data = data;
        }

        public static class DeviceLamp implements Serializable {
            /**
             * UUID : 2016C0312000001200001192
             * LAT : 22.635293
             * LNG : 114.003836
             * NAME : 测试灯1
             * TYPE : 2
             * PROJECT : 中科洛丁展示项目/深圳展厅
             * SUBGROUP :
             * _id : 5
             * FUUID : 5,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99
             * smsphone : 18033440703
             * subgroups : ["1","2"]
             * admin : ld
             * FirDimming : 1299254
             * SecDimming : 1299254
             * Temp : 1299254
             * Illu : 1299254
             * _modified : 2019-09-18T08:40:46.397Z
             * STATE : 1
             * LampDiameter : 21cm
             * Power_Manufacturer : 英飞特
             * Lamp_RatedCurrent : 1025mA
             * Lamp_Ratedvoltage : 125V
             * lampType : LED
             * Lamp_Manufacturer : 洛丁光电
             * Lamp_Num : 1
             * PoleProductionDate : 2015/2/12
             * Pole_height : 2.8m
             * Rated_power : 120
             * Subcommunicate_mode : 以太网有线
             * Current : 0
             * Power : 0
             * Leak_curt : 0
             * Gprs_csq : 31
             * Power_Factor : 0
             * Energy : 0
             * Voltage : 0
             * RESET_COUNT : 3
             * Version : 1.0.0|0-0-0
             * Warning_state : 2
             * Alarm_Light_Mode : OFF
             */

            private String UUID;
            private String LAT;
            private String LNG;
            private String NAME;
            private double TYPE;
            private String PROJECT;
            private String SUBGROUP;
            private double _id;
            private String FUUID;
            private String smsphone;
            private String subgroups;
            private String admin;
            private double FirDimming;
            private double SecDimming;
            private double Temp;
            private double Illu;
            private String _modified;
            private double STATE;
            private String LampDiameter;
            private String Power_Manufacturer;
            private String Lamp_RatedCurrent;
            private String Lamp_Ratedvoltage;
            private String lampType;
            private String Lamp_Manufacturer;
            private String Lamp_Num;
            private String PoleProductionDate;
            private String Pole_height;
            private String Rated_power;
            private String Subcommunicate_mode;
            private double Current;
            private double Power;
            private double Leak_curt;
            private double Gprs_csq;
            private double Power_Factor;
            private double Energy;
            private double Voltage;
            private double RESET_COUNT;
            private String Version;
            private double Warning_state;
            private String Alarm_Light_Mode;
            private String info;
            private Object config;
            private Object report_config;
            private String A_v;
            private String B_v;
            private String C_v;
            private String A_c;
            private String B_c;
            private String C_c;
            private long ts_mqtt;
            private String Frequency;
            private String Tot_p_fac;
            private String Tot_view_p;
            private String Tot_act_deg;
            private String Tot_react_deg;
            private String A_act_p;
            private String B_act_p;
            private String C_act_p;
            private String Tot_act_p;

            @Override
            public String toString() {
                return "DeviceLamp{" +
                        "UUID='" + UUID + '\'' +
                        ", LAT='" + LAT + '\'' +
                        ", LNG='" + LNG + '\'' +
                        ", NAME='" + NAME + '\'' +
                        ", TYPE=" + TYPE +
                        ", PROJECT='" + PROJECT + '\'' +
                        ", SUBGROUP='" + SUBGROUP + '\'' +
                        ", _id=" + _id +
                        ", FUUID='" + FUUID + '\'' +
                        ", smsphone='" + smsphone + '\'' +
                        ", subgroups='" + subgroups + '\'' +
                        ", admin='" + admin + '\'' +
                        ", FirDimming=" + FirDimming +
                        ", SecDimming=" + SecDimming +
                        ", Temp=" + Temp +
                        ", Illu=" + Illu +
                        ", _modified='" + _modified + '\'' +
                        ", STATE=" + STATE +
                        ", LampDiameter='" + LampDiameter + '\'' +
                        ", Power_Manufacturer='" + Power_Manufacturer + '\'' +
                        ", Lamp_RatedCurrent='" + Lamp_RatedCurrent + '\'' +
                        ", Lamp_Ratedvoltage='" + Lamp_Ratedvoltage + '\'' +
                        ", lampType='" + lampType + '\'' +
                        ", Lamp_Manufacturer='" + Lamp_Manufacturer + '\'' +
                        ", Lamp_Num='" + Lamp_Num + '\'' +
                        ", PoleProductionDate='" + PoleProductionDate + '\'' +
                        ", Pole_height='" + Pole_height + '\'' +
                        ", Rated_power='" + Rated_power + '\'' +
                        ", Subcommunicate_mode='" + Subcommunicate_mode + '\'' +
                        ", Current=" + Current +
                        ", Power=" + Power +
                        ", Leak_curt=" + Leak_curt +
                        ", Gprs_csq=" + Gprs_csq +
                        ", Power_Factor=" + Power_Factor +
                        ", Energy=" + Energy +
                        ", Voltage=" + Voltage +
                        ", RESET_COUNT=" + RESET_COUNT +
                        ", Version='" + Version + '\'' +
                        ", Warning_state=" + Warning_state +
                        ", Alarm_Light_Mode='" + Alarm_Light_Mode + '\'' +
                        '}';
            }

            public String getUUID() {
                return UUID;
            }

            public void setUUID(String UUID) {
                this.UUID = UUID;
            }

            public String getLAT() {
                return LAT;
            }

            public void setLAT(String LAT) {
                this.LAT = LAT;
            }

            public String getLNG() {
                return LNG;
            }

            public void setLNG(String LNG) {
                this.LNG = LNG;
            }

            public String getNAME() {
                return NAME;
            }

            public void setNAME(String NAME) {
                this.NAME = NAME;
            }

            public double getTYPE() {
                return TYPE;
            }

            public void setTYPE(double TYPE) {
                this.TYPE = TYPE;
            }

            public String getPROJECT() {
                return PROJECT;
            }

            public void setPROJECT(String PROJECT) {
                this.PROJECT = PROJECT;
            }

            public String getSUBGROUP() {
                return SUBGROUP;
            }

            public void setSUBGROUP(String SUBGROUP) {
                this.SUBGROUP = SUBGROUP;
            }

            public double get_id() {
                return _id;
            }

            public void set_id(double _id) {
                this._id = _id;
            }

            public String getFUUID() {
                return FUUID;
            }

            public void setFUUID(String FUUID) {
                this.FUUID = FUUID;
            }

            public String getSmsphone() {
                return smsphone;
            }

            public void setSmsphone(String smsphone) {
                this.smsphone = smsphone;
            }

            public String getSubgroups() {
                return subgroups;
            }

            public void setSubgroups(String subgroups) {
                this.subgroups = subgroups;
            }

            public String getAdmin() {
                return admin;
            }

            public void setAdmin(String admin) {
                this.admin = admin;
            }

            public double getFirDimming() {
                return FirDimming;
            }

            public void setFirDimming(double FirDimming) {
                this.FirDimming = FirDimming;
            }

            public double getSecDimming() {
                return SecDimming;
            }

            public void setSecDimming(double SecDimming) {
                this.SecDimming = SecDimming;
            }

            public double getTemp() {
                return Temp;
            }

            public void setTemp(double Temp) {
                this.Temp = Temp;
            }

            public double getIllu() {
                return Illu;
            }

            public void setIllu(double Illu) {
                this.Illu = Illu;
            }

            public String get_modified() {
                return _modified;
            }

            public void set_modified(String _modified) {
                this._modified = _modified;
            }

            public double getSTATE() {
                return STATE;
            }

            public void setSTATE(double STATE) {
                this.STATE = STATE;
            }

            public String getLampDiameter() {
                return LampDiameter;
            }

            public void setLampDiameter(String LampDiameter) {
                this.LampDiameter = LampDiameter;
            }

            public String getPower_Manufacturer() {
                return Power_Manufacturer;
            }

            public void setPower_Manufacturer(String Power_Manufacturer) {
                this.Power_Manufacturer = Power_Manufacturer;
            }

            public String getLamp_RatedCurrent() {
                return Lamp_RatedCurrent;
            }

            public void setLamp_RatedCurrent(String Lamp_RatedCurrent) {
                this.Lamp_RatedCurrent = Lamp_RatedCurrent;
            }

            public String getLamp_Ratedvoltage() {
                return Lamp_Ratedvoltage;
            }

            public void setLamp_Ratedvoltage(String Lamp_Ratedvoltage) {
                this.Lamp_Ratedvoltage = Lamp_Ratedvoltage;
            }

            public String getLampType() {
                return lampType;
            }

            public void setLampType(String lampType) {
                this.lampType = lampType;
            }

            public String getLamp_Manufacturer() {
                return Lamp_Manufacturer;
            }

            public void setLamp_Manufacturer(String Lamp_Manufacturer) {
                this.Lamp_Manufacturer = Lamp_Manufacturer;
            }

            public String getLamp_Num() {
                return Lamp_Num;
            }

            public void setLamp_Num(String Lamp_Num) {
                this.Lamp_Num = Lamp_Num;
            }

            public String getPoleProductionDate() {
                return PoleProductionDate;
            }

            public void setPoleProductionDate(String PoleProductionDate) {
                this.PoleProductionDate = PoleProductionDate;
            }

            public String getPole_height() {
                return Pole_height;
            }

            public void setPole_height(String Pole_height) {
                this.Pole_height = Pole_height;
            }

            public String getRated_power() {
                return Rated_power;
            }

            public void setRated_power(String Rated_power) {
                this.Rated_power = Rated_power;
            }

            public String getSubcommunicate_mode() {
                return Subcommunicate_mode;
            }

            public void setSubcommunicate_mode(String Subcommunicate_mode) {
                this.Subcommunicate_mode = Subcommunicate_mode;
            }

            public double getCurrent() {
                return Current;
            }

            public void setCurrent(double Current) {
                this.Current = Current;
            }

            public double getPower() {
                return Power;
            }

            public void setPower(double Power) {
                this.Power = Power;
            }

            public double getLeak_curt() {
                return Leak_curt;
            }

            public void setLeak_curt(double Leak_curt) {
                this.Leak_curt = Leak_curt;
            }

            public double getGprs_csq() {
                return Gprs_csq;
            }

            public void setGprs_csq(double Gprs_csq) {
                this.Gprs_csq = Gprs_csq;
            }

            public double getPower_Factor() {
                return Power_Factor;
            }

            public void setPower_Factor(double Power_Factor) {
                this.Power_Factor = Power_Factor;
            }

            public double getEnergy() {
                return Energy;
            }

            public void setEnergy(double Energy) {
                this.Energy = Energy;
            }

            public double getVoltage() {
                return Voltage;
            }

            public void setVoltage(double Voltage) {
                this.Voltage = Voltage;
            }

            public double getRESET_COUNT() {
                return RESET_COUNT;
            }

            public void setRESET_COUNT(double RESET_COUNT) {
                this.RESET_COUNT = RESET_COUNT;
            }

            public String getVersion() {
                return Version;
            }

            public void setVersion(String Version) {
                this.Version = Version;
            }

            public double getWarning_state() {
                return Warning_state;
            }

            public void setWarning_state(double Warning_state) {
                this.Warning_state = Warning_state;
            }

            public String getAlarm_Light_Mode() {
                return Alarm_Light_Mode;
            }

            public void setAlarm_Light_Mode(String Alarm_Light_Mode) {
                this.Alarm_Light_Mode = Alarm_Light_Mode;
            }

            public String getInfo() {
                return info;
            }

            public void setInfo(String info) {
                this.info = info;
            }

            public Object getConfig() {
                return config;
            }

            public void setConfig(Object config) {
                this.config = config;
            }

            public Object getReport_config() {
                return report_config;
            }

            public void setReport_config(Object report_config) {
                this.report_config = report_config;
            }

            public String getA_v() {
                return A_v;
            }

            public void setA_v(String a_v) {
                A_v = a_v;
            }

            public String getB_v() {
                return B_v;
            }

            public void setB_v(String b_v) {
                B_v = b_v;
            }

            public String getC_v() {
                return C_v;
            }

            public void setC_v(String c_v) {
                C_v = c_v;
            }

            public String getA_c() {
                return A_c;
            }

            public void setA_c(String a_c) {
                A_c = a_c;
            }

            public String getB_c() {
                return B_c;
            }

            public void setB_c(String b_c) {
                B_c = b_c;
            }

            public String getC_c() {
                return C_c;
            }

            public void setC_c(String c_c) {
                C_c = c_c;
            }

            public long getTs_mqtt() {
                return ts_mqtt;
            }

            public void setTs_mqtt(long ts_mqtt) {
                this.ts_mqtt = ts_mqtt;
            }

            public String getFrequency() {
                return Frequency;
            }

            public void setFrequency(String frequency) {
                Frequency = frequency;
            }

            public String getTot_p_fac() {
                return Tot_p_fac;
            }

            public void setTot_p_fac(String tot_p_fac) {
                Tot_p_fac = tot_p_fac;
            }

            public String getTot_view_p() {
                return Tot_view_p;
            }

            public void setTot_view_p(String tot_view_p) {
                Tot_view_p = tot_view_p;
            }

            public String getTot_act_deg() {
                return Tot_act_deg;
            }

            public void setTot_act_deg(String tot_act_deg) {
                Tot_act_deg = tot_act_deg;
            }

            public String getTot_react_deg() {
                return Tot_react_deg;
            }

            public void setTot_react_deg(String tot_react_deg) {
                Tot_react_deg = tot_react_deg;
            }

            public String getA_act_p() {
                return A_act_p;
            }

            public void setA_act_p(String a_act_p) {
                A_act_p = a_act_p;
            }

            public String getB_act_p() {
                return B_act_p;
            }

            public void setB_act_p(String b_act_p) {
                B_act_p = b_act_p;
            }

            public String getC_act_p() {
                return C_act_p;
            }

            public void setC_act_p(String c_act_p) {
                C_act_p = c_act_p;
            }

            public String getTot_act_p() {
                return Tot_act_p;
            }

            public void setTot_act_p(String tot_act_p) {
                Tot_act_p = tot_act_p;
            }
        }
    }
}
