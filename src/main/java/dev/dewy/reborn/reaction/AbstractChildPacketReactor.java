package dev.dewy.reborn.reaction;

import dev.dewy.reborn.client.Child;

/**
 * Used when we need to access the relevant child during a reaction.
 */
public abstract class AbstractChildPacketReactor {

    private Child child;

    public void setChild(Child child) {
        this.child = child;
    }

    public Child getChild() {
        return child;
    }
}
