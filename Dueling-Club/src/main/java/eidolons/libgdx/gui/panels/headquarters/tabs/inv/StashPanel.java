package eidolons.libgdx.gui.panels.headquarters.tabs.inv;

import eidolons.game.core.Eidolons;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.texture.Images;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 10/16/2018.
 *
 * rollable
 *
 * data per town
 *
 * drag'n'drop support
 *
 * custom operations?
 *
 * new cell_type?
 *
 * party usage
 *
 */
public class StashPanel extends TablePanelX{

    StashSlotsPanel slotsPanel;

    public StashPanel() {
//        TablePanelX header = new TablePanelX<>(400, 140);
//        header.add(new ImageContainer(Images.STASH_LANTERN)).left();
//        header. add(new ImageContainer(Images.CHEST_OPEN)).growX();
//        header. add(new ImageContainer(Images.STASH_LANTERN)).right();
//        header.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
//        add(header).row();

        TablePanelX header = new TablePanelX<>(150, 280);
        header. add(new ImageContainer(Images.CHEST_OPEN)).row();
        header.add(new ImageContainer(Images.STASH_LANTERN)).row();
        add(header).top();

        int size = Eidolons.getTown().getStashSize();
        List<Integer> dividers=    new ArrayList<>() ;
        for (int i = 2; i <= size/2; i++) {
            if (size%i==0)
                dividers.add(i);
        }
        int n = dividers.size() / 2;
        Integer preferred = dividers.get(n);
        while(preferred<6 && n<dividers.size()){
            n++;
            preferred=dividers.get(n);
        }
        int cols = preferred;
        int rows = size / cols;

        add(slotsPanel = new StashSlotsPanel(rows, cols)).colspan(3);
        setSize(Math.max(400, slotsPanel.getWidth()),
         slotsPanel.getHeight()+128+ 20 + 40);
        slotsPanel.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());

    }
}
