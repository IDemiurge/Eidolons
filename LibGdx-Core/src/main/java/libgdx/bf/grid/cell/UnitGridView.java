package libgdx.bf.grid.cell;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.explore.behavior.AiBehavior;
import eidolons.game.battlecraft.ai.explore.behavior.AiBehaviorManager;
import eidolons.game.core.game.DC_Game;
import libgdx.GDX;
import libgdx.StyleHolder;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.anims.sprite.SpriteX;
import libgdx.bf.decor.CellDecor;
import libgdx.bf.grid.moving.PlatformController;
import libgdx.bf.overlays.bar.HpBar;
import libgdx.gui.LabelX;
import libgdx.gui.dungeon.panels.dc.topleft.atb.AtbPanel;
import libgdx.gui.dungeon.tooltips.ToolTipManager;
import libgdx.gui.dungeon.tooltips.Tooltip;
import libgdx.screens.batch.CustomSpriteBatch;
import libgdx.shaders.ShaderDrawer;
import libgdx.assets.texture.TextureCache;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.FontMaster.FONT;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UnitGridView extends GenericGridView {

    private final boolean wall;
    protected QueueView initiativeQueueUnitView;
    private LabelX debugInfo;
    private final float scaleResetPeriod = 4;
    private float scaleResetTimer = scaleResetPeriod;

    OverlayView attachedObj;
    private PlatformController platformController;
    List<CellDecor> linkedDecor;


    public List<CellDecor> getLinkedDecor() {
        if (linkedDecor == null) {
            linkedDecor = new ArrayList<>();
        }
        return linkedDecor;
    }

    @Override
    protected boolean isIgnored() {
        return super.isIgnored();
    }

    @Override
    public boolean isWithinCamera() {
        return super.isWithinCamera();
    }

    public UnitGridView(BattleFieldObject bfObj, UnitViewOptions options) {
        super(bfObj, options);
        initQueueView(options);
        this.wall = options.isWall();
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

    public UnitGridView(UnitViewOptions o) {
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
    public void setHovered(boolean hovered) {
        super.setHovered(hovered);
        if (getInitiativeQueueUnitView() != null) {
            getInitiativeQueueUnitView().setHovered(hovered);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        boolean transform = getScaleX() != 1 || getScaleY() != 1;
        if (transform)
            setTransform(transform);
        else
            setTransform(false);
        //TODO gdx quick fix

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
                ActionMasterGdx.addScaleAction(this, 1, 3);
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
        // if (getUserObject().isPlayerCharacter()) {
        //debug...
        // main.system.auxiliary.log.LogMaster.log(1,getParent().getClass().getSimpleName()+ "; " + getActions()+"; "
        //         +getColor().a + " " + getX() + ":"+getY());
        //     ((CustomSpriteBatch) batch).resetBlending();
        //     if (getUserObject().isHidden()) {
        //         return;
        //     }
        // }
        if (((CustomSpriteBatch) batch).getBlending() == null) {
            for (SpriteX sprite : overlaySprites) {
                sprite.setVisible(false);
            }
        } else {
            for (SpriteX sprite : overlaySprites) {
                sprite.setVisible(true);
            }
        }
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
            if (texture == null) {
                initiativeQueueUnitView.highlightOff();
            } else {
                initiativeQueueUnitView.highlight();
            }
            //            initiativeQueueUnitView.setBorder(texture);
        }
    }


    @Override
    public void updateModeImage(String pathToImage) {
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
        this.outlineSupplier = () -> StringMaster.isEmpty(pathSupplier.get()) ? null
                : TextureCache.getRegionUV(pathSupplier.get());

        if (initiativeQueueUnitView != null) //TODO atlas revamp
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
            if (getParent()==null) {
                return;
            }
        super.setPosition(x, y);
    }

    public PlatformController getPlatformController() {
        return platformController;
    }

    public void setPlatformController(PlatformController platformController) {
        this.platformController = platformController;
    }

    public boolean isWall() {
        return wall;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    public void addLinkedDecor(CellDecor decor) {
        getLinkedDecor().add(decor);
    }

    @Override
    public boolean isVisible() {
        if (wall) {
            return true;
        }
        return super.isVisible();
    }
    //    @Override
    //    public void highlightOff() {
    ////        screenOverlay=0;
    //    }
}
