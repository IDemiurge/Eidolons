package eidolons.system.options;

import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster.OPTIONS_GROUP;
import eidolons.system.options.SoundOptions.SOUND_OPTION;
import eidolons.system.options.SystemOptions.SYSTEM_OPTION;

import java.util.Map;

/**
 * Created by JustMe on 11/13/2018.
 */
public class SystemAnalyzer {
    private static float memoryLevel;
    private static int CPUs;
    private static float pcLevel;
    private static float cpuLevel;

    public static void analyze() {
        long memory = Runtime.getRuntime().maxMemory();
        float level = 0.9f;
        if (memory > 3 * (1024e3))
            level = 3f;
        else if (memory > 2 * (1024e3))
            level = 2f;
        else if (memory > 1.5f * (1024e3))
            level = 1.5f;
        else if (memory > 1.25f * (1024e3))
            level = 1.25f;
        else if (memory > 1.0f * (1024e3))
            level = 1.0f;
        setMemoryLevel(level);
        CPUs = Runtime.getRuntime().availableProcessors();
        pcLevel = Math.min(CPUs, memoryLevel);
    }

    public static void adjustForRAM(Map<OPTIONS_GROUP, Options> defaults) {
        Options graphics = defaults.get(OPTIONS_GROUP.GRAPHICS);
        Options system = defaults.get(OPTIONS_GROUP.SYSTEM);
        Options animation = defaults.get(OPTIONS_GROUP.ANIMATION);
        Options gameplay = defaults.get(OPTIONS_GROUP.GAMEPLAY);
        Options sound = defaults.get(OPTIONS_GROUP.SOUND);

        if (pcLevel <= 1.0) {
            //            we are doomed 
        } else if (pcLevel <= 1.25) {
          system.setValue(SYSTEM_OPTION.LOG_TO_FILE, false);
          system.setValue(SYSTEM_OPTION.LOGGING, false);
          system.setValue(SYSTEM_OPTION.PRECONSTRUCT, false);
          
          animation.setValue(ANIMATION_OPTION.WEAPON_3D_ANIMS_OFF, true);
            animation.setValue(ANIMATION_OPTION.CAST_ANIMATIONS, false);
            animation.setValue(ANIMATION_OPTION.AFTER_EFFECTS_ANIMATIONS, false);
            animation.setValue(ANIMATION_OPTION.PRECAST_ANIMATIONS, false);
            animation.setValue(ANIMATION_OPTION.PARALLEL_DRAWING, false);
            
            sound.setValue(SOUND_OPTION.MUSIC_OFF, true);
            gameplay.setValue(GAMEPLAY_OPTION.LIMIT_LOG_LENGTH, true);
            
            graphics.setValue(GRAPHIC_OPTION.AMBIENCE, false);
            graphics.setValue(GRAPHIC_OPTION.SHADOW_MAP_OFF, true);
            graphics.setValue(GRAPHIC_OPTION.ADD_SHARDS_NEVER, true);
            graphics.setValue(GRAPHIC_OPTION.UI_VFX, false);
            graphics.setValue(GRAPHIC_OPTION.UI_ATLAS, false);
            graphics.setValue(GRAPHIC_OPTION.FRAMERATE, 45);
        } else if (pcLevel <= 1.5) {
            system.setValue(SYSTEM_OPTION.LOG_TO_FILE, false);
            animation.setValue(ANIMATION_OPTION.WEAPON_3D_ANIMS_OFF, true);
            gameplay.setValue(GAMEPLAY_OPTION.LIMIT_LOG_LENGTH, true);
            
            graphics.setValue(GRAPHIC_OPTION.AMBIENCE_DENSITY, 25);
            graphics.setValue(GRAPHIC_OPTION.ADD_SHARDS_NEVER, true);
            graphics.setValue(GRAPHIC_OPTION.UI_ATLAS, false);
            graphics.setValue(GRAPHIC_OPTION.FRAMERATE, 50);
        } else if (pcLevel <= 2) {
            graphics.setValue(GRAPHIC_OPTION.AMBIENCE_MOVE_SUPPORTED, false);
        } else if (pcLevel >= 3) {
            graphics.setValue(GRAPHIC_OPTION.FULL_ATLAS, true);
        }
    }

    public static float getMemoryLevel() {
        return memoryLevel;
    }

    public static void setMemoryLevel(float memoryLevel) {
        SystemAnalyzer.memoryLevel = memoryLevel;
    }
}
