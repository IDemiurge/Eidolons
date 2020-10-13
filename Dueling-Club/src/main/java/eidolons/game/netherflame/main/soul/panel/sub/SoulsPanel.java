package eidolons.game.netherflame.main.soul.panel.sub;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import eidolons.content.PARAMS;
import eidolons.game.netherflame.main.soul.eidola.EidolonImbuer;
import eidolons.game.netherflame.main.soul.eidola.Soul;
import eidolons.game.netherflame.main.soul.eidola.SoulMaster;
import eidolons.game.netherflame.main.soul.panel.sub.imbue.ImbuePanel;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.generic.VerticalValueContainer;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartTextButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.tooltips.DynamicTooltip;
import eidolons.libgdx.shaders.DarkShader;
import eidolons.libgdx.shaders.ShaderDrawer;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.List;

public class SoulsPanel extends SoulTab {
    private final LabelX soulforce;
    private final LabelX trapped;
    private final TablePanelX souls;
    private final ImbuePanel imbuePanel;
    TablePanelX infoHeader = new TablePanelX();
    TablePanelX headerButtons = new TablePanelX();
    private ButtonGroup<Button> btnGroup;
    private final List<SoulActor> soulActors=    new ArrayList<>()  ;

    public SoulsPanel() {
        imbuePanel = new ImbuePanel();
        imbuePanel.setVisible(false);
        addActor(imbuePanel);

        TablePanelX header = new TablePanelX();
//        header.addActor(bg);
        infoHeader.add(soulforce = new LabelX("Soulforce: ")).row();
        infoHeader.add(trapped = new LabelX("Souls Trapped: ")).row();

        headerButtons.add(new SmartTextButton("Imbue", ButtonStyled.STD_BUTTON.TAB_HIGHLIGHT_COLUMN, this::toggleImbuePanel));

        header.add(infoHeader);
        header.add(headerButtons);

        add(header).row();

        souls = new TablePanelX();
        add(souls).row();

        imbuePanel.setPosition(getPrefWidth() + 250, 20);

        GuiEventManager.bind(GuiEventType.UPDATE_SOULS_PANEL, p -> {
            imbuePanel.update();
            soulActors.forEach(SoulActor::update);
        });
    }

    @Override
    public void layout() {
        super.layout();
    }

    private void toggleImbuePanel() {
        imbuePanel.toggleFade();

    }

    @Override
    public void updateAct(float delta) {
        if (getUserObject() == null) {
            return;
        }
        souls.clearChildren();
        btnGroup = new ButtonGroup<>();
        int i = 0;
        for (Soul soul : getUserObject().getSouls()) {
            SoulActor actor = new SoulActor(soul);
            i++;
            if (i % 2 == 0)
                souls.add(actor).row();
            else
                souls.add(actor);
            soulActors.add(actor);
        }

        trapped.setText("Souls Trapped: " +
                getUserObject().getSouls().size());
        soulforce.setText("Soulforce: " +
                getUserObject().getLord().getIntParam(PARAMS.SOULFORCE));
    }

    public class SoulActor extends TablePanelX {
        private final SmartTextButton btn;
        private  ShaderProgram shader;
        Soul soul;

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (parentAlpha == ShaderDrawer.SUPER_DRAW )
            super.draw(batch, 1);
        else
            ShaderDrawer.drawWithCustomShader(this, batch, shader);
        }

        @Override
        public void update() {
            shader = soul.isBeingUsed() ? DarkShader.getDarkShader() : null;
            btn.setDisabled(soul.isBeingUsed());
        }

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
            String aspects1 = parts.get(0) + " " + parts.get(1);
            String aspects2 = parts.get(2) + " " + parts.get(3);
            VerticalValueContainer aspects = new VerticalValueContainer(aspects1, aspects2);
            table.add(aspects).center().row();

                  btn = new SmartTextButton("Consume", ButtonStyled.STD_BUTTON.TAB_HIGHLIGHT_COLUMN,
                    () -> {

                        if (imbuePanel.isVisible()) {
                            imbuePanel.addSoul(soul);
                            soul.setBeingUsed(true);
                            GuiEventManager.trigger(GuiEventType.UPDATE_SOULS_PANEL);
                            return;
                        }

                        SoulMaster.consume(soul);
                        GuiEventManager.trigger(GuiEventType.UPDATE_SOULS_PANEL);
                    });
            btnGroup.add(btn);
            table.add(btn);
            btn.addListener(new DynamicTooltip(()->{
                return soul.isBeingUsed() ? "This Soul is being used" : "Destroy this soul to gain " +
                        sf +
                        " Soulforce";
            }).getController());

            add(table);

            FadeImageContainer portrait = new FadeImageContainer(soul.getUnitType().getImagePath());
            add(portrait);
//      tooltip
        }
    }
}























