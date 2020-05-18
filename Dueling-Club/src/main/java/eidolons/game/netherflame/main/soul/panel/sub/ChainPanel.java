package eidolons.game.netherflame.main.soul.panel.sub;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import eidolons.game.netherflame.main.death.ChainHero;
import eidolons.game.netherflame.main.soul.panel.LordPanel;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.headquarters.hero.HqVerticalValueTable;
import main.system.graphics.FontMaster;

public class ChainPanel extends SoulTab {

    private static final String RESTORE = "Restore";
    private static final String INFO = "View Info";
    private static final String SCREEN = "Hero Screen";

    public ChainPanel() {
        super();
//        debugAll();
    }

    @Override
    public void update() {
        LordPanel.LordDataSource dataSource =  getUserObject();
        for (ChainHero chainHero : dataSource.getChain().getHeroes()) {
            ChainHeroView view = new ChainHeroView(chainHero);
            add(view).row();
        }

    }

    public static class ChainButtonHandler implements ButtonHandler {

        @Override
        public void handle(String btn) {
            switch (btn) {
                case RESTORE:
                case INFO:
                case SCREEN:

            }
        }
    }

    public class ChainHeroView extends TablePanelX {
        ChainHero chainHero;

        public ChainHeroView(ChainHero chainHero) {
            this.chainHero = chainHero;
            Stack portraitStack = new Stack();
//            portraitStack.add(new Image(TextureCache.getOrCreate(Images.CHAIN_BACKGROUND)));
            TablePanelX<Actor> header = new TablePanelX<>();
            header.add(new HqVerticalValueTable());
            int deaths = chainHero.getDeaths();
            int lives = chainHero.getLives();
            if (lives == 0) {
                header.add(new MiniButtonPanel(new ChainButtonHandler(), SCREEN, INFO, RESTORE));
            } else {
                header.add(new MiniButtonPanel(new ChainButtonHandler(), SCREEN, INFO));
            }
            add(header).row();
            add(portraitStack).row();
            TablePanelX<Actor> info = new TablePanelX<>();
            info.add(new ValueContainer("Level", "").setStyle(StyleHolder.getSizedLabelStyle(FontMaster.FONT.AVQ, 17))).row();
            info.add(new ValueContainer("Lives: ", ""+lives).setStyle(StyleHolder.getSizedLabelStyle(FontMaster.FONT.AVQ, 17))).row();
            info.add(new ValueContainer("Deaths: ", ""+deaths).setStyle(StyleHolder.getSizedLabelStyle(FontMaster.FONT.AVQ, 17))).row();
            add(info);
        }
    }
}








