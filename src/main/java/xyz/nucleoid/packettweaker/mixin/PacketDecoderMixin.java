package xyz.nucleoid.packettweaker.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.packettweaker.PlayerProvidingPacketListener;
import xyz.nucleoid.packettweaker.impl.ConnectionHolder;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

@Mixin(DecoderHandler.class)
public class PacketDecoderMixin implements ConnectionHolder {
    @Unique
    private ClientConnection connection;

    @Override
    public void setConnection(ClientConnection connection) {
        this.connection = connection;
    }

    @Inject(method = "decode", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkState;getPacketHandler(Lnet/minecraft/network/NetworkSide;ILnet/minecraft/network/PacketByteBuf;)Lnet/minecraft/network/packet/Packet;", shift = At.Shift.BEFORE))
    private void packetTweaker_setPacketContext(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list, CallbackInfo ci) {
        if (this.connection != null) {
            PacketContext.setContext(PlayerProvidingPacketListener.getPlayer(this.connection.getPacketListener()));
        }
    }

    @Inject(method = "decode", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkState;getPacketHandler(Lnet/minecraft/network/NetworkSide;ILnet/minecraft/network/PacketByteBuf;)Lnet/minecraft/network/packet/Packet;", shift = At.Shift.AFTER))
    private void packetTweaker_clearPacketContext(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list, CallbackInfo ci) {
        PacketContext.clearContext();
    }
}
