package main.libgdx.screens.map.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import main.content.PARAMS;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.data.filesys.PathFinder;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.entity.MacroParty;
import main.libgdx.GdxMaster;
import main.libgdx.StyleHolder;
import main.libgdx.gui.NinePatchFactory;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.texture.TextureCache;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.images.ImageManager.STD_IMAGES;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 2/9/2018.
 * //split in two by time panel?
 */
public class MapResourcesPanel extends TablePanel {

    private static final float WIDTH = 128;
    private static final float HEIGHT = 36;
    MacroParty party;
    MAP_RESOURCE[] resourceGroupOne = {
     MAP_RESOURCE.PROVISIONS, MAP_RESOURCE.WATER
    };
    MAP_RESOURCE[] resourceGroupTwo = {
     MAP_RESOURCE.GOLD, MAP_RESOURCE.SOULGEMS
    };
    private List<MapResourcePanel> elements = new ArrayList<>();


    public MapResourcesPanel() {
        updateRequired = true;
        setHeight(GdxMaster.adjustSize(HEIGHT + 4));
        defaults().width(GdxMaster.getWidth() / 3);
        addResGroup(resourceGroupOne);
        add(new Group());
        addResGroup(resourceGroupTwo);
    }

    private static String getImgPath(MAP_RESOURCE resource) {
        switch (resource) {
            case PROVISIONS:
                return STD_IMAGES.BAG.getPath();
            case WATER:
                return "UI\\components\\small\\glory.png";
            case SANITY:
                break;
            case GOLD:
                return VISUALS.GOLD.getImgPath();
            case SOULGEMS:
                return VISUALS.XP.getImgPath();
        }
        return PathFinder.getMacroUiPath() + "values"
         + StringMaster.getPathSeparator()
         + resource.toString();
    }

    private static PARAMETER getParam(MAP_RESOURCE resource) {
        switch (resource) {
            case PROVISIONS:
                return MACRO_PARAMS.C_PROVISIONS;
            case WATER:
                return MACRO_PARAMS.C_WATER;
            case SANITY:
                break;
            case GOLD:
                return PARAMS.GOLD;
            case SOULGEMS:
                return MACRO_PARAMS.C_SOULGEMS;
        }
        return null;
    }

    private void addResGroup(MAP_RESOURCE[] resourceGroup) {
        HorizontalFlowGroup group = new HorizontalFlowGroup(5);
        group.setWidth(GdxMaster.adjustSize(WIDTH));
        for (MAP_RESOURCE sub : resourceGroup) {
            MapResourcePanel res = new MapResourcePanel(sub);
            group.addActor(res);
            elements.add(res);
        }

        add(group).center();
    }

    @Override
    public void updateAct(float delta) {
        if (party == null) {
            if (MacroGame.game == null) return;
            party = MacroGame.game.getPlayerParty();
        }
        super.updateAct(delta);
        for (TablePanel sub : elements) {
            sub.setUpdateRequired(true);
        }
    }

    public enum MAP_RESOURCE {
        PROVISIONS, WATER, SANITY, GOLD, SOULGEMS,;
    }

    public class MapResourcePanel extends TablePanel {
        MAP_RESOURCE resource;
        MacroParty party;

        public MapResourcePanel(MAP_RESOURCE resource) {
            setSize(GdxMaster.adjustSize(WIDTH), GdxMaster.adjustSize(HEIGHT));
            this.resource = resource;
//        container.overrideImageSize();
            setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));
        }

        @Override
        public void updateAct(float delta) {
            super.updateAct(delta);
            clearChildren();
            String val = RandomWizard.getRandomInt(100) + "";// party.getParam(getParam(resource));
            TextureRegion tex = TextureCache.getOrCreateR(getImgPath(resource));
            ValueContainer container = new ValueContainer(tex, val);
            container.defaults().space(20).width(40);
            container.setStyle(StyleHolder.getSizedLabelStyle(FONT.MAIN, 18));
            addActor(container);
        }
    }
}
