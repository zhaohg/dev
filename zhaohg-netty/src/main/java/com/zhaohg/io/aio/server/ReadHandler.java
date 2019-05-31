package com.zhaohg.io.aio.server;

import com.zhaohg.io.Calculator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {
    //
    private AsynchronousSocketChannel channel;

    public ReadHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    //
    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        //flip
        attachment.flip();
        //
        byte[] message = new byte[attachment.remaining()];
        attachment.get(message);
        try {
            String expression = new String(message, "UTF-8");
            System.out.println("dddd: " + expression);
            String calrResult = null;
            try {
                calrResult = Calculator.Instance.cal(expression).toString();
            } catch (Exception e) {
                calrResult = "fff" + e.getMessage();
            }
            //
            doWrite(calrResult);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //
    private void doWrite(String result) {
        byte[] bytes = result.getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        //
        channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                //
                if (buffer.hasRemaining())
                    channel.write(buffer, buffer, this);
                else {
                    //
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    //
                    channel.read(readBuffer, readBuffer, new ReadHandler(channel));
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    channel.close();
                } catch (IOException e) {
                }
            }
        });
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}