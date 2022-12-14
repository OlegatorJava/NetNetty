package org.example.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.model.Command;
import org.example.model.Message;

import java.util.function.Consumer;

public class ClientHandler extends SimpleChannelInboundHandler<Message> {

    private Message message;
    private Consumer<Message> callback;
    private boolean authOK;

    public ClientHandler() {

    }

    public boolean isAuthOK() {
        return authOK;
    }

    public ClientHandler(Message message, Consumer<Message> callback) {
        this.message = message;
        this.callback = callback;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(message);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        callback.accept(msg);
        if(msg.getCommand() == Command.AUTH_OK){
            this.authOK = true;
        }else{
            this.authOK = false;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        this.authOK = false;
    }
}