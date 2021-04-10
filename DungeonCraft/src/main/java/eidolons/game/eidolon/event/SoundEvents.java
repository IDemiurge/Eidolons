package eidolons.game.eidolon.event;

import eidolons.game.core.Eidolons;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicEnums;
import main.data.filesys.PathFinder;
import main.entity.EntityCheckMaster;
import main.game.logic.event.Event;

public class SoundEvents {
    //TODO local for Source?
    public static void checkEventSound(Event event) {

        if (event.getType() instanceof Event.STANDARD_EVENT_TYPE) {
            switch (((Event.STANDARD_EVENT_TYPE) event.getType())) {
                case UNIT_HAS_BEEN_HIT:
                    DC_SoundMaster.playHitSound(event.getRef().getTargetObj());
                    return;
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
                    return MusicEnums.MUSIC_MOMENT.VICTORY.getCorePath();
                case DEFEAT:
                    return MusicEnums.MUSIC_MOMENT.GAMEOVER.getCorePath();
                case TOWN_ENTERED:
                    return MusicEnums.MUSIC_MOMENT.TOWN.getCorePath();
                case SECRET_FOUND:
                    return MusicEnums.MUSIC_MOMENT.SECRET.getCorePath();

                case ATTACK_MISSED:

                    DC_SoundMaster.playRandomSoundVariant(PathFinder.getSoundsetsPath()
                            + "combat/miss/", true);
                    break;

                case UNIT_HAS_BEEN_KILLED:
                    if (EntityCheckMaster.isOverlaying(event.getRef().getTargetObj()))
                        break;
                    if (event.getRef().getTargetObj() != Eidolons.MAIN_HERO) {
//                        DC_SoundMaster.playRandomSoundVariant(PathFinder.getSoundsetsPath()
//                                + "combat/fall/", true);
                        break;
                    }


                    return MusicEnums.MUSIC_MOMENT.DEATH.getCorePath();

                case UNIT_HAS_RECOVERED_FROM_UNCONSCIOUSNESS:
                    if (event.getRef().getSourceObj() != Eidolons.MAIN_HERO) {
                        break;
                    }
                    return MusicEnums.MUSIC_MOMENT.RISE.getCorePath();

                case UNIT_HAS_FALLEN_UNCONSCIOUS:
                    if (event.getRef().getSourceObj().isDead()) {
                        return null;
                    }
                    if (event.getRef().getSourceObj() != Eidolons.MAIN_HERO) {
                        DC_SoundMaster.playRandomSoundVariant(PathFinder.getSoundsetsPath()
                                + "combat/fall/", true);
                        break;
                    }
                    return MusicEnums.MUSIC_MOMENT.FALL.getCorePath();

            }
        }
        return null;
    }
}
