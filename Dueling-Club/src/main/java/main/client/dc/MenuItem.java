package main.client.dc;

import main.client.dc.MainManager.MAIN_MENU_ITEMS;
import main.swing.components.buttons.CustomButton;
import main.system.audio.DC_SoundMaster;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.awt.*;

public class MenuItem extends CustomButton {
    private static final float SIZE = 18;
    private MAIN_MENU_ITEMS item;
    private Object obj;

    public MenuItem(MAIN_MENU_ITEMS item) {
        super(VISUALS.MENU_ITEM);
        setText(item.getText());
        this.item = item;
    }

    public MenuItem(Object obj) {
        super(VISUALS.MENU_ITEM);
        setText(obj.toString());
        this.obj = obj;
    }


    @Override
    protected boolean isMoreY() {
        return false;
    }

    @Override
    public void handleClick() {
        // itemClicked(data.item);
    }

    public MAIN_MENU_ITEMS getItem() {
        return item;
    }

    @Override
    protected int getDefaultY() {
        return super.getDefaultY() * 4 / 3;
    }

    @Override
    protected void playClickSound() {
        DC_SoundMaster.playStandardSound(STD_SOUNDS.TURN);
    }

    @Override
    protected Font getDefaultFont() {
        return FontMaster.getFont(FONT.DARK, SIZE, Font.PLAIN);
    }


    public Object getObj() {
        return obj;
    }

}