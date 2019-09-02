package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueActor;
import main.system.PathUtils;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 12/2/2018.
 */
public class ActorDataSource {

    String actorName;
    String actorImage;
    String imageSuffix;
    private DialogueActor actor;

    public ActorDataSource(String actorName, String actorImage, String imageSuffix) {
        this.actorName = actorName;
        this.actorImage = actorImage;
        this.imageSuffix = imageSuffix;
    }

    public ActorDataSource(DialogueActor actor) {
        this.actor = actor;
        if (actor == null) {
            return;
        }
        actorName = actor.getName();
        actorImage = actor.getImagePath();
        if (actor.checkSingleProp(PROPS.ACTOR_TYPE, "SECONDARY")) {
            imageSuffix = ImageManager.FULL;
        } else {
//          TODO   if (DialoguePortraitContainer.isBlotch())
//            {
//                actorImage = ImageManager.getBlotchPath(StringMaster.cropFormat(PathUtils.getLastPathSegment(actorImage)));
//                imageSuffix="";
//            }
//            else
                imageSuffix = ImageManager.LARGE;
        }
//        actorImage = actor.getProperty()
    }

    public String getActorName() {
        return actorName;
    }

    public String getActorImage() {
        return actorImage;
    }

    public String getImageSuffix() {
        return imageSuffix;
    }

    public DialogueActor getActor() {
        return actor;
    }
}
