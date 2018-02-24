package main.libgdx.screens.map.obj;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.game.module.adventure.map.Place;
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

    }



}
