package net.teekay.axess.block.link.payload;

import net.teekay.axess.block.link.ILinkableBlockEntity;

public class LockdownUpdateLinkPayload extends AbstractLinkPayload {

    private boolean newState;

    public LockdownUpdateLinkPayload(ILinkableBlockEntity origin, boolean newState) {
        super(origin);
        this.newState = newState;
    }

    public boolean getNewState() {
        return newState;
    }
}
