package eidolons.libgdx.gui.panels.headquarters.tabs.tree.skill;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import eidolons.libgdx.gui.controls.radial.RadialMenu;
import eidolons.libgdx.gui.controls.radial.RadialValueContainer;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HQ_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.texture.TextureCache;
import main.entity.type.ObjType;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.system.GuiEventType.RADIAL_MENU_CLOSE;

/**
 * Created by JustMe on 5/8/2018.
 */
public class SkillSelectionMenu extends RadialMenu {

    Map<List, List> cache = new HashMap<>();
    private HqHeroDataSource dataSource;

    public SkillSelectionMenu() {
        super();
        GuiEventManager.bind(GuiEventType.SHOW_SKILL_CHOICE, p -> {
            triggered(p);
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    protected void bindEvents() {
        addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return event.getTarget() == SkillSelectionMenu.this
                 || super.mouseMoved(event, x, y);
            }
        });
        GuiEventManager.bind(RADIAL_MENU_CLOSE, obj -> {
            close();
        });
    }

    public void selected(ObjType skill) {
        HqDataMaster.operation(dataSource,
         HQ_OPERATION.NEW_SKILL, skill);
    }

    @Override
    protected Vector2 getInitialPosition() {
        Vector2 pos = super.getInitialPosition();
        setPosition(0,0);
        return stageToLocalCoordinates(pos);
    }
    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        cache.clear();
        dataSource = (HqHeroDataSource) getUserObject();
    }

    @Override
    protected void triggered(EventCallbackParam obj) {
        List<ObjType> available = (List<ObjType>) obj.get();
        List<RadialValueContainer> nodes = getNodes(available);
        init(nodes);
        open();
    }

    private List<RadialValueContainer> getNodes(List<ObjType> available) {
        List<RadialValueContainer> list = cache.get(available);
        if (list == null) {
            list = createNodes(available);
        }
        return list;
    }

    private List<RadialValueContainer> createNodes(List<ObjType> available) {
        List<RadialValueContainer> list = new ArrayList<>();
        for (ObjType type : available) {
            String reason = SkillMaster.getReqReasonForSkill(dataSource.getEntity(), type);
            boolean valid = reason == null;
            TextureRegion region = valid
             ? TextureCache.getOrCreateGrayscaleR(type.getImagePath())
             : TextureCache.getOrCreateR(type.getImagePath());
            RadialValueContainer node = new RadialValueContainer(region, () -> {

                if (valid)
                    selected(type);
                else {
                    //floating text?
                }
            });
            list.add(node);
        }
        return list;
    }

}
