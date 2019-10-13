package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.ai.explore.behavior.AiBehavior;
import eidolons.game.battlecraft.ai.explore.behavior.AiBehaviorManager;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.overlays.HpBar;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.panels.dc.atb.AtbPanel;
import eidolons.libgdx.gui.tooltips.ToolTipManager;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;

import java.util.function.Supplier;

public class GridUnitView extends GenericGridView {

    protected QueueView initiativeQueueUnitView;
    private LabelX debugInfo;
    private float scaleResetPeriod = 4;
    private float scaleResetTimer = scaleResetPeriod;

    OverlayView attachedObj;
    private float screenOverlay;

    @Override
    protected boolean isIgnored() {
        if (getUserObject() instanceof Unit) {

        }
        return super.isIgnored();
    }

    @Override
    public boolean isWithinCamera() {
        return super.isWithinCamera();
    }

    public GridUnitView(BattleFieldObject bfObj, UnitViewOptions options) {
        super(bfObj, options);
        initQueueView(options);
        if (AiBehaviorManager.TEST_MODE)
            addActor(debugInfo = new LabelX(
                    "", StyleHolder.getSizedColoredLabelStyle(FONT.AVQ, 20, Color.RED)) {
                @Override
                public void draw(Batch batch, float parentAlpha) {
                    if (parentAlpha == ShaderDrawer.SUPER_DRAW) {
                        super.draw(batch, 1);
                    } else {
                        ShaderDrawer.drawWithCustomShader(this,
                                batch, null);
                    }
                }
            });
    }

    public GridUnitView(UnitViewOptions o) {
        this(o.getObj(), o);
    }

