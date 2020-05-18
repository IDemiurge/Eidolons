package eidolons.game.netherflame.main.soul;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.SimpleAnim;
import eidolons.libgdx.anims.actions.FloatActionLimited;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.ScissorMaster;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.shaders.ShaderDrawer;
import main.content.enums.GenericEnums;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class SoulforceBar extends GroupX {

    FloatActionLimited floatAction = (FloatActionLimited) ActionMaster.getAction(FloatActionLimited.class);
    boolean labelDisplayed = true;
    float displayedPerc = 1f;
    private Float perc = 1f;
    private Float previousPerc = 0f;

    SpriteX barSprite;
    SimpleAnim anim;
    private float innerWidth;
    private float height;

    private float max = 100f;
    private float value= 0;
    private FadeImageContainer barBg;
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
        previousPerc = perc;
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
        return new Integer( (int)value/10)+"/"+( (int)max/10);
    }
    public void init() {
        GuiEventManager.bind(GuiEventType.SOULFORCE_RESET , p->
        {
            EidolonLord lord = (EidolonLord) p.get();
            max = lord.getSoulforceMax();
            set(lord.getSoulforce());
        });

        addAction(floatAction);
        floatAction.setTarget(this);

        addActor(barBg = new FadeImageContainer("ui/components/dc/soulforce/bar frame.png"));
        addActor(barBgSprite = new SpriteX("sprites/ui/soulforce bar bg.txt"){
            @Override
            public void draw(Batch batch, float parentAlpha) {
                if (parentAlpha!= ShaderDrawer.SUPER_DRAW) {
                    ScissorMaster.drawInRectangle(barBgSprite,batch, innerWidth *   displayedPerc , getY(),
                            innerWidth *   (1-displayedPerc)   , height);
                } else
                    super.draw(batch, parentAlpha);
            }

        });
        addActor(barSprite = new SpriteX("sprites/ui/soulforce bar.txt"){
            @Override
            public void draw(Batch batch, float parentAlpha) {
                if (parentAlpha!= ShaderDrawer.SUPER_DRAW) {
                    ScissorMaster.drawInRectangle(barSprite,batch, getX(), getY(), innerWidth * Math.min(1, displayedPerc) * getScaleX(), height);
                } else
                super.draw(batch, parentAlpha);
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
