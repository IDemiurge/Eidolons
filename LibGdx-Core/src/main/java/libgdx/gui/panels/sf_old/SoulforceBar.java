package libgdx.gui.panels.sf_old;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.content.consts.GraphicData;
import eidolons.netherflame.lord.EidolonLord;
import libgdx.GdxMaster;
import libgdx.anims.SimpleAnim;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.anims.actions.FloatActionLimited;
import libgdx.anims.sprite.SpriteX;
import libgdx.bf.Fluctuating;
import libgdx.bf.SuperActor;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.ScissorMaster;
import libgdx.screens.CustomSpriteBatch;
import libgdx.shaders.ShaderDrawer;
import eidolons.content.consts.Sprites;
import main.content.enums.GenericEnums;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.math.MathMaster;

public class SoulforceBar extends SuperActor {

    FloatActionLimited floatAction = (FloatActionLimited) ActionMasterGdx.getAction(FloatActionLimited.class);
    boolean labelDisplayed = true;
    float displayedPerc = 1f;
    private Float perc = 1f;

    SpriteX barSprite;
    SimpleAnim anim;
    private float innerWidth;
    private float height;

    private float max = 100f;
    private float value= 0;
    private SpriteX barBgSprite;
    private Fluctuating f;

    public SoulforceBar() {
        init();
    }

    public void set(float value) {
        //special anim on top?

        this.value = value;
            updateAction();
    }

    private void updateAction() {
        Float previousPerc = perc;
        perc = value/max;

        floatAction.reset();
        floatAction.setDuration(2);
        floatAction.setStart(previousPerc);
        floatAction.setEnd(perc);
        floatAction.restart();
        addAction(floatAction);
        floatAction.setTarget(this);
    }

    public String getTooltip() {
        return (int) value / 10 +"/"+( (int)max/10);
    }
    public void init() {
        GuiEventManager.bind(GuiEventType.SOULFORCE_GAINED, p ->
                sfChanged((EidolonLord) p.get(), this.value < value));

        GuiEventManager.bind(GuiEventType.SOULFORCE_LOST , p->
                sfChanged((EidolonLord) p.get(), this.value < value ));

        addAction(floatAction);
        floatAction.setTarget(this);

        FadeImageContainer barBg;
        addActor(barBg = new FadeImageContainer("ui/components/dc/soulforce/bar frame.png"));
        addActor(barBgSprite = new SpriteX(Sprites.SOULFORCE_BAR_BG_WHITE){
            @Override
            public void draw(Batch batch, float parentAlpha) {
                if (parentAlpha!= ShaderDrawer.SUPER_DRAW) {
                    ScissorMaster.drawInRectangle(barBgSprite,batch, innerWidth *   displayedPerc , getY(),
                            innerWidth *   (1-displayedPerc)   , height);
                } else
                    super.draw(batch, parentAlpha);

                ((CustomSpriteBatch) batch).resetBlending();
            }

        });
        addActor(barSprite = new SpriteX(Sprites.SOULFORCE_BAR_WHITE){
            @Override
            public void draw(Batch batch, float parentAlpha) {
                if (parentAlpha!= ShaderDrawer.SUPER_DRAW) {
                    ScissorMaster.drawInRectangle(barSprite,batch, getX(), getY(), innerWidth * Math.min(1, displayedPerc) * getScaleX(), height);
                } else
                super.draw(batch, parentAlpha);
                ((CustomSpriteBatch) batch).resetBlending();
            }

        });
        setSize(barSprite.getWidth(), barSprite.getHeight());
        GdxMaster.center(barBg);
        barSprite.setBlending(GenericEnums.BLENDING.SCREEN);
        barSprite.setFps(12);
        barBgSprite.setBlending(GenericEnums.BLENDING.SCREEN);
        barBgSprite.setFps(12);
//        anim = new SimpleAnim("sprites/ui/soulforce bar anim.txt", () -> {
//        });
        addActor(f = new Fluctuating(GenericEnums.ALPHA_TEMPLATE.SOULFORCE));
        innerWidth = barSprite.getWidth();
        height = barSprite.getHeight();



    }

    private void sfChanged(EidolonLord lord, boolean gain) {
        max = lord.getSoulforceMax();
        float dif = this.value - lord.getSoulforce();
        set(lord.getSoulforce());
        GraphicData data = new GraphicData("");
        data.setValue(GraphicData.GRAPHIC_VALUE.dur, MathMaster.minMax(dif/10, 0.75f, 2));
        data.setValue(GraphicData.GRAPHIC_VALUE.alpha, MathMaster.minMax(dif/10, 0.25f, 1));
        data.setValue(GraphicData.GRAPHIC_VALUE.interpolation,  "fade");
        GuiEventManager.triggerWithParams(GuiEventType.GRID_SCREEN, this, data);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        f.fluctuate(delta);
//        barSprite.getColor().a=(f.getColor().a);
        barBgSprite.getColor().a=(f.getColor().a);

        barSprite.getSprite().centerOnParent(this);
        barBgSprite.getSprite().centerOnParent(this);
        displayedPerc = floatAction.getValue();
    }

}
