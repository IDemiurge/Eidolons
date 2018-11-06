package eidolons.libgdx.gui.panels.dc.menus.outcome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import eidolons.game.battlecraft.logic.battle.mission.MissionStatManager;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.TiledNinePatchGenerator.BACKGROUND_NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.gui.panels.TabbedPanel;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.shaders.ShaderMaster;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.audio.DC_SoundMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.Bools;
import main.system.datatypes.WeightMap;
import main.system.graphics.FontMaster.FONT;
import main.system.graphics.MigMaster;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.Map;

/**
 * Created by JustMe on 8/15/2017.
 */
public class OutcomePanel extends TablePanelX implements EventListener {
    public static final boolean TEST_MODE = true;
    public static final boolean TEST_OUTCOME = false;
    private static final String VICTORY_MESSAGE =
     "+++That's It - You are Victorious!+++";
    private static final String DEFEAT_MESSAGE =
     "***All is lost - the Enemy has Prevailed!***";
    Cell<TextButton> exitButton;
    Cell<TextButton> continueButton;
    private Boolean outcome;
    private OutcomeDatasource datasource;
    private Image picture;
    private Label message;
    private TabbedPanel unitStatTabs;

    public OutcomePanel() {
        addListener(this);
        Texture background = TiledNinePatchGenerator.getOrCreateNinePatch(
         NINE_PATCH.SAURON, BACKGROUND_NINE_PATCH.PATTERN, (int) GdxMaster.adjustSize(980), (int) GdxMaster.adjustSize(600));
        TextureRegion textureRegion = new TextureRegion(background);
        setBackground(new TextureRegionDrawable(textureRegion));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (parentAlpha == ShaderMaster.SUPER_DRAW) {
            super.draw(batch, 1);
            return;
        }
        ShaderMaster.drawWithCustomShader(this, batch, null);
    }

    @Override
    public void updateAct(float delta) {
        if (getUserObject() instanceof OutcomeDatasource) {
            init((OutcomeDatasource) getUserObject());
        }
        super.updateAct(delta);
    }

    public void init(OutcomeDatasource outcomeDatasource) {
        clear();
        setVisible(true);
        outcome = outcomeDatasource.getOutcome();
        if (outcome == null)
            outcome = TEST_OUTCOME;

        datasource = outcomeDatasource;
        String imgPath = "UI\\big\\victory.png";
        if (outcome != null)
            imgPath = outcome ? "UI\\big\\victory.png" : "UI\\big\\defeat.jpg";
        picture = new Image(TextureCache.getOrCreateR(imgPath));

        addActor(picture);
        picture.setAlign(Align.center);
        picture.setScale(GdxMaster.getFontSizeModSquareRoot());
        picture.setPosition(
         MigMaster.center(getWidth(), picture.getWidth() * picture.getScaleX()),
         MigMaster.center(getHeight(), picture.getHeight() * picture.getScaleY()
         ));

        STD_SOUNDS sound = STD_SOUNDS.DEATH;

        String messageText = VICTORY_MESSAGE;
        if (outcome != null)
            sound = outcome ? STD_SOUNDS.VICTORY : STD_SOUNDS.DEATH;
        if (outcome != null)
            messageText = outcome ? VICTORY_MESSAGE : DEFEAT_MESSAGE;
        message = new Label(messageText, StyleHolder.getSizedColoredLabelStyle(0.25f, FONT.AVQ, 22));
        addActor(message);
        message.setAlignment(Align.top);
        message.setPosition(
         MigMaster.center(getWidth(), message.getWidth()),
         MigMaster.top(getWidth(), message.getHeight() + 55
         ));

        DC_SoundMaster.playStandardSound(sound);

        TablePanel<Actor> stats = new TablePanel<>();
        datasource.getPlayerStatsContainers().forEach(c -> {
            stats.addElement(c).fill(false).expand(0, 0).bottom()
             .size(150, 50);
            stats.row();
        });
        addElement(stats);
        //        new ScrollPanel<>(stats);


        final TablePanel<TextButton> buttonTable = new TablePanel<>();
        continueButton = buttonTable.addElement(
         new TextButton("Achievements", //getContinueText(outcome),
          StyleHolder.getMenuTextButtonStyle(16))
        ).fill(false).expand(0, 0).right()
         .pad(20, 10, 20, 10);
        continueButton.getActor().addListener(this);

        exitButton = buttonTable.addElement(
         new TextButton("Main Menu",
          StyleHolder.getMenuTextButtonStyle(18))
        ).fill(false).expand(0, 0).center()
         .pad(20, 10, 20, 10);
        exitButton.getActor().addListener(this);

        buttonTable.row();

        addActor(buttonTable);
        buttonTable.setPosition(
         MigMaster.center(getWidth(), buttonTable.getWidth()),
         55
        );

        float y = GdxMaster.getHeight() -
         (GdxMaster.getHeight() -  getHeight() / 2);
        float x = (GdxMaster.getWidth() -  getWidth()) / 2;
        ActorMaster.addMoveToAction(this, x, y, 2.5f);
        //        addElement(buttonTable).pad(0, 20, 20, 20);
    }

