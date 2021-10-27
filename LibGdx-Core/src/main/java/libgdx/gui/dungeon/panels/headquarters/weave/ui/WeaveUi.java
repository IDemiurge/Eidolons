package libgdx.gui.dungeon.panels.headquarters.weave.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import libgdx.GdxMaster;
import libgdx.TiledNinePatchGenerator;
import libgdx.bf.generic.ImageContainer;
import libgdx.gui.dungeon.panels.headquarters.weave.WeaveMaster;
import libgdx.gui.dungeon.panels.headquarters.weave.WeaveSpace.WEAVE_VIEW_MODE;
import libgdx.stage.GuiStage;
import main.entity.Entity;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 6/4/2018.
 */
public class WeaveUi extends GuiStage {
    WeaveButtonPanel buttonPanel;
    WeaveHeroPreview heroPreview;
    private VisSelectBox<String> viewModeBox;

    public WeaveUi(Viewport viewport, Batch batch) {
        super(viewport, batch);
        ImageContainer frame;
        addActor(frame = new ImageContainer(new Image(
         TiledNinePatchGenerator.getOrCreateNinePatch(
          TiledNinePatchGenerator.NINE_PATCH.FRAME, TiledNinePatchGenerator.BACKGROUND_NINE_PATCH.TRANSPARENT,
          GdxMaster.getWidth()*11/10, GdxMaster.getHeight()*11/10) )));

        addActor(buttonPanel = new WeaveButtonPanel());
        buttonPanel.setPosition(GdxMaster.centerWidth(buttonPanel),
         50 );
//        addActor(heroPreview = new WeaveHeroPreview());
//        addActor(viewModeBox = createViewModeBox());
//        viewModeBox.setPosition(GdxMaster.right(viewModeBox),
//         GdxMaster.top(viewModeBox)-100 );
       init();

    }

    @Override
    protected void init() {
        initGameMenu();
        initTooltipsAndMisc();
    }

    /*
        tooltips

         */
    public VisSelectBox<String> createViewModeBox() {
        String[] strings  = Arrays.stream(WEAVE_VIEW_MODE.values())
         .map(mode -> StringMaster.format(mode.name()))
         .collect(Collectors.toList())
         .toArray(new String[WEAVE_VIEW_MODE.values().length]);
        String selected = StringMaster.format(
         WEAVE_VIEW_MODE.DEFAULT.name());

        final VisSelectBox<String> selectBox = new VisSelectBox<>();
        selectBox.setItems(strings);
        selectBox.setSelected(selected);
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                WEAVE_VIEW_MODE mode = new EnumMaster<WEAVE_VIEW_MODE>().retrieveEnumConst(WEAVE_VIEW_MODE.class,
                 selectBox.getSelected());
                WeaveMaster.viewModeChanged(mode);
            }
        });
        return selectBox;
    }
    public void setDraggedEntity(Entity draggedEntity) {

    }
}
