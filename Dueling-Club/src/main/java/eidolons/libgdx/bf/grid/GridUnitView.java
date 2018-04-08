package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.libgdx.bf.overlays.HpBar;
import eidolons.libgdx.gui.panels.dc.InitiativePanel;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.ResourceSourceImpl;
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
          TextureCache.getSizedRegion(InitiativePanel.imageSize, pathSupplier.get())) ;
    }


    public void resetHpBar(ResourceSourceImpl resourceSource) {
        super.resetHpBar(resourceSource);
        if (initiativeQueueUnitView != null)
            initiativeQueueUnitView.resetHpBar(
             resourceSource);
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

    public void createHpBar(ResourceSourceImpl resourceSource) {
        setHpBar(new HpBar(resourceSource));
        if (initiativeQueueUnitView != null)
            initiativeQueueUnitView.setHpBar(new HpBar(resourceSource));
    }

    public void animateHpBarChange() {
        if (!getHpBar().isVisible())
            return;

        getHpBar().animateChange();
        if (initiativeQueueUnitView != null)
            initiativeQueueUnitView.getHpBar().animateChange();
    }
}