package com.qianfujiaoyu.Utils;

/**
 * Created by Administrator on 2016/12/17.
 */
public class NumUtils {

    public static String getNumStr(String num) {
        if (num == null || num.equals("")) {
            return "0";
        }
        double n = Double.valueOf(num);
        if (n < 10000) {
            return num;
        } else {
            if(n>=100000000){
                double d=Double.valueOf(num)/100000000;
                return String .format("%.2f",d)+"亿";
            }else{
                double d = Double.valueOf(num) / 10000;
                return String.format("%.2f", d)+"万";
            }

        }



    }

    public  static String getNumPrice(String s){
        if(s==null||s.equals("")){
            return "";
        }
        int i=Integer.valueOf(s);
        if(i>=1000&&i<10000){
            return i/1000+"千";
        }else if(i>=10000&&i<100000000){
            return i/10000+"万";
        }else  if(i>=100000000){
            return i/100000000+"亿"+(i-100000000)/10000000+"千万";
        }
        return s;
    }
}
