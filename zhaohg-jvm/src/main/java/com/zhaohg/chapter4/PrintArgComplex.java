package com.zhaohg.chapter4;

import com.sun.btrace.BTraceUtils;
import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.Kind;
import com.sun.btrace.annotations.Location;
import com.sun.btrace.annotations.OnMethod;
import com.sun.btrace.annotations.ProbeClassName;
import com.sun.btrace.annotations.ProbeMethodName;
import com.zhaohg.chapter2.User;

import java.lang.reflect.Field;

@BTrace
public class PrintArgComplex {

    /**
     * 拦截对象
     * @param pcn
     * @param pmn
     * @param user
     */
    @OnMethod(
            clazz = "com.zhaohg.chapter4.Ch4Controller",
            method = "arg2",
            location = @Location(Kind.ENTRY)
    )
    public static void anyRead(@ProbeClassName String pcn, @ProbeMethodName String pmn, User user) {
        //print all fields
        BTraceUtils.printFields(user);
        //print one field
        Field filed2 = BTraceUtils.field("com.zhaohg.chapter2.User", "name");
        BTraceUtils.println(BTraceUtils.get(filed2, user));
        BTraceUtils.println(pcn + "," + pmn);
        BTraceUtils.println();
    }
}
