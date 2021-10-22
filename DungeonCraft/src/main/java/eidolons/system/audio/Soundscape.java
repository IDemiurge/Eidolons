package eidolons.system.audio;

import eidolons.game.exploration.story.cinematic.Cinematics;
import main.content.CONTENT_CONSTS.SOUNDSET;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.MapMaster;
import main.system.sound.AudioEnums;
import main.system.sound.AudioEnums.SOUNDS;
import main.system.sound.SoundMaster;

import java.util.List;
import java.util.Map;

public class Soundscape {

    Map<AudioEnums.SOUNDSCAPE, Float> map;
    Map<AudioEnums.SOUNDSCAPE, Float> periodMap;

    public Soundscape() {
        GuiEventManager.bind(GuiEventType.SET_SOUNDSCAPE_VOLUME , p-> {
            List list = (List) p.get();
            map.put((AudioEnums.SOUNDSCAPE) list.get(0), (Float) list.get(1));
        });
        init();
    }

    private void init() {
        map = new XLinkedMap<>();
        periodMap = new XLinkedMap<>();


    }

    public void act(float delta) {
        if (Cinematics.ON) {
            return;
        }
        for (AudioEnums.SOUNDSCAPE soundscape : map.keySet()) {
            float coef= map.get(soundscape);
            if (coef<=0) {
                continue;
            }
            MapMaster.addToFloatMap(periodMap, soundscape, delta);
            Float period = periodMap.get(soundscape);
            if (period >= soundscape.minPause) {
                int chance = Math.round(soundscape.chancePerSecond * period);
                if (RandomWizard.chance(chance)) {
                    playSound(soundscape,coef);
                    periodMap.put(soundscape, 0f);
                }
            }

        }
    }

    private void playSound(AudioEnums.SOUNDSCAPE soundscape, float volumeCoef) {
        int volume= (int) (RandomWizard.getRandomInt(100)*volumeCoef);
        if (RandomWizard.chance(soundscape.customChance)) {
            String sound = soundscape.weightMapCustom.getRandomByWeight();
            String path = PathFinder.getSoundPath() + "soundscape/" + sound + ".mp3";
            SoundMaster.playRandomSoundVariant(path, true, volume, 0);
//            DC_SoundMaster.play(path, volume, 0);
        } else {
            SOUNDSET soundset = soundscape.weightMapUnit.getRandomByWeight();
            SOUNDS type = soundscape.weightMapSoundType.getRandomByWeight();
            if (type != null) {
                DC_SoundMaster.playEffectSound(type, soundset, volume,0);
            }
        }

    }

}
