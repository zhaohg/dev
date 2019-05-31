package com.zhaohg.hprose.util;

/**
 * Created by zhaohg on 2018/8/29.
 */
public class StatusCode {
    public enum LEVEL {
        SERVER {
            @Override
            public int getCode() {
                return 1;
            }
        },
        ;

        public abstract int getCode();
    }

    public enum MODEL {

        LOGIN {
            @Override
            public int getCode() {
                return 5;
            }

            @Override
            public String getMsg() {
                return "登陆";
            }
        },

        SIGNUP {
            @Override
            public int getCode() {
                return 6;
            }

            @Override
            public String getMsg() {
                return "注册";
            }
        },
        ;

        public abstract int getCode();

        public abstract String getMsg();
    }


    public static class Status {
        public int    code;
        public String msg;

        public Status(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

}
