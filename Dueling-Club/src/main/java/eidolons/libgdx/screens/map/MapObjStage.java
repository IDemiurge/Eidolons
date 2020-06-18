package eidolons.libgdx.screens.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.screens.map.editor.EditorControlPanel.MAP_EDITOR_MOUSE_MODE;
import eidolons.libgdx.screens.map.editor.EditorManager;
import eidolons.libgdx.screens.map.obj.*;
import eidolons.macro.entity.party.MacroParty;
import eidolons.macro.map.Place;
import eidolons.macro.map.travel.MapWanderAi;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.launch.Flags;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static main.system.MapEvent.*;

/**
 * Created by JustMe on 3/11/2018.
 */
public class MapObjStage extends Stage {
    private final MapWanderAi wanderAi;
    private MacroParty mainParty;
    private PartyActor mainPartyActor;
    private final Group pointsGroup = new Group();
    private final List<PlaceActor> places = new ArrayList<>();
    private final List<PartyActor> parties = new ArrayList<>();


    public MapObjStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
        addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 1)
                    if (mainParty != null)
                        mainPartyActor.moveTo(x, y);
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        bindEvents();
        wanderAi = new MapWanderAi(this);
    }

    public Group getPointsGroup() {
        return pointsGroup;
    }

    public MapWanderAi getWanderAi() {
        return wanderAi;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        wanderAi.act(delta);
        if (Flags.isMapEditor())
            pointsGroup.setVisible(EditorManager.getMode() == MAP_EDITOR_MOUSE_MODE.POINT);
        if (!Flags.isMapEditor())
            for (PlaceActor place : places) {
                switch (place.getPlace().getInfoLevel()) {
                    case VISIBLE:
                    case KNOWN:
                        if (place.getColor().a == 0) {
                            ActionMaster.addFadeInAction(place, 0.84f);
                        }
                        break;
                    case CONCEALED:
                    case UNKNOWN:
                    case INVISIBLE:
                        if (place.getColor().a > 0) {
                            ActionMaster.addFadeOutAction(place, 0.84f);
                        }
                        break;
                }
            }
        resetZIndices();
    }

    private void resetZIndices() {
        //sort by x:y?

        Actor hovered = null;
        try {
            places.sort(getPlacesSorter());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        for (PlaceActor sub : places) {
            sub.setZIndex(Integer.MAX_VALUE);
            if (sub.isHovered())
                hovered = sub;
        }
        if (hovered != null)
            hovered.setZIndex(Integer.MAX_VALUE);
        hovered = mainPartyActor;
        for (PartyActor sub : parties) {
            sub.setZIndex(Integer.MAX_VALUE);
            if (sub.isHovered())
                hovered = sub;
        }
        if (hovered != null)
            hovered.setZIndex(Integer.MAX_VALUE);
    }

    private Comparator<? super PlaceActor> getPlacesSorter() {
        return new Comparator<PlaceActor>() {
            @Override
            public int compare(PlaceActor o1, PlaceActor o2) {
                if (o1.getY() <= o2.getY())
                    return 1;
                if (o1.getX() <= o2.getX())
                    return 1;
                return -1;
            }
        };
    }

    protected void bindEvents() {
        GuiEventManager.bind(CREATE_PARTY, param -> {
            MacroParty party = (MacroParty) param.get();
            if (party == null) {
                return;
            }
            PartyActor partyActor = PartyActorFactory.getParty(party);
            addActor(partyActor);
            if (party.isMine()) {
                setMainParty(party);
                setMainPartyActor(partyActor);
                MapScreen.getInstance().getGuiStage().setMainPartyMarker(
                        PartyActorFactory.getParty(party));
                try {
                    MapScreen.getInstance().centerCamera();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
            parties.add(partyActor);
            wanderAi.update();
        });
        GuiEventManager.bind(CREATE_PLACE, param -> {
            Place place = (Place) param.get();
            PlaceActor placeActor = PlaceActorFactory.getPlace(place);
            addActor(placeActor);
            places.add(placeActor);
        });
        GuiEventManager.bind(REMOVE_MAP_OBJ, param -> {
            MapActor actor = (MapActor) param.get();
            actor.remove();
            wanderAi.update();
        });
    }

    public List<PlaceActor> getPlaces() {
        return places;
    }

    public List<PartyActor> getParties() {
        return parties;
    }

    public MacroParty getMainParty() {
        return mainParty;
    }

    public void setMainParty(MacroParty mainParty) {
        this.mainParty = mainParty;
    }

    public PartyActor getMainPartyActor() {
        return mainPartyActor;
    }

    public void setMainPartyActor(PartyActor mainPartyActor) {
        this.mainPartyActor = mainPartyActor;
    }

    public void removeClosest(int x, int y) {
        float minDistance = Float.MAX_VALUE;
        Actor actor = null;
        for (Actor sub : getRoot().getChildren()) {
            if (sub instanceof EmitterActor) {
                float distance = new Vector2(x, y).dst(new Vector2(sub.getX(), sub.getY()));
                if (distance < minDistance) {
                    minDistance = distance;
                    actor = sub;
                }
            }
        }
        GuiEventManager.trigger(MapEvent.REMOVE_MAP_OBJ, actor);
    }


    public void removeLast() {
        GuiEventManager.trigger(MapEvent.REMOVE_MAP_OBJ, getRoot().getChildren().peek());
    }
}
