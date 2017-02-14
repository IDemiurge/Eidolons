package main.music.gui;

import main.music.ahk.AHK_Master;
import main.swing.components.TextComp;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.graphics.GuiManager;
import main.system.auxiliary.StringMaster;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ListButtonPanel extends G_Panel {
    private MusicListPanel panel;
    private Map<String, List<String>> map;
    private int customWrap;
    private List<String> musicConsts;

    public ListButtonPanel(Map<String, List<String>> map, MusicListPanel musicListPanel,
                           int customWrap, List<String> musicConsts) {
        super("flowy");
        this.map = map;
        this.panel = musicListPanel;
        this.customWrap = customWrap;
        this.musicConsts = musicConsts;

    }

    @Override
    public void refresh() {
        removeAll();
        int n = 0;
        int i = 0;
        for (final String chars : map.keySet()) {
            List<String> lines = map.get(chars);
            lines = panel.sort(lines, chars);

            int wrap = customWrap;
            if (wrap != 0) {
                wrap += Math.min(2, Math.abs(Math.round((wrap * 10 - lines.size()) / 10)));
                TextComp comp = new TextComp(StringMaster
                        .getWellFormattedString(musicConsts.get(i)), Color.black) {
                    @Override
                    public int getPanelHeight() {
                        return super.getPanelHeight() / 3 * 2;
                    }

                    @Override
                    protected int getDefaultFontSize() {
                        return 15;
                    }
                };
                add(comp, "x @center_x");
                if (i == 0) {
                    add(comp, "x @center_x");
                }
                i++;
            }

            G_Panel subPanel = new G_Panel("flowy");
            if (wrap == 0) {
                subPanel.setPanelSize(new Dimension(GuiManager.getScreenWidthInt(), GuiManager
                        .getScreenHeightInt()
                        / map.keySet().size()));

                add(subPanel);
            } else {
                add(subPanel, "w " + GuiManager.getScreenWidthInt());
            }
            Character lastLetter = null;
            boolean letterAdded = false;

            for (String line : lines) {
                try {
                    char letter = AHK_Master.getLetter(line);
                    n = addButton(wrap, n, subPanel, lastLetter, letterAdded, line, letter);
                    lastLetter = letter;
                    letterAdded = n == 0;
                } catch (Exception e) {
                    LogMaster.log(1, "failed: " + line);
                    e.printStackTrace();
                }
            }

        }

    }

    private int addButton(final int customWrap, int i, G_Panel subPanel, Character lastLetter,
                          boolean letterAdded, String line, char letter) {
        List<String> list = StringMaster.openContainer(line, "::");
        String keyPart = list.get(0);
        if (list.size() < 2) {
            LogMaster.log(1, " ");
            return i;
        }
        String funcPart = list.get(1).trim();

        Font font = FontMaster.getFont(FONT.NYALA, 16, Font.PLAIN);
        boolean wrap;

        // TODO adapt to max list size also
        wrap = panel.checkWrap(i, customWrap, lastLetter, letter);
        i++;
        if (wrap) {
            i = 0;
            letterAdded = false;
        }
        lastLetter = letter;

        String sizeGroup = "SizeGroup" + (letter);
        String pos = wrap ? "wrap," : "" + "sg " + sizeGroup;
        String name = panel.formatListName(funcPart);
        if (panel.isLetterShown()) {
            if (!letterAdded) {
                // letterAdded = true;
                name = StringMaster.wrapInBraces("" + letter) + name;
            }
        }
        JButton button = panel.getButton(keyPart, funcPart, font, name);
        subPanel.add(button, pos);

        subPanel.setBorder(BorderFactory.createBevelBorder(3));

        return i;

    }
}
