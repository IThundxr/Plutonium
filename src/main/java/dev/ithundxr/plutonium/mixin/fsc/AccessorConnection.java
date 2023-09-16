package dev.ithundxr.plutonium.mixin.fsc;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Connection.class)
public interface AccessorConnection {
    @Invoker("sendPacket")
    void plutonium$sendImmediately(Packet<?> packet, PacketSendListener callbacks);
}