package eidolons.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class BaseSlotPanel extends TablePanel {
    protected final int imageSize;
    protected Map<PagesMod, TablePanel> modTableMap = new HashMap<>();

    protected PagesMod activePage = PagesMod.NONE;

    public BaseSlotPanel(int imageSize) {
        this.imageSize = imageSize;
        left().bottom();
    }

    @Override
    public void clear() {
        super.clear();
        modTableMap.clear();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        PagesMod mod = PagesMod.NONE;

        PagesMod[] pagesMods = PagesMod.getValues();
        for (int i = 0, pagesModsLength = pagesMods.length; i < pagesModsLength; i++) {
            PagesMod pagesMod = pagesMods[i];
            if (Gdx.input.isKeyPressed(pagesMod.getKeyCode())) {
                mod = pagesMod;
                break;
            }
        }
        if (mod != activePage) {
            if (modTableMap.containsKey(mod)) {
                setActivePage(mod);
            }
        }
    }

    protected void initContainer(List<ActionValueContainer> sources, String emptyImagePath) {
        int pagesCount = sources.size() / getPageSize();

        if (sources.size() % getPageSize() > 0) {
            pagesCount++;
        }

        pagesCount = Math.min(PagesMod.values().length, pagesCount);

        for (int i = 0; i < pagesCount; i++) {
            final int indx = i * getPageSize();
            final int toIndx = Math.min(sources.size(), indx + getPageSize());
            addPage(sources.subList(indx, toIndx), emptyImagePath);
        }

        if (modTableMap.size() == 0) {
            addPage(Collections.EMPTY_LIST, emptyImagePath);
        }

        setActivePage(PagesMod.NONE);
    }

    protected void addValueContainer(TablePanel page, ValueContainer valueContainer,
                                     TextureRegion emptySlotTexture) {
        if (valueContainer == null) {
            valueContainer = new ValueContainer(emptySlotTexture);
        }
        if (imageSize > 0) {
            valueContainer.overrideImageSize(imageSize, imageSize);
            float scale = imageSize / valueContainer.getImageContainer().getActor().getWidth();
            valueContainer.setScale(scale, scale);
        }
        page.add(valueContainer).left().bottom().size(imageSize);

    }

    protected void addPage(List<ActionValueContainer> list, String emptyImagePath) {
        final TablePanel page = initPage(list, emptyImagePath);
        modTableMap.put(PagesMod.values()[modTableMap.size()], page);
        addElement(page).left().bottom();

        page.setVisible(false);
    }

    protected void setActivePage(PagesMod page) {
        TablePanel view = modTableMap.get(activePage);
        if (view == null) {
            if (modTableMap.isEmpty())
            return;
            else
                view = modTableMap.values().iterator().next();
        }
        view.setVisible(false);
        activePage = page;
        modTableMap.get(activePage).setVisible(true);
    }

    protected TablePanel initPage(List<ActionValueContainer> sources, String emptyImagePath) {
        TablePanel page = new TablePanel();
        for (int i = 0; i < getPageSize(); i++) {
            ActionValueContainer valueContainer = null;
            if (sources.size() > i)
                valueContainer = sources.get(i);
            addValueContainer(page, valueContainer, getOrCreateR(emptyImagePath));
        }

        return page;
    }

    protected int getPageSize() {
        return 6;
    }
}
