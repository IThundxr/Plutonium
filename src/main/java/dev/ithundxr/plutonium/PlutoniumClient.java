package dev.ithundxr.plutonium;

import dev.ithundxr.plutonium.mixin.fsc.AccessorClientHandshakePacketListenerImpl;
import dev.ithundxr.plutonium.mixinsupport.FSCConnection;
import dev.ithundxr.plutonium.registry.PlutoniumCommandClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import java.util.concurrent.CompletableFuture;

public class PlutoniumClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> PlutoniumCommandClient.register(dispatcher));

        ClientLoginNetworking.registerGlobalReceiver(Plutonium.FULL_STREAM_COMPRESSION, (client, handler, buf, listenerAdder) -> {
            if (Plutonium.CAN_USE_ZSTD) {
                ((FSCConnection) ((AccessorClientHandshakePacketListenerImpl) handler).plutonium$getConnection()).plutonium$enableFullStreamCompression();
                return CompletableFuture.completedFuture(PacketByteBufs.empty());
            } else {
                return CompletableFuture.completedFuture(null);
            }
        });
    }
}
