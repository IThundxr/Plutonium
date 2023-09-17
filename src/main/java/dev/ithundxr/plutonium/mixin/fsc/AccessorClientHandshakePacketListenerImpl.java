package dev.ithundxr.plutonium.mixin.fsc;

import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientHandshakePacketListenerImpl.class)
public interface AccessorClientHandshakePacketListenerImpl {
    @Accessor("connection")
    Connection plutonium$getConnection();
}