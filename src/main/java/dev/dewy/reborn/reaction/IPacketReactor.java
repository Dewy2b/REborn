package dev.dewy.reborn.reaction;

import com.github.steveice10.packetlib.packet.Packet;

public interface IPacketReactor<T extends Packet> {

    /**
     * The action to take when a packet is recieved from the remote server
     *
     * @param packet The packet
     * @return TRUE if REborn should push the packet to the connected children,
     * FALSE if REborn should BLOCK the packet from reaching the chilren.
     */
    boolean takeAction(T packet);

}
