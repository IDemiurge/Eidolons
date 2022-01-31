package libgdx.audio;

import eidolons.content.consts.VisualEnums.ANIM_PART;
import eidolons.entity.active.ActiveObj;
import eidolons.entity.active.Spell;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.battlecraft.rules.magic.ChannelingRule;
import eidolons.game.core.game.DC_Game;
import eidolons.system.audio.DC_SoundMaster;
import libgdx.anims.Anim;
import libgdx.anims.CompositeAnim;
import libgdx.anims.construct.AnimResourceFinder;
import libgdx.screens.dungeon.DungeonScreen;
import main.content.DC_TYPE;
import main.content.enums.entity.HeroEnums;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.sound.AudioEnums;

public class DC_Playback {
    private static SoundPlayer soundPlayer;
    private   static DC_SoundMaster master;
    private final DungeonScreen screen;

    public DC_Playback(DungeonScreen screen) {
        this.screen = screen;
        soundPlayer = new SoundPlayer(screen);
        master = new DC_SoundMaster(soundPlayer);
    }

    public void doPlayback(float delta) {
        if (soundPlayer == null)
            soundPlayer = new SoundPlayer(screen);
        soundPlayer.doPlayback(delta);
    }
    public static void bindEvents() {
        //TODO ON SEPARATE THREAD!!!!
        GuiEventManager.bind(GuiEventType.ANIMATION_STARTED, p -> {
            Anim anim = (Anim) p.get();
            ActiveObj activeObj = (ActiveObj) anim.getActive();
            try {
                playAnimStartSound(activeObj, anim.getPart());
            } catch (Exception e) {
                //                main.system.ExceptionMaster.printStackTrace(e);
            }
        });
        GuiEventManager.bind(GuiEventType.COMPOSITE_ANIMATION_STARTED, p -> {
            CompositeAnim anim = (CompositeAnim) p.get();
            ActiveObj activeObj = (ActiveObj) anim.getActive();
            try {
                playActionStartSound(activeObj);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        });
    }

    private static void playActionStartSound(ActiveObj activeObj) {
        //TODO
    }

    public static void playAnimStartSound(ActiveObj activeObj, ANIM_PART part) {
        if (activeObj instanceof Spell)
            switch (part) {
                case PRECAST:
                case CAST:
                case RESOLVE:
                case MISSILE:
                case IMPACT:
                case AFTEREFFECT:
                    master.playNow(master.getActionEffectSoundPath((Spell) activeObj, part), 200, 0);
                    return;
            }
        if (activeObj.isMove()) {
            return;
        }
        switch (part) {
            case PRECAST:
                ChannelingRule.playChannelingSound(activeObj, activeObj.getOwnerUnit().getGender() == HeroEnums.GENDER.FEMALE);
                master.getPlayer().playEffectSound(AudioEnums.SOUNDS.PRECAST, activeObj);
                break;
            case CAST:
                master.getPlayer().playEffectSound(AudioEnums.SOUNDS.CAST, activeObj);
                break;
            case RESOLVE:
                master.getPlayer().playEffectSound(AudioEnums.SOUNDS.RESOLVE, activeObj);
                break;
            case MISSILE:
                master.getPlayer().playEffectSound(AudioEnums.SOUNDS.EFFECT, activeObj);
                break;
            case IMPACT:
                master.playImpact(activeObj);
                break;
            case AFTEREFFECT:
                break;
        }
    }

    public static void preconstructEffectSounds() {
        for (ObjType type : DataManager.getTypes(DC_TYPE.SPELLS)) {
            Spell active = new Spell(type, DC_Player.NEUTRAL, DC_Game.game, new Ref());
            for (ANIM_PART part :  ANIM_PART.values()) {
                preconstructSpell(active, part);
            }
        }
    }

    private static void preconstructSpell(Spell spell, ANIM_PART part) {
        String file = AnimResourceFinder.findResourceForSpell(spell,
                part.toString(), "", true,
              master.getSpellSoundPath(), false);
        if (file == null) {
            file = AnimResourceFinder.findResourceForSpell(spell,
                    part.toString(), "", false,
                    master. getSpellSoundPath(), true);
        }
        if (file == null) {
            file = master.getActionEffectSoundPath(spell, part);
        }
        if (file == null) {
            return;
        }
        file = file.replace(PathFinder.getSoundPath().toLowerCase(), "");
        PROPERTY prop = master.getProp(part);
        if (prop == null)
            return;
        spell.getType().setProperty(prop, file);
    }
}
