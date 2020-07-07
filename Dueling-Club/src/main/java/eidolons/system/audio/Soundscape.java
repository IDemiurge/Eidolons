package eidolons.system.audio;

import eidolons.game.module.cinematic.Cinematics;
import main.content.CONTENT_CONSTS.SOUNDSET;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.MapMaster;
import main.system.datatypes.WeightMap;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.SOUNDS;

import java.util.List;
import java.util.Map;

import static eidolons.system.audio.Soundscape.SOUNDSCAPE_SOUND.*;

public class Soundscape {

    static{
        SOUNDSCAPE.NETHER.weightMapCustom
                .chain(thunder, 20)
                .chain(whispers, 4)
                .chain(rocks, 21)
                .chain(crack, 15)
                .chain(growl, 1)
                .chain(chant_dark, 2)
                .chain(chant_evil, 2)
//                .chain(scream, 10)
//                .chain(splash, 10)
//                .chain(mythic_beast, 10)
        ;
//        SOUNDSCAPE.NETHER.weightMapUnit
//                .putChain(wraith, 2)
//                .putChain(ironman, 10)
//                .putChain(sorcerer, 10)
//                .putChain(warlock, 2)
//        ;
//        SOUNDSCAPE.NETHER.weightMapSoundType
////                .putChain(IDLE, 10)
//                .putChain(ALERT, 6)
//                .putChain(SPOT, 3)
//                .putChain(DEATH, 3)
//        ;

    }
    Map<SOUNDSCAPE, Float> map;
    Map<SOUNDSCAPE, Float> periodMap;

    public enum SOUNDSCAPE_SOUND {
        rocks,
        crack,
        growl,
        chant_dark,
        chant_evil,
        whispers,
        thunder,
        woosh,
        splash,
        howl,
        scream,
        stones,
        mythic_beast,

    }
    public enum SOUNDSCAPE {
        NETHER,
        WATERS,
        DEEP,
        HELL,
        HORROR,
        ASTRAL,
        ;
        int customChance=75;
        WeightMap<String> weightMapCustom=new WeightMap<>();
        WeightMap<SOUNDSET> weightMapUnit=new WeightMap<>(SOUNDSET.class);
        WeightMap<SOUNDS> weightMapSoundType=new WeightMap<>(SOUNDS.class);
        int minPause=20000;
        float chancePerSecond=0.003f;
    }

    public Soundscape() {
        GuiEventManager.bind(GuiEventType.SET_SOUNDSCAPE_VOLUME , p-> {
            List list = (List) p.get();
            map.put((SOUNDSCAPE) list.get(0), (Float) list.get(1));
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
        for (SOUNDSCAPE soundscape : map.keySet()) {
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

    private void playSound(SOUNDSCAPE soundscape,  float volumeCoef) {
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
