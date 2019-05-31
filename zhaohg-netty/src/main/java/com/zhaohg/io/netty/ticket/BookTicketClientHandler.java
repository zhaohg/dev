package com.zhaohg.io.netty.ticket;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Calendar;

/**
 * 客户端处理器
 * <p>
 * reated by zhaohg on 2017-11-21.
 */
public class BookTicketClientHandler extends ChannelHandlerAdapter {
    private User user;

    public BookTicketClientHandler() {
        user = new User();
        user.setUserName("zhaohg");
        user.setPhone("150886*****");
        user.setEmail("373973619@qq.com");
        user.setUserId("610528198*********");
    }

    /**
     * 链路链接成功
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        // 链接成功后发送查询某车次余票的请求
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 2015);
        c.set(Calendar.MONTH, 1);
        c.set(Calendar.DATE, 2);
        c.set(Calendar.HOUR, 11);
        c.set(Calendar.MINUTE, 30);
        // G242查询余票
        BookRequestMsg requestMsg1 = new BookRequestMsg();
        requestMsg1.setCode(Code.CODE_SEARCH);
        requestMsg1.setStartTime(c.getTime());
        requestMsg1.setTrainNumber("G242");//设置查询车次
        requestMsg1.setUser(user);//设置当前登陆用户
        ctx.write(requestMsg1);
        // D1235查询余票
        BookRequestMsg requestMsg2 = new BookRequestMsg();
        requestMsg2.setCode(Code.CODE_SEARCH);
        requestMsg2.setStartTime(c.getTime());
        requestMsg2.setTrainNumber("D1235");//设置查询车次
        requestMsg2.setUser(user);
        ctx.write(requestMsg2);
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BookResponseMsg responseMsg = (BookResponseMsg) msg;
        switch (responseMsg.getCode()) {
            case Code.CODE_SEARCH://收到查询结果
                System.out.println("==========火车【" + responseMsg.getTrain().getNumber() + "】余票查询结果:【" + (responseMsg.getSuccess() ? "成功" : "失败") + "】=========");
                System.out.println(responseMsg.getMsg());
                //查询发现有余票的话 需要发送订票指令
                if (responseMsg.getTrain().getTicketCounts() > 0) {
                    //构造查询有余票的火车的订票指令
                    BookRequestMsg requestMsg = new BookRequestMsg();
                    requestMsg.setCode(Code.CODE_BOOK);
                    requestMsg.setUser(user);
                    requestMsg.setStartTime(responseMsg.getStartTime());
                    requestMsg.setTrainNumber(responseMsg.getTrain().getNumber());
                    ctx.writeAndFlush(requestMsg);
                } else {
                    System.out.println("火车【" + responseMsg.getTrain().getNumber() + "】没有余票，不能订票了！");
                }
                break;
            case Code.CODE_BOOK://收到订票结果
                System.out.println("==========火车【" + responseMsg.getTrain().getNumber() + "】订票结果:【" + (responseMsg.getSuccess() ? "成功" : "失败") + "】=========");
                System.out.println(responseMsg.getMsg());
                System.out.println("========车票详情========");
                Ticket ticket = responseMsg.getTicket();
                System.out.println("车票票号：【" + ticket.getNumber() + "】");
                System.out.println("火车车次：【" + ticket.getTrainNumber() + "】");
                System.out.println("火车车厢：【" + ticket.getCarriageNumber() + "】");
                System.out.println("车厢座位：【" + ticket.getSeatNumber() + "】");
                System.out.println("预定时间：【" + ticket.getBookTime() + "】");
                System.out.println("出发时间：【" + ticket.getStartTime() + "】");
                System.out.println("乘客信息：【" + ticket.getUser().getUserName() + "】");
                break;
            default:
                System.out.println("==========操作错误结果=========");
                System.out.println(responseMsg.getMsg());
                break;
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
