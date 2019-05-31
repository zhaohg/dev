package com.zhaohg;

/**
 * Created by zhaohg on 2017/2/21.
 */

import java.util.Scanner;

public class RenMingBi {
    String[] strHan     = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    String[] moneyCount = {"", "拾", "佰", "仟"};
    private boolean zero;

    RenMingBi() {
        zero = false;
    }

    public static void main(String[] args) {
        RenMingBi rmb = new RenMingBi();
        while (true) {
            System.out.println(rmb.transComb());
        }
    }

    private String[] divide(double digitalRmbValue) {
        // 处理整数
        long intPart = (long) (digitalRmbValue);
        // 处理小数，把小数转换成整数形式处理，并进行四舍五入，此时小数四舍五入之后为100的应该另外加一层处理
        long decimalPart = Math
                .round((double) Math.round((digitalRmbValue - intPart) * 10000) / 100);
        // System.out.println("整数部分为：" + intPart + "小数部分为：" + (digitalRmbValue - intPart));
        if (decimalPart == 100) {
            intPart += 1;
        }
        // System.out.println("四舍五入之后整数部分为：" + intPart + "小数部分为：" + decimalPart);

        String strIntPart = "";
        if (intPart == 0)
            strIntPart = "0";
        else {
            strIntPart = "" + intPart;
        }

        String strDecimalPart = "";
        if (decimalPart < 10 && decimalPart >= 0)
            strDecimalPart = "0" + decimalPart;
        else {
            strDecimalPart = "" + decimalPart;
        }
        // 处理输入为0的时候
        if (intPart == 0 && decimalPart == 0) {
            zero = true;
        }
        // System.out.println("准换为String后，整数部分为：" + strIntPart + "小数部分为：" + strDecimalPart);
        return new String[]{strIntPart, strDecimalPart};
    }

    // 用来转换小数部分
    private String transDecimal(String str) {
        if (str.equals("100") || str.equals("00")) {
            return "零角零分";
        } else {
            String strResult;
            char[] strRmb = new char[20];
            strRmb = str.toCharArray();
            if (strRmb[0] == '0' && strRmb[1] == '0') {
                strResult = "";
            } else {
                int dm = strRmb[0] - '0';
                int dn = strRmb[1] - '0';
                strResult = strHan[dm] + "角" + strHan[dn] + "分";
            }
            return strResult;
        }
    }

    // 用来转换整数部分
    private String transInt(String str) {
        char[] firstFourBitRmb = new char[20];
        firstFourBitRmb = str.toCharArray();
        int len = firstFourBitRmb.length;
        String strResult = "";
        for (int i = 0; i < len; i++) {
            /**
             * 这里开启之后会显示成通俗易懂的格式，例如1001，会转化为：壹仟零壹元，而不是：壹仟零佰零拾壹元， 但我希望得到的是后面这种，因为发票所需显示的就是后面这种
             **/

            // if (firstFourBitRmb[i] == '0' && i < len) {
            // boolean zeroFlag = false;
            // while (i < len && firstFourBitRmb[i] == '0') {
            // zeroFlag = true;
            // i++;
            // }
            // if (i != len && zeroFlag == true)
            // strResult += "零";
            // i--;
            // } else {
            // int m = firstFourBitRmb[i] - '0';
            // strResult += strHan[m] + moneyCount[len - i - 1];
            // }
            int m = firstFourBitRmb[i] - '0';
            strResult += strHan[m] + moneyCount[len - i - 1];
        }
        return strResult;
    }

    public String transComb() {
        String[] strTempRmb = new String[2];
        double strValue = 0D;
        Scanner sc = new Scanner(System.in);

        System.out.println("请输入具体金额，结果会精确到分:");
        strValue = sc.nextDouble();
        RenMingBi nr = new RenMingBi();
        strTempRmb = nr.divide(strValue);
        // 转换小数部分
        String result1 = nr.transDecimal(strTempRmb[1]);

        // 转换整数部分
        String result2 = "";
        int len = strTempRmb[0].length();
        if (len > 12)
            return "数据过大，无法进行转换，请重新输入！";
        else {
            if (len >= 9) {
                String strBit1 = strTempRmb[0].substring(0, len - 8);
                String strBit2 = strTempRmb[0].substring(len - 8, len - 4);
                String strBit3 = strTempRmb[0].substring(len - 4, len);
                String strResult1 = nr.transInt(strBit1) + "亿";
                String strResult2 = nr.transInt(strBit2) + "万";
                String strResult3 = nr.transInt(strBit3) + "元";
                result2 = strResult1 + strResult2 + strResult3;
            } else if (len >= 5) {
                String strBit1 = strTempRmb[0].substring(0, len - 4);
                String strBit2 = strTempRmb[0].substring(len - 4, len);
                String strResult1 = nr.transInt(strBit1) + "万";
                String strResult2 = nr.transInt(strBit2) + "元";
                result2 = strResult1 + strResult2;
            } else if (len >= 1) {
                if (nr.zero == true) {
                    result2 = "零元";
                } else
                    result2 = nr.transInt(strTempRmb[0]) + "元";
            }
            return result2 + result1;
        }
    }
}