    public void attachObj(OverlayView view) {
        view.setDirection(getUserObject().getDirection());

        addActor(view);
//        arrow.getVar

        //TODO  will we keep it as normal overlaying obj?
        // So basically maybe all we do really is change its position dynamically?

        /**
         *
         */

//        OverlayingMaster.getOffsetsForOverlaying()
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (hovered) {
            if (ToolTipManager.isHoverOff())
                setHovered(false);
        }
        if (debugInfo == null) {
            return;
        }
        scaleResetTimer -= delta;
        if (scaleResetTimer <= 0) {
            if (getScaleX() > 1)
                ActionMaster.addScaleAction(this, 1, 3);
            scaleResetTimer = scaleResetPeriod;
        }
        if (DC_Game.game.isDebugMode()) {
            if (getUserObject() instanceof Unit) {
                if (!getUserObject().isAiControlled()) {
                    return;
                }

                //color for enabled
                AiBehavior behavior = ((Unit) getUserObject()).getAI().getExploreAI().getActiveBehavior();
                debugInfo.setText(behavior.getType()
                        + ":\n " + behavior.getDebugInfo());
            }
            debugInfo.setVisible(true);
            debugInfo.pack();
            debugInfo.setY(GDX.top(debugInfo));
        } else {
            debugInfo.setVisible(false);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        if (isMainHero()) {
        if (getUserObject().isPlayerCharacter()) {
//            if (getPortrait().getColor().a == 0) {
//                getPortrait().fadeIn();
//            }
            ((CustomSpriteBatch) batch).resetBlending();
            if (getUserObject().isHidden()) {
//            if (EidolonsGame.DUEL) {
//                setZIndex(853485348);
//            }
                return;
            }
        }
        if (Cinematics.ON)
        if (!getColor().equals(GdxColorMaster.WHITE))
            portrait.setColor(getColor());

        if (screenOverlay > 0.01f) { //TODO need a flag instead
            emblemLighting.setVisible(false);
            if (emblemImage.getColor().a == 1) {
                emblemImage.fadeOut();
            }
            if (arrow.getColor().a == 1) {
                arrow.fadeOut();
            }
        }
        if (!(screenOverlay > 0)) {
            super.draw(batch, parentAlpha);
            return;
        }
        if ((screenOverlay > 0)) {
            return; //TODO cut it
        }
        portrait.setZIndex(999999);
        if (spritesContainersUnder != null)
            spritesContainersUnder.setVisible(false);
        if (spritesContainers != null)
            spritesContainers.setVisible(false);
        super.draw(batch, parentAlpha);

        ((CustomSpriteBatch) batch).setBlending(GenericEnums.BLENDING.SCREEN); //could do other blends too

        float a = getColor().a; // hue?
        getColor().a = screenOverlay;
        //setVisible(false); for all nonportrait
        super.draw(batch, parentAlpha);

        if (spritesContainersUnder != null)
            spritesContainersUnder.setVisible(true);
        if (spritesContainers != null)
            spritesContainers.setVisible(true);
        portrait.setZIndex(1);
        getColor().a = a;
        ((CustomSpriteBatch) batch).resetBlending();
    }

    public float getScreenOverlay() {
        return screenOverlay;
    }

    public void setScreenOverlay(float screenOverlay) {
        this.screenOverlay = screenOverlay;
    }

    @Override
    public void setFlickering(boolean flickering) {
        super.setFlickering(flickering);
        if (initiativeQueueUnitView != null)
            initiativeQueueUnitView.setFlickering(flickering);
    }

    @Override
    public void setGreyedOut(boolean greyedOut) {
        super.setGreyedOut(greyedOut);
        if (initiativeQueueUnitView != null)
            initiativeQueueUnitView.setGreyedOut(greyedOut);
    }


    @Override
    public void setToolTip(Tooltip tooltip) {
        super.setToolTip(tooltip);
        if (initiativeQueueUnitView != null) {
            initiativeQueueUnitView.setToolTip(tooltip);
        }
    }

    public QueueView getInitiativeQueueUnitView() {
        return initiativeQueueUnitView;
    }

    protected void initQueueView(UnitViewOptions o) {
        if (o.getObj() instanceof Structure) {
            return;
        }
        setHoverResponsive(o.isHoverResponsive());
        initiativeQueueUnitView = new QueueView(o, curId);
        initiativeQueueUnitView.setParentView(this);
        initiativeQueueUnitView.setSize(AtbPanel.imageSize, AtbPanel.imageSize);
        initiativeQueueUnitView.setHoverResponsive(isHoverResponsive());
        initiativeQueueUnitView.setMainHero(isMainHero());
    }


    @Override
    public void setBorder(TextureRegion texture) {
        super.setBorder(texture);
        if (initiativeQueueUnitView != null) {
            initiativeQueueUnitView.setBorder(texture);
        }
    }


    @Override
    protected void updateModeImage(String pathToImage) {
        super.updateModeImage(pathToImage);
        initiativeQueueUnitView.updateModeImage(pathToImage);
        modeImage.setPosition(0, 0);
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        if (initiativeQueueUnitView != null)
            initiativeQueueUnitView.setActive(active);
    }

    @Override
    public void setOutline(TextureRegion outline) {
        super.setOutline(outline);
        if (initiativeQueueUnitView != null)
            initiativeQueueUnitView.setOutline(outline);
    }

    public void setOutlinePathSupplier(Supplier<String> pathSupplier) {
        super.setOutlinePathSupplier(pathSupplier);
        this.outlineSupplier = () -> StringMaster.isEmpty(pathSupplier.get()) ? null : TextureCache.getOrCreateR(pathSupplier.get());

        if (initiativeQueueUnitView != null)
            initiativeQueueUnitView.
                    setOutlineSupplier(() -> StringMaster.isEmpty(pathSupplier.get()) ? null :
                            TextureCache.getSizedRegion(AtbPanel.imageSize, pathSupplier.get()));
    }


    @Override
    public void resetHpBar() {
        super.resetHpBar();
        if (initiativeQueueUnitView != null)
            initiativeQueueUnitView.resetHpBar();
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        if (initiativeQueueUnitView != null)
            initiativeQueueUnitView.setUserObject(userObject);
    }

    @Override
    public void setTeamColor(Color teamColor) {
        super.setTeamColor(teamColor);
        if (initiativeQueueUnitView != null)
            initiativeQueueUnitView.setTeamColor(teamColor);
    }


    @Override
    public void setTeamColorBorder(boolean teamColorBorder) {
        super.setTeamColorBorder(teamColorBorder);
        if (initiativeQueueUnitView != null)
            initiativeQueueUnitView.setTeamColorBorder(teamColorBorder);
    }

    public void createHpBar() {
        setHpBar(new HpBar(getUserObject()));
        if (initiativeQueueUnitView != null)
            initiativeQueueUnitView.setHpBar(new HpBar(getUserObject()));
    }

    public void animateHpBarChange() {
        if (!getHpBar().isVisible())
            return;

        getHpBar().animateChange();
        if (initiativeQueueUnitView != null)
            initiativeQueueUnitView.getHpBar().animateChange();
    }

    @Override
    public void setX(float x) {
        super.setX(x);
    }

    @Override
    public void setPosition(float x, float y) {
        if (x == 0 && y == 0)
            if (getParent() instanceof GridPanel) {
                return;
            }
        super.setPosition(x, y);
    }

    public Actor getArrow() {
        return arrow;
    }

    public void validateArrowRotation() {
        int real = getUserObject().getFacing().getDirection().getDegrees() % 360;
        if (Math.abs((arrow.getRotation() + 360 - 4) % 360 - real) > ARROW_ROTATION_OFFSET - 3) {
            main.system.auxiliary.log.LogMaster.verbose(arrow.getRotation()
                    + " raw rotation val, to real => " + real);
            updateRotation(real);

            if (real % 360 != arrowRotation) {
                main.system.auxiliary.log.LogMaster.verbose(
                        getUserObject() + "'s " +
                                arrowRotation
                                + " rotation updated to  " + arrowRotation);
                arrowRotation = real;
            }
        }
    }

    @Override
    protected void alphaFluctuation(float delta) {
        super.alphaFluctuation(delta);
//        if (highlight!=null )
//        if (highlight.getColor().a>0) {
//            alphaFluctuation(highlight, delta);
//        }
    }

    @Override
    public void highlight() {
        super.highlight();
//        screenOverlay=1;
    }

    @Override
    public void highlightOff() {
//        screenOverlay=0;
    }
}
