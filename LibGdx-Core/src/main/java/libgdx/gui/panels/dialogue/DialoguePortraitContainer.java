package libgdx.gui.panels.dialogue;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.ActorDataSource;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueActor;
import libgdx.StyleHolder;
import libgdx.anims.sprite.SpriteX;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.LabelX;
import libgdx.gui.panels.TablePanelX;
import libgdx.screens.CustomSpriteBatch;
import eidolons.content.consts.Sprites;
import main.content.enums.GenericEnums;
import main.system.auxiliary.StringMaster;

import static eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager.isBlotch;

/**
 * Created by JustMe on 11/30/2018.
 */
public class DialoguePortraitContainer extends TablePanelX {
    private SpriteX bgSprite;
    private SpriteX overlaySprite;
    //    private final ValueContainer trepidation;
//    private final ValueContainer esteem;
//    private final ValueContainer affection;
    FadeImageContainer portrait;
    LabelX nameLabel;
    private boolean init;

    public DialoguePortraitContainer() {
        super(250, DialogueView.HEIGHT);
//        TablePanelX teaInfo = new TablePanelX();
//        TextureRegion region = TextureCache.getOrCreateR(
//         ImageManager.getValueIconPath(MACRO_PARAMS.TREPIDATION));
//        LabelStyle style = StyleHolder.getHqLabelStyle(18);
//        teaInfo.add(trepidation = new ValueContainer(style, region, "", ""));
//        region = TextureCache.getOrCreateR(
//         ImageManager.getValueIconPath(MACRO_PARAMS.ESTEEM));
//        teaInfo.add(esteem = new ValueContainer(style, region, "", ""));
//        region = TextureCache.getOrCreateR(
//         ImageManager.getValueIconPath(MACRO_PARAMS.AFFECTION));
//        teaInfo.add(affection = new ValueContainer(style, region, "", ""));

//        addActor(overlaySprite = new SpriteX(Sprites.INK_BLOTCH));
//        bgSprite.setBlending(SuperActor.BLENDING.SCREEN);

//        Texture background = TiledNinePatchGenerator.getOrCreateNinePatch(NINE_PATCH.LIGHT, BACKGROUND_NINE_PATCH.PATTERN,
//        ImageManager.LARGE_ICON_WIDTH,  ImageManager.LARGE_ICON_HEIGHT);
//        setBackground(new TextureRegionDrawable(new TextureRegion(background)));

//        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
//        getCell(nameLabel).setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());

    }
    public void init(){
        init=true;
        if (isBlotch()) {
            addActor(bgSprite = new SpriteX(Sprites.INK_BLOTCH));
            bgSprite.setBlending(GenericEnums.BLENDING.INVERT_SCREEN);
        }
        add(nameLabel = new LabelX()).row();
        nameLabel.setStyle(StyleHolder.getHqLabelStyle(20));
//        add(teaInfo ).row();
        add(portrait = new FadeImageContainer(){
            @Override
            public void draw(Batch batch, float parentAlpha) {
                if (batch instanceof CustomSpriteBatch) {
                    ((CustomSpriteBatch) batch).setBlending(GenericEnums.BLENDING.SCREEN);
                }
                super.draw(batch, parentAlpha);
                if (batch instanceof CustomSpriteBatch) {
                    ((CustomSpriteBatch) batch).resetBlending( );
                }
            }
        }).row();
        main.system.auxiliary.log.LogMaster.devLog("PORTRAIT INIT " +this);
    }



    @Override
    public void act(float delta) {
        if (!init){
            init();
        }
        super.act(delta);
        //TODO notnull
        bgSprite.setFps(8);
        bgSprite.getSprite().centerOnParent(this);
        bgSprite.setY(-220);
        bgSprite.setX(-275);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!init){
            init();
        }
        bgSprite.getColor().a = parentAlpha;
        super.draw(batch, parentAlpha);
    }

    @Override
    public void updateAct(float delta) {
//        debugAll();
        if (!init){
            init();
        }
        super.updateAct(delta);
        ActorDataSource dataSource = (ActorDataSource) getUserObject();
        if (dataSource == null) {
            fadeOut();
            return;
        }
        DialogueActor actor = dataSource.getActor();

//        actor.isSpeaker().if
        //animate

        setImage(StringMaster.getAppendedImageFile(dataSource.getActorImage(),
                dataSource.getImageSuffix()));
        nameLabel.setText(dataSource.getActorName());
//        esteem.setValueText(actor.getEsteem());
    }

    public void setImage(String appendedImageFile) {
        if (!init){
            init();
        }
        portrait.setImage(appendedImageFile);
        pack();
    }
}
