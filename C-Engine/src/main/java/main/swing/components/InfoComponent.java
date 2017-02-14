package main.swing.components;

import main.content.VALUE;
import main.entity.obj.Obj;
import main.swing.generic.components.Refreshable;
import main.swing.renderers.SmartTextManager;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;

import javax.swing.*;
import java.awt.*;

public class InfoComponent extends JLabel implements Refreshable {
    private static final int FACTOR = 5;
    String textureImage;
    private SmartTextManager smartRenderManager;
    private Obj obj;
    private VALUE value;
    private int compWidth;
    private int compHeight;
    private float size;
    private Font font;

    public InfoComponent(VALUE value, Obj obj, int compWidth, int compHeight,
                         float size) {
        this.size = size;
        this.obj = obj;
        this.value = value;
        this.compWidth = compWidth;
        this.compHeight = compHeight;
        this.smartRenderManager = new SmartTextManager();

        init();
    }

    private void init() {
        font = FontMaster.getFont(FONT.MAIN, size, Font.PLAIN);
        setFont(font);
        refresh();
    }

    @Override
    public void refresh() {
        String text = value.getName() + ": " + obj.getValue(value);
        setText(text);
        // setIcon(icon);

    }

//	@Override
//	public void paint(Graphics g) {
//		int x = 0;
//		int y = compHeight / FACTOR;
//		g.setFont(font);
//		Image valueIcon = ImageManager.getValueIcon(value);
//		if (valueIcon != null) {
//			g.drawImage(valueIcon, x, y, null);
//			x += valueIcon.getWidth(null) + compWidth / FACTOR;
//		} else {
//			g.drawString(value.getShortName(), x, y);
//
//			x += value.getShortName().toCharArray().length * size / 3
//					+ compWidth / FACTOR;
//		}
//
//		g.setColor(smartRenderManager.getValueCase(value, obj).getColor());
//		g.drawString(obj.getValue(value), x, y);
//
//	}

}
