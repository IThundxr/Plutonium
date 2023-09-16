package dev.ithundxr.plutonium.mixin.fsc;

import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerLoginPacketListenerImpl.class)
public interface AccessorServerLoginPacketListenerImpl {
    @Accessor("connection")
    Connection plutonium$getConnection();
}