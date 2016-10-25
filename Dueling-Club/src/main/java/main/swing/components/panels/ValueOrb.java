package main.swing.components.panels;

import main.content.PARAMS;

import java.awt.*;

public class ValueOrb extends ValueComp {
    public ValueOrb(PARAMS p, Color c) {
        super(VISUALS.VALUE_ORB_64, p);
    }

    protected boolean isVertical() {
        return true;
    }

    @Override
    protected String getCompPath() {
        return "UI\\components\\new\\orb ";
    }
}
