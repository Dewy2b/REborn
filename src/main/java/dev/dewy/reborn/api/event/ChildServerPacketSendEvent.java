package dev.dewy.reborn.api.event;

import com.github.steveice10.packetlib.packet.Packet;
import com.sasha.eventsys.SimpleCancellableEvent;
import dev.dewy.reborn.client.Child;

/**
 * Invoked when we send a client-bound packet to a child client
 */
public class ChildServerPacketSendEvent extends SimpleCancellableEvent {

    private Packet pck;
    private Child child;

    public ChildServerPacketSendEvent(Child child, Packet pck) {
        this.pck = pck;
        this.child = child;
    }

    public Packet getSendingPacket() {
        return pck;
    }

    public void setSendingPacket(Packet pck) {
        this.pck = pck;
    }

    public Child getChild() {
        return child;
    }
}