    private String getContinueText(Boolean outcome) {
        if (Bools.isTrue(outcome))
            return new WeightMap<String>().
             chain("For Glory!", 10).
             chain("Can this be?", 10).
             chain("Yes, but how?", 7).
             getRandomByWeight();

        return new WeightMap<String>().
         chain("What happened?", 10).
         chain("How did I die?", 10).
         chain("No...", 7).
         chain("Not again...", 5).
         getRandomByWeight();
    }

    @Override
    public boolean remove() {
        return super.remove();
    }

    @Override
    public boolean handle(Event e) {
        if (!(e instanceof InputEvent)) return true;
        InputEvent event = ((InputEvent) e);

        if (event.getType() != Type.touchDown)
            return true;
        if (getActions().size > 0) return true;
        Actor actor = event.getTarget();
        if (actor instanceof Label) {
            if (actor.getParent() instanceof TextButton) {
                if (CoreEngine.isIDE() &&TEST_MODE){
                    setVisible(false);
                } else
                rollBack();
                //                ActorMaster.addRemoveAfter(this);
                final Boolean exit_continue_next = getEventType(actor);
                if (exit_continue_next == null) {
                    if (!ExplorationMaster.isExplorationOn())
                        Eidolons.getGame().getMaster().nextLevel();

                    if (!Bools.isTrue(outcome))
                        Eidolons.restart();


                } else if (exit_continue_next) {

                    if (CoreEngine.isMacro()) {
                        GuiEventManager.trigger(GuiEventType.BATTLE_FINISHED);
                    } else {
                        Eidolons.exitFromGame();
                    }


                } else {
                    //TODO display stats!
                    String stats = MissionStatManager.getGameStatsText( );
                    EUtils.onConfirm(stats +
                     "\n Exit to menu?", true, () ->
                     Eidolons.exitFromGame());
                    WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_FINISHED,
                     false);

                }
                remove();
                return true;
            }

        }


        return false;
    }

    private void rollBack() {
        ActorMaster.addMoveToAction(this, getX(),
         GdxMaster.getHeight() - exitButton.getActorHeight(), 1.5f);
    }

    private String getGameStats(OutcomeDatasource datasource) {
        String stats = "";
        stats += "\n Glory rating:" + datasource.getGlory() + StringMaster.wrapInParenthesis(
         getCodename(datasource.getGlory())
        );
        stats += "\n Units Slain:" + datasource.getUnitsSlain();
        stats += "\n Damage dealt:" + datasource.getDAMAGE_DEALT();
        stats += "\n Damage taken:" + datasource.getDAMAGE_TAKEN();
        Map<String, Integer> map = datasource.getHeroStats().getGeneralStats();
        for (String s : map.keySet()) {
            stats+="\n" + s + ": " + map.get(s);
        }
//        for (String s : datasource.getHeroStats().getStatMap()) {
//            stats+="\n" + s + ": " + datasource.getMainStats().get(s);
//        }
        //class outcome!
        return stats;
    }

    private String getCodename(Integer glory) {
        return "Einherjar";
    }

    private Boolean getEventType(Actor actor) {
        if (actor == continueButton.getActor().getLabel()) {
            return false;
        }
        return true;
    }

}
