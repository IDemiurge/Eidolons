package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueActor;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.TiledNinePatchGenerator.BACKGROUND_NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.texture.TextureCache;
import main.content.values.parameters.MACRO_PARAMS;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 11/30/2018.
 */
public class DialoguePortraitContainer extends TablePanelX {
    private final ValueContainer trepidation;
    private final ValueContainer esteem;
    private final ValueContainer affection;
    FadeImageContainer portrait;
    LabelX nameLabel;

    public DialoguePortraitContainer() {
        TablePanelX teaInfo = new TablePanelX();
        TextureRegion region = TextureCache.getOrCreateR(
         ImageManager.getValueIconPath(MACRO_PARAMS.TREPIDATION));
        LabelStyle style = StyleHolder.getHqLabelStyle(18);
        teaInfo.add(trepidation = new ValueContainer(style, region, "", ""));
        region = TextureCache.getOrCreateR(
         ImageManager.getValueIconPath(MACRO_PARAMS.ESTEEM));
        teaInfo.add(esteem = new ValueContainer(style, region, "", ""));
        region = TextureCache.getOrCreateR(
         ImageManager.getValueIconPath(MACRO_PARAMS.AFFECTION));
        teaInfo.add(affection = new ValueContainer(style, region, "", ""));

        add(nameLabel = new LabelX()).row();
        add(teaInfo ).row();
        add(portrait = new FadeImageContainer()).row();

        Texture background = TiledNinePatchGenerator.getOrCreateNinePatch(NINE_PATCH.LIGHT, BACKGROUND_NINE_PATCH.PATTERN,
         250, 400);
        setBackground(new TextureRegionDrawable(new TextureRegion(background)));
    }


    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        ActorDataSource dataSource= (ActorDataSource) getUserObject();
        DialogueActor actor = dataSource.getActor();

        setImage(StringMaster.getAppendedImageFile(dataSource.getActorImage(),
         dataSource.getImageSuffix()));
//        esteem.setValueText(actor.getEsteem());
    }

    public void setImage(String appendedImageFile) {
        portrait.setImage(appendedImageFile);
    }
}
