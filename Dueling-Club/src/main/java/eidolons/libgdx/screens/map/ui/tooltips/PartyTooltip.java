package eidolons.libgdx.screens.map.ui.tooltips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import eidolons.content.PARAMS;
import eidolons.game.module.adventure.MacroGame;
import eidolons.game.module.adventure.entity.party.MacroParty;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.HorGroup;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.screens.map.obj.PartyActor;
import eidolons.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.FontMaster.FONT;

import java.util.stream.Collectors;

/**
 * Created by JustMe on 2/23/2018.
 */
public class PartyTooltip extends Tooltip {
    private final MacroParty party;
    private final PartyActor actor;
    private final ValueContainer leader;
    private final ValueContainer members;
    private final HorGroup<Image> membersPics;
    private final Label threatLabel;
    private final Label allegiance;
    private final ValueContainer main;

    public PartyTooltip(MacroParty party, PartyActor actor) {
        this.party = party;
        this.actor = actor;

        setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));

        main = new ValueContainer(TextureCache.getOrCreateR(
         party.getEmblemPath()),
         party.getName());
        leader = new ValueContainer(TextureCache.getOrCreateR(
         party.getLeader().getImagePath()),
         "Leader: ",
         party.getName());
        //set to "known members: " if...
        members = new ValueContainer(party.getMembers().size() + " ", "Members");
        membersPics =
         new HorGroup<>(Math.max(256, party.getMembers().size() * 128 / 3), 0, party.getMembers().stream().map(hero ->
          new Image(TextureCache.getOrCreateR(hero.getImagePath()))
         ).collect(Collectors.toList()));
        allegiance = new Label("", StyleHolder.getDefaultLabelStyle());

//        Label status; //traveling, guarding, ..
//        Label visibility; //in sight, known

        threatLabel = new Label("", StyleHolder.getDefaultLabelStyle());
//        TextBuilder builder = new TextBuilder();
        defaults().uniform(true, false).space(5);

        add(main).row();
        add(leader).row();
        add(allegiance).row();
        add(threatLabel).row();
        add(members).row();
        add(membersPics).row();

        main.setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));
        leader.setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));
        members.setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));
//        membersPics.setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(1, 1, 1, 1);
        super.draw(batch, parentAlpha);
    }

    private THREAT_LEVEL getThreatLevel(MacroParty party, MacroParty playerParty) {
        int percentage = party.getParamSum(PARAMS.POWER)
         * 100 / playerParty.getParamSum(PARAMS.POWER);
        THREAT_LEVEL level = null;
        for (THREAT_LEVEL sub : THREAT_LEVEL.values()) {
            level = sub;
            if (sub.powerPercentage <= percentage)
                break;
        }
        return level;
    }


    @Override
    public void updateAct(float delta) {
        String text = "Unknown";
        boolean showThreat = false;
        setVisible(showing);
//        if (party.getInfoLevel() == MAP_OBJ_INFO_LEVEL.VISIBLE) {
        text = "Neutral";
        if (!party.getOwner().isMe()) {
            showThreat = true;
            if (party.getOwner().isNeutral()) {
                text = "Neutral";
            } else {
                text = party.getOwner().isHostileTo(MacroGame.game.getPlayerParty().getOwner()) ? "Hostile" : "Friendly";

            }
        }
//        }
        this.allegiance.setText(text);

        if (showThreat) {
            THREAT_LEVEL threat = getThreatLevel(party, MacroGame.getGame().getPlayerParty());
            threatLabel.setText(threat.name());
            threatLabel.setStyle(StyleHolder.getSizedColoredLabelStyle(FONT.AVQ, 20, threat.color));
        } else
            threatLabel.setText("");
    }

    @Override
    protected void onTouchDown(InputEvent event, float x, float y) {
        if (event.getButton() != 1)
            return;
        showing = true;
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, this);
    }

    @Override
    protected void onTouchUp(InputEvent event, float x, float y) {
        showing = false;
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, null);
    }

    @Override
    protected void onMouseMoved(InputEvent event, float x, float y) {

    }

    @Override
    protected void onMouseEnter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//        super.onMouseEnter(event, x, y, pointer, fromActor);
//        ActorMaster.addScaleAction(actor, 1.2f, 1.2f, 0.4f);

        updateRequired = true;

        actor.hover();
    }

    @Override
    protected void onMouseExit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        if (toActor == actor.getEmblem() || toActor == actor.getPortrait())
            return;
        if (toActor != null)
            if (toActor.getParent() == actor)
                return;
        if (toActor == actor)
            return;
        actor.minimize();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    public enum THREAT_LEVEL {
        DEADLY(GdxColorMaster.PURPLE, 250),
        OVERPOWERING(GdxColorMaster.RED, 175),
        CHALLENGING(GdxColorMaster.ORANGE, 125),
        EVEN(GdxColorMaster.YELLOW, 100),
        MODERATE(GdxColorMaster.BLUE, 75),
        EASY(GdxColorMaster.GREEN, 50),
        EFFORTLESS(GdxColorMaster.WHITE, 25),;
        public final Color color;
        public final int powerPercentage;

        THREAT_LEVEL(Color color, int powerPercentage) {
            this.color = color;
            this.powerPercentage = powerPercentage;
        }

    }
}