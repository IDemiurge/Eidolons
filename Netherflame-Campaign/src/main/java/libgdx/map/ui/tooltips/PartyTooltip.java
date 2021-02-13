package libgdx.map.ui.tooltips;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import eidolons.content.consts.VisualEnums;
import eidolons.game.battlecraft.ai.tools.priority.ThreatAnalyzer;
import libgdx.StyleHolder;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.HorGroup;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.tooltips.Tooltip;
import libgdx.map.obj.PartyActor;
import libgdx.texture.TextureCache;
import eidolons.macro.MacroGame;
import eidolons.macro.entity.party.MacroParty;
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
    private final Label threatLabel;
    private final Label allegiance;

    public PartyTooltip(MacroParty party, PartyActor actor) {
        this.party = party;
        this.actor = actor;

        setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));

        ValueContainer main = new ValueContainer(TextureCache.getOrCreateR(
                party.getEmblemPath()),
                party.getName());
        ValueContainer leader = new ValueContainer(TextureCache.getOrCreateR(
                party.getLeader().getImagePath()),
                "Leader: ",
                party.getName());
        //set to "known members: " if...
        ValueContainer members = new ValueContainer(party.getMembers().size() + " ", "Members");
        HorGroup<Image> membersPics = new HorGroup<>(Math.max(256, party.getMembers().size() * 128 / 3), 0, party.getMembers().stream().map(hero ->
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



    @Override
    public void updateAct(float delta) {
        String text;
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
            VisualEnums.THREAT_LEVEL threat = ThreatAnalyzer.getThreatLevel(party, MacroGame.getGame().getPlayerParty());
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

        setUpdateRequired(true);

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

}