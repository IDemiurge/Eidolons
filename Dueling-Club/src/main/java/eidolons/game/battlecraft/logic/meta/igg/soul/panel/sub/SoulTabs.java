package eidolons.game.battlecraft.logic.meta.igg.soul.panel.sub;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.game.battlecraft.logic.meta.igg.soul.panel.LordPanel;
import eidolons.libgdx.gui.panels.TabbedPanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.system.auxiliary.StringMaster;

public class SoulTabs extends TabbedPanel {
    private   Image background;

    public SoulTabs(LordPanel.SOUL_TABS... tabs) {
//        addActor(background = new Image(TextureCache.getOrCreate(Images.COLUMNS_AND_TREE_BG)));
//        background.setPosition();
        for (LordPanel.SOUL_TABS tab : tabs) {
            addTab(createTab(tab), StringMaster.getWellFormattedString(tab.toString()));
        }
    }

    @Override
    protected Cell addTabTable() {
        return super.addTabTable();
//        .size(background.getPrefWidth(),background.getPrefHeight());
    }

    @Override
    protected TablePanelX createContentsTable() {
        TablePanelX table = super.createContentsTable();
//        table.setSize(background.getPrefWidth(),background.getPrefHeight());
        table.setSize(555,898);
        return table;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        background.draw(batch, parentAlpha);
        super.draw(batch, parentAlpha);
    }

    private SoulTab createTab(LordPanel.SOUL_TABS tab) {
        switch (tab) {
            case CHAIN:
                return new ChainPanel();
            case SOULS:
                return new SoulsPanel();
            case MEMORIES:
                return new LordMemoryPanel();
            case LORD:
                return new LordMemoryPanel();
            case FLAME:
                return new FlamePanel();
            case STATS:
                return new LordStatsPanel();
        }
        return null;
    }


}
