package eidolons.game.battlecraft.logic.meta.igg.soul.panel.sub;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.content.PARAMS;
import eidolons.game.battlecraft.logic.meta.igg.soul.eidola.EidolonImbuer;
import eidolons.game.battlecraft.logic.meta.igg.soul.eidola.Soul;
import eidolons.game.battlecraft.logic.meta.igg.soul.eidola.SoulMaster;
import eidolons.game.battlecraft.logic.meta.igg.soul.panel.sub.imbue.ImbuePanel;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.generic.VerticalValueContainer;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.data.ListMaster;

import java.util.List;

public class SoulsPanel extends SoulTab {
    private final LabelX soulforce;
    private final LabelX trapped;
    private final TablePanelX souls;
    private ImbuePanel imbuePanel;
    TablePanelX infoHeader = new TablePanelX();
    TablePanelX headerButtons = new TablePanelX();

    public SoulsPanel() {
        imbuePanel = new ImbuePanel();
        imbuePanel.setVisible(false);
        imbuePanel.setPosition(getWidth(), getHeight() - 500);
        addActor(imbuePanel);

        TablePanelX header = new TablePanelX();
//        header.addActor(bg);
        infoHeader.add(soulforce = new LabelX("Soulforce: ")).row();
        infoHeader.add(trapped = new LabelX("Souls Trapped: ")).row();

        headerButtons.add(new SmartButton("Imbue", ButtonStyled.STD_BUTTON.TAB_HIGHLIGHT_COLUMN, () -> toggleImbuePanel()));

        header.add(infoHeader);
        header.add(headerButtons);

        add(header).row();

        souls = new TablePanelX();
        add(souls).row();

        debugAll();
    }

    private void toggleImbuePanel() {
        imbuePanel.toggleFade();

    }

    @Override
    public void updateAct(float delta) {
        souls.clearChildren();
        int i = 0;
        for (Soul soul : getUserObject().getSouls()) {
            SoulActor actor = new SoulActor(soul);
            i++;
            if (i % 2 == 0)
                souls.add(actor).row();
            else
                souls.add(actor);

        }

        trapped.setText("Souls Trapped: " +
                getUserObject().getSouls().size());
        soulforce.setText("Soulforce: " +
                getUserObject().getLord().getIntParam(PARAMS.SOULFORCE));
    }

    public class SoulActor extends TablePanelX {
        Soul soul;

        public SoulActor(Soul soul) {
            super(240, 128);
            this.soul = soul;
            TablePanelX<Actor> table = new TablePanelX<>();
            LabelX name = new LabelX(soul.getUnitType().getName(), StyleHolder.getAVQLabelStyle(18));
            table.add(name).center().row();
            String sf = soul.getForce() + "";
            ValueContainer force = new ValueContainer("Soulforce:", sf);
            table.add(force).center().row();
            List<String> parts = ContainerUtils.openContainer(
                    EidolonImbuer.getAspects(soul), " ");
            ListMaster.fillWithEmptyStrings(parts, 4);
            String aspects1 = parts.get(0) + " " +  parts.get(1) ;
            String aspects2 = parts.get(2) + " " +  parts.get(3) ;
            VerticalValueContainer aspects = new VerticalValueContainer(aspects1, aspects2);
            table.add(aspects).center().row();
            SmartButton btn = new SmartButton("Consume", ButtonStyled.STD_BUTTON.TAB_HIGHLIGHT_COLUMN,
                    () -> SoulMaster.consume(soul));
            table.add(btn);

            btn.addListener(new ValueTooltip("Destroy this soul to gain " +
                   sf +
                    " Soulforce").getController());

            add(table);

            FadeImageContainer portrait = new FadeImageContainer(soul.getUnitType().getImagePath());
            add(portrait);
//      tooltip
        }
    }
}























