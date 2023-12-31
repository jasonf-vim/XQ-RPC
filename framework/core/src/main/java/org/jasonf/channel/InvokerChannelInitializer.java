package org.jasonf.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.jasonf.channel.handler.*;

/**
 * @Author jasonf
 * @Date 2023/11/6
 * @Description
 */

public class InvokerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast(new LoggingHandler())
                .addLast(new Decoder())
                .addLast(new Encoder())
                .addLast(new CompressCodec())
                .addLast(new SerializeCodec())
                .addLast(new ReturnValueHandler());
    }
}
