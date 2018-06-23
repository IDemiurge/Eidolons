package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.bf.overlays.HpBar;
import eidolons.libgdx.gui.panels.dc.InitiativePanel;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.texture.TextureCache;
import main.system.auxiliary.StringMaster;

import java.util.function.Supplier;

public class GridUnitView extends GenericGridView {

    protected QueueView initiativeQueueUnitView;

    public GridUnitView(UnitViewOptions o) {
        super(o);
        initQueueView(o);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (mainHero) {
//            Vector2 assumed = GridMaster.getCenteredPos(getUserObject().getCoordinates());
//            if (getActionsOfClass(MoveByActionLimited.class).size > 0) {
                //set pos, don't draw
//                Vector2 pos = localToStageCoordinates(new Vector2(getX(), getY()));
//                if (pos.dst(assumed)
//                 > 128) {
//                    setPosition(assumed.x, assumed.y);
//                    return;
//                }
            } else {
//               Vector2 pos = localToStageCoordinates(new Vector2(getX(), getY()));
//                if (pos.dst(assumed)
//                 > 256) {
//                    main.system.auxiliary.log.LogMaster.log(1,"PC VIEW IS MISPLACED AT " +pos);
//                    ExceptionMaster.printStackTrace(new RuntimeException("PC VIEW IS MISPLACED AT " +pos));
//                    getParent(). setTransform(true);
//                    pos = stageToLocalCoordinates(new Vector2(assumed.x , assumed.y ));
//                    setPosition(pos.x, pos.y);
//                    return;
//                }
//            }

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
    public void act(float delta) {
        super.act(delta);
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

    public Image getArrow() {
        return arrow;
    }

    public void validateArrowRotation() {
        if (arrow.getRotation()!=arrowRotation){
             updateRotation(arrowRotation-ARROW_ROTATION_OFFSET);
        }
    }
}
