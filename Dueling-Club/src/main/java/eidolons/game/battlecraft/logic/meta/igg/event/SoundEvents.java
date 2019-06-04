package eidolons.game.battlecraft.logic.meta.igg.event;

import eidolons.game.core.Eidolons;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicMaster;
import main.data.filesys.PathFinder;
import main.game.logic.event.Event;

public class SoundEvents {
    //TODO local for Source?
    public static void checkEventSound(Event event) {

        if (event.getType() instanceof Event.STANDARD_EVENT_TYPE) {
            switch (((Event.STANDARD_EVENT_TYPE) event.getType())) {
                case UNIT_HAS_BEEN_DEALT_SPELL_DAMAGE:
                case UNIT_HAS_BEEN_DEALT_PHYSICAL_DAMAGE:
                    DC_SoundMaster.playDamageSound(event.getRef().getDamageType());
                    return;
            }
        }
        String sound = getSoundForEvent(event);
        if (sound != null) {
            DC_SoundMaster.playRandomSoundVariant(sound, false);
        }
    }

    public static String getSoundForEvent(Event event) {


        if (event.getType() instanceof Event.STANDARD_EVENT_TYPE) {
            switch (((Event.STANDARD_EVENT_TYPE) event.getType())) {

                case VICTORY:
                    return MusicMaster.MUSIC_MOMENT.VICTORY.getCorePath();
                case DEFEAT:
                    return MusicMaster.MUSIC_MOMENT.GAMEOVER.getCorePath();
                    case TOWN_ENTERED:
                    return MusicMaster.MUSIC_MOMENT.TOWN.getCorePath();
                case SECRET_FOUND:
                    return MusicMaster.MUSIC_MOMENT.SECRET.getCorePath();

                case ATTACK_MISSED:

                    DC_SoundMaster.playRandomSoundVariant(PathFinder.getSoundsetsPath()
                            +"combat/miss/", true);
                    break;

                case UNIT_HAS_BEEN_KILLED:
                    if (event.getRef().getTargetObj() != Eidolons.MAIN_HERO) {
                        DC_SoundMaster.playRandomSoundVariant(PathFinder.getSoundsetsPath()
                        +"combat/fall/", true);
                        break;
                    }
                    return MusicMaster.MUSIC_MOMENT.DEATH.getCorePath();

                case UNIT_HAS_RECOVERED_FROM_UNCONSCIOUSNESS:
                    if (event.getRef().getSourceObj() != Eidolons.MAIN_HERO) {
                        break;
                    }
                    return MusicMaster.MUSIC_MOMENT.RISE.getCorePath();

                case UNIT_HAS_FALLEN_UNCONSCIOUS:
                    if (event.getRef().getSourceObj() != Eidolons.MAIN_HERO) {
                        DC_SoundMaster.playRandomSoundVariant(PathFinder.getSoundsetsPath()
                                +"combat/fall/", true);
                        break;
                    }
                    return MusicMaster.MUSIC_MOMENT.FALL.getCorePath();

            }
        }
        return null;
    }
}
