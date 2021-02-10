package eidolons.game.netherflame.boss.anims._2d;

import eidolons.game.netherflame.boss.anims.BossAnimHandler;
import eidolons.game.netherflame.boss.anims.BossAnims;
import eidolons.game.netherflame.boss.anims.old.BossPart;
import eidolons.game.netherflame.boss.logic.BossCycle;
import main.game.logic.event.Event;

import java.util.Map;
/*
default idle's are NULL
animations are single Weapon3dAnim's
if those are turned off, we can do a floating text with Action's name

custom impl - for Storm, we do NOT apply 2d simplification to ORB
    use GRID ANIMS - colorize, scale, etc etc
    use multiple variants of portrait?
    displace A LOT

 */
public class BossAnim2dHandler implements BossAnimHandler {

    public Map<String, String> fullToSmallMap;

    @Override
    public void animate(BossPart part, BossAnims.BOSS_ANIM animType) {

//        createWeaponAnim(part, animType);
    }

    @Override
    public void handleEvent(Event event) {

    }

    @Override
    public void toggleActive(BossCycle.BOSS_TYPE type, boolean active) {

    }




    /*
    load calc - 2d boss fight

    vfx for spells
    1-2 anims for attacks (_from perspective)
    special anim for ultimate
    ++ use video?
    idea: use rendered shots of full anims with lighting as "cards"
    otherwise:
    colorize, shader, shakes, short => rendered scene







    load calc - full 3d boss fight
    weapons for all 4(+) eidolons (*could cheat a little by letting only 2 be chosen at once!)
    => at least 5-6 weapons in full!
    note: hidden inv weapons would have to be loaded in some smart way!
    idea: only use to animations for this? splitting atlases...

    boss - full 3d:     3-4 distinct parts, 20-30% reuse
    2-3 idle/channeling;         all parts =>   10
    1-2 single-attack;          half parts =>   5
    2-3 spell anims;            all parts =>    10
    1-2 global anims (spikes, ..; all parts =>  6
    1 ultimate anim (crush, ..; all parts =>    5
    1 appearance anim;          all parts =>    5
    2-3 hit anims;              all parts =>    8
    [optional] 1 disappear/death; all parts =>  5

    total: 40-45

    *each animation will be equivalent to 1/10 of a weapon anim

    >>> so far, by xp, on good pc, 10 weapon anims is a reasonable limit...
    thus, it is barely feasible on a good pc? before we count the rest here!

    an ultra-2d variant would cut 3d anims from weapons and make it real lite tho

    cinematic
    -- at least 2-3 lite fullscreen fx
    -- 1 particle fx

    vfx
    -- sf
    -- lots of spells

    other
    --     special long ost

    > mostly we're concerned with
    -- loading time (assets were loading for 5-10 mins for intro...
    -- vram (already above 1gb?
    -- performance (mark: on this asus, game was lagging w/o any 3d bosses, w/o any action,
    just passively due to vfx/whatnot
     */

}
