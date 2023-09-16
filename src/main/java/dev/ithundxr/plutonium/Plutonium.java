package dev.ithundxr.plutonium;

import com.github.luben.zstd.util.Native;
import dev.ithundxr.plutonium.mixin.fsc.AccessorConnection;
import dev.ithundxr.plutonium.mixin.fsc.AccessorServerLoginPacketListenerImpl;
import dev.ithundxr.plutonium.mixinsupport.FSCConnection;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Plutonium implements ModInitializer {
    public static final String MODID = "plutonium";
    public static final String NAME = "Plutonium";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static final ResourceLocation FULL_STREAM_COMPRESSION = asResource("full_stream_compression");

    public record QueuedPacket(Connection conn, Packet<?> packet, PacketSendListener listener) {}

    private static final AtomicInteger nextQueue = new AtomicInteger();

    @SuppressWarnings("unchecked")
    public static final LinkedBlockingQueue<QueuedPacket>[] PACKET_QUEUES = new LinkedBlockingQueue[4];

    public static boolean CAN_USE_ZSTD = false;

    @Override
    public void onInitialize() {
        for (int i = 0; i < PACKET_QUEUES.length; i++) {
            LinkedBlockingQueue<QueuedPacket> q = new LinkedBlockingQueue<>();
            PACKET_QUEUES[i] = q;
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        QueuedPacket p = q.take();
                        ((AccessorConnection)p.conn()).plutonium$sendImmediately(p.packet(), p.listener());
                    } catch (Throwable t) {
                        LOGGER.error("Exception in packet thread", t);
                    }
                }
            }, "Fireblanket async packet send thread #"+(i+1));
            thread.setDaemon(true);
            thread.start();
        }

        try {
            Native.load();
            CAN_USE_ZSTD = true;
        } catch (UnsatisfiedLinkError e) {
            CAN_USE_ZSTD = false;
            LOGGER.warn("Could not load zstd, full-stream compression unavailable", e);
        }

        if (CAN_USE_ZSTD) {
            LOGGER.info("Enabling full-stream compression");
            ServerLoginConnectionEvents.QUERY_START.addPhaseOrdering(new ResourceLocation("plutonium:pre"), Event.DEFAULT_PHASE);
            ServerLoginConnectionEvents.QUERY_START.register(new ResourceLocation("plutonium:pre"), (handler, server, sender, synchronizer) -> {
                if (!server.isSingleplayer()) {
                    sender.sendPacket(FULL_STREAM_COMPRESSION, PacketByteBufs.empty());
                }
            });
        }

        ServerLoginNetworking.registerGlobalReceiver(FULL_STREAM_COMPRESSION, (server, handler, understood, buf, synchronizer, responseSender) -> {
            if (understood) {
                ((FSCConnection)((AccessorServerLoginPacketListenerImpl)handler).plutonium$getConnection()).plutonium$enableFullStreamCompression();
            }
        });
    }

    public static LinkedBlockingQueue<QueuedPacket> getNextQueue() {
        return PACKET_QUEUES[Math.floorMod(nextQueue.getAndIncrement(), PACKET_QUEUES.length)];
    }

    public static ResourceLocation asResource(String name) {
        return new ResourceLocation(MODID, name);
    }
}
