package eidolons.netherflame.lord.arts;

import eidolons.netherflame.lord.EidolonLord;

/*
Simple rules for 'drawing from deck' into left/right slot
 */
public class Soulflux {

    ArsEidola left;
    ArsEidola center;
    ArsEidola right;

    EidolonLord lord;
    SoulDeck deck;
    private boolean toggle;
    ArsEidola centerV1;
    ArsEidola centerV2;

    public Soulflux(EidolonLord lord, ArsEidola centerV1, ArsEidola centerV2) {
        this.lord = lord;
        this.centerV1 = centerV1;
        this.centerV2 = centerV2;
    }

    public void init(){
        deck.shuffle();
    }
    /*
    we may not even need much animation, it would happen under the hood!
     */
    public void newRound() {
        toggleCenter();
        right = left;
        left = deck.draw();
        if (left == null) {
            deck.shuffle();
            left = deck.draw();
        }
    }

    private void toggleCenter() {
        toggle = !toggle;
        center = getCenter(toggle);
    }

    private ArsEidola getCenter(boolean toggle) {
        return toggle ? centerV1 : centerV2;
    }
}
