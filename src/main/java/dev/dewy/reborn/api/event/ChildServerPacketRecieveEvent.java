package dev.dewy.reborn.api.event;

import com.github.steveice10.packetlib.packet.Packet;
import com.sasha.eventsys.SimpleCancellableEvent;
import dev.dewy.reborn.client.Child;

/**
 * Invoked when we recieve a server-bound packet from the child server
 */
public class ChildServerPacketRecieveEvent extends SimpleCancellableEvent {

    private Packet pck;
    private Child child;

    public ChildServerPacketRecieveEvent(Child child, Packet pck) {
        this.pck = pck;
        this.child = child;
    }

    public Packet getRecievedPacket() {
        return pck;
    }

    public void setRecievedPacket(Packet pck) {
        this.pck = pck;
    }

    public Child getChild() {
        return child;
    }
}
