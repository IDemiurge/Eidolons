package eidolons.libgdx.screens;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.fullscreen.Screenshake;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import eidolons.libgdx.stage.BattleGuiStage;
import eidolons.libgdx.stage.GuiStage;
import eidolons.macro.entity.town.Town;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import static main.system.GuiEventType.DIALOG_SHOW;

/**
 * Created by JustMe on 10/15/2018.
 */
public abstract class GameScreenWithTown extends GenericDungeonScreen {

    protected TownPanel townPanel;

    @Override
    protected void preBindEvent() {
        super.preBindEvent();
        GuiEventManager.bind(GuiEventType.GAME_PAUSED, d -> {
            DC_Game.game.getLoop().setPaused(true);
        });
        GuiEventManager.bind(GuiEventType.GAME_RESUMED, d -> {
            DC_Game.game.getLoop().setPaused(false);
        });
        GuiEventManager.bind(GuiEventType.CAMERA_SHAKE, p -> {
            shakes.add((Screenshake) p.get());
        });
        GuiEventManager.bind(DIALOG_SHOW, obj -> {
            DialogueHandler handler =
                    (DialogueHandler) obj.get();
            getGuiStage().afterBlackout(() -> getGuiStage().dialogueStarted(handler));
//            if (dialogsStage == null) {
//                dialogsStage = new ChainedStage(viewPort, getBatch(), list);
//
//            } else {
//                dialogsStage.play(list);
//            }
//            dialogsStage.setDialogueHandler(handler);
//            updateInputController();
        });
        GuiEventManager.bind(GuiEventType.SHOW_TOWN_PANEL, p -> {
            try {
                showTownPanel(p);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                showTownPanel(null);
                Eidolons.exitToMenu();
            }
        });
    }

    public boolean isOpaque() {
        if (blackoutAction != null)
            if (blackoutAction.getValue() >= 1) {
                return true;
            }
        if (getGuiStage().getDialogueContainer() != null) {
            return getGuiStage().getDialogueContainer().isOpaque();
        }
        return false;
    }

    public GuiStage getGuiStage() {
        return (GuiStage) guiStage;
    }

    protected void showTownPanel(EventCallbackParam p) {

        if (p.get() == null) {
            townPanel.fadeOut();
            overlayStage.setActive(false);
            if (!isTownInLoaderOnly() || isLoaded()) {
                loading = false;
            } else {
                GdxMaster.setLoadingCursor();
            }
            TownPanel.setActiveInstance(null);
            updateInputController();
            getGuiStage().setTown(false);


        } else {
            loading = true;
            Town town = (Town) p.get();
            if (townPanel == null || TownPanel.TEST_MODE) {
                overlayStage.addActor(townPanel = new TownPanel());
            } else {
                townPanel.fadeIn();
                townPanel.entered();
            }
            try {
                townPanel.setUserObject(town); //  // TODO
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

            overlayStage.setActive(true);
            GdxMaster.setDefaultCursor();
            TownPanel.setActiveInstance(townPanel);
            updateInputController();
            getGuiStage().setTown(true);
        }
    }

    protected abstract boolean isTownInLoaderOnly();

    @Override
    protected void renderLoaderAndOverlays(float delta) {
        super.renderLoaderAndOverlays(delta);
        if (townPanel != null && townPanel.isVisible()) {
            guiStage.act(delta);
            guiStage.draw();
        }
    }

}
