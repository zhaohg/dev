package com.zhaohg.rpc;

/**
 * Created by zhaohg on 2017/8/14.
 */
public class RPCMain {

    public static void main(String[] args) throws Exception {
        RPCClient rpcClient = new RPCClient();
        System.out.println(" [x] Requesting fib(abc)");
        String response = rpcClient.call("send_post");
        System.out.println(" [.] Got '" + response + "'");
        rpcClient.close();
    }
}