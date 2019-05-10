package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.explore.behavior.AiBehavior;
import eidolons.game.battlecraft.ai.explore.behavior.AiBehaviorManager;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GDX;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.bf.overlays.HpBar;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.panels.dc.InitiativePanel;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.libgdx.texture.TextureCache;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;

import java.util.function.Supplier;

public class GridUnitView extends GenericGridView {

    protected QueueView initiativeQueueUnitView;
    private LabelX debugInfo;
    private float scaleResetPeriod = 4;
    private float scaleResetTimer = scaleResetPeriod;

    public GridUnitView(UnitViewOptions o) {
        super(o);
        initQueueView(o);
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

    @Override
    public void act(float delta) {
        super.act(delta);
        if (debugInfo == null) {
            return;
        }
        scaleResetTimer -= delta;
        if (scaleResetTimer <= 0) {
            if (getScaleX() > 1)
                ActorMaster.addScaleAction(this, 1, 3);
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
        super.draw(batch, parentAlpha);
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
        setHoverResponsive(o.isHoverResponsive());
        initiativeQueueUnitView = new QueueView(o, curId);
        initiativeQueueUnitView.setParentView(this);
        initiativeQueueUnitView.setSize(InitiativePanel.imageSize, InitiativePanel.imageSize);
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
        initiativeQueueUnitView.setOutline(outline);
    }

    public void setOutlinePathSupplier(Supplier<String> pathSupplier) {
        super.setOutlinePathSupplier(pathSupplier);
        this.outlineSupplier = () -> StringMaster.isEmpty(pathSupplier.get()) ? null : TextureCache.getOrCreateR(pathSupplier.get());


        initiativeQueueUnitView.
                setOutlineSupplier(() -> StringMaster.isEmpty(pathSupplier.get()) ? null :
                        TextureCache.getSizedRegion(InitiativePanel.imageSize, pathSupplier.get()));
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
            main.system.auxiliary.log.LogMaster.log(1, arrow.getRotation()
                    + " raw val, to  " + real);
            updateRotation(real);

            if (real % 360 != arrowRotation) {
                main.system.auxiliary.log.LogMaster.log(1,
                        getUserObject() + "'s " +
                                arrowRotation
                                + " rotation updated to  " + arrowRotation);
                arrowRotation = real;
            }
        }
    }
}