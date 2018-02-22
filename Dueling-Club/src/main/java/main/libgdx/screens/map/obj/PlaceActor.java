package main.libgdx.screens.map.obj;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.game.module.adventure.map.Place;
import main.libgdx.gui.tooltips.ValueTooltip;
import main.libgdx.screens.map.obj.PlaceActorFactory.PlaceActorParameters;
import main.system.GuiEventManager;
import main.system.MapEvent;

/**
 * Created by JustMe on 2/9/2018.
 */
public class PlaceActor extends MapActor  {
    private Place place;

//    Image preview;

    public PlaceActor(PlaceActorParameters parameters) {
        super(parameters.mainIcon);
        init(parameters);
    }

    private void init(PlaceActorParameters parameters) {
//        preview = new Image(parameters.preview);
        this.place = parameters.place;
        setPosition(parameters.position.x - portrait.getImageWidth()/2,
         parameters.position.y- portrait.getImageHeight()/2);
//        icon.setPosition(GdxMaster.right(emblem), GdxMaster.top(emblem) );
        addListener(new ValueTooltip(parameters.preview,parameters. name).getController());

        addListener(new PlaceListener());
    }


     public  class PlaceListener extends ClickListener {

         @Override
         public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
             //highlight routes
             GuiEventManager.trigger(MapEvent.PLACE_HOVER, place);
             super.enter(event, x, y, pointer, fromActor);
         }

         @Override
         public boolean mouseMoved(InputEvent event, float x, float y) {
             return super.mouseMoved(event, x, y);
         }

         @Override
         public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
             GuiEventManager.trigger(MapEvent.PLACE_HOVER, null );
             //show info on either of the routes ?
             super.exit(event, x, y, pointer, toActor);
         }
     }
}
