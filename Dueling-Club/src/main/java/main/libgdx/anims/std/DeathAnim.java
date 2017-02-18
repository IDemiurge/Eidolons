package main.libgdx.anims.std;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import javafx.util.Pair;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.battlefield.Coordinates;
import main.game.logic.event.Event;
import main.libgdx.anims.ActorMaster;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.AnimData.ANIM_VALUES;
import main.libgdx.anims.AnimMaster;
import main.libgdx.texture.TextureManager;
import main.system.EventCallbackParam;
import main.system.GuiEventType;

import java.util.Arrays;
import java.util.List;

import static main.system.GuiEventType.DESTROY_UNIT_MODEL;

/**
 * Created by JustMe on 1/16/2017.
 */
public class DeathAnim extends ActionAnim {
    private static boolean on=true;
    Unit unit;
    DEATH_ANIM template;
    private Image skull;

    public DeathAnim(Event e) {
        super(e.getRef().getObj(KEYS.ACTIVE), getDeathAnimData(e));
       unit = (Unit) e.getRef().getTargetObj();
        template = getTemplate(getActive(), unit);
        duration=2;
    }

    private static AnimData getDeathAnimData(Event e) {
        AnimData  data=new AnimData();
        data.setValue(ANIM_VALUES.PARTICLE_EFFECTS, "impact\\Crimson Death");
        return data;
    }

    public static boolean isOn() {
        if (!AnimMaster.isOn()) return false;
        return on;
    }

    @Override
    public boolean draw(Batch batch) {
        return super.draw(batch);
    }

    @Override
    public List<Pair<GuiEventType, EventCallbackParam>> getEventsOnFinish() {
        return Arrays.asList(new Pair<>( DESTROY_UNIT_MODEL, new EventCallbackParam<>(unit)));
    }

    @Override
    protected Action getAction() {
        return null ;
    }

    @Override
    protected void dispose() {
        super.dispose();
        if (getActor()!=null )
        getActor().remove();
    }

    @Override
    protected void add() {
        if (getActor()==null )return ;
        AnimMaster.getInstance().addActor(getActor());
        getActor().setPosition(getOrigin().x, getOrigin().y);
        AlphaAction action = ActorMaster.addFadeAction(getActor());

        action.setDuration(duration);
    }

    @Override
    public Coordinates getOriginCoordinates() {
        return unit.getCoordinates();
    }

    @Override
    public Actor getActor() {
        if (skull==null ){
            skull = new Image(TextureManager.getOrCreate("UI\\Empty.png")){
                @Override
                public void draw(Batch batch, float parentAlpha) {
                    act(Gdx.graphics.getDeltaTime());
                    super.draw(batch, parentAlpha);
                }
            };
        }
//        return skull;
        return null ;
    }

    private DEATH_ANIM getTemplate(DC_ActiveObj active, Unit unit) {
//        getRef().getEvent().getRef().getDamageType();
        return DEATH_ANIM.FADE;
    }

    @Override
    public void start() {
//        addSfx();
        //skull / grave?
        super.start();
        add();
    }

    public enum DEATH_ANIM {
        FADE, FLASH,
        EXPLODE,
        BURN,
        COLLAPSE,
        ATOMIZE, SHATTER,;
        String spritePath;
    }

}
