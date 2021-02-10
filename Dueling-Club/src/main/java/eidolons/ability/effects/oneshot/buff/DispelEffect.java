package eidolons.ability.effects.oneshot.buff;

import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.entity.obj.Attachment;
import main.entity.obj.BuffObj;
import main.system.math.Formula;

public class DispelEffect extends MicroEffect implements OneshotEffect {

    private final Formula durationMod;
    private final boolean friendlyFire;
    private final Boolean positiveOnly;

    public DispelEffect(Formula chance, Formula durationMod,
                        Boolean friendlyFire, Boolean positiveOnly, Boolean negativeOnly) {

        this.durationMod = durationMod;
        this.friendlyFire = friendlyFire;
        this.positiveOnly = positiveOnly;
    }

    public DispelEffect(Formula chance, Formula durationMod,
                        Boolean friendlyFire) {
        this(chance, durationMod, friendlyFire, false, false);
    }

    @Override
    public boolean applyThis() {

        for (Attachment attachment : game.getState().getAttachmentsMap()
         .get(ref.getTargetObj())) {

            if (!(attachment instanceof BuffObj)) {
                continue;
            }
            BuffObj buff = (BuffObj) attachment;

            if (!buff.isDispelable()) {
                continue;
            }
            if (!friendlyFire) {
                if (buff.getOwner().isMe()) {
                    continue;
                }
            }
            if (positiveOnly) {
                // if (buff.getBuffType() ==)
            }
            boolean result = true;
            // Ref REF = Ref.getCopy(ref);
            // REF.setTarget(buff.getId());
            // REF.setID(KEYS.SUMMONER, buff.getOwnerUnit()); // is it owner or
            // // basis? ;)
            //
            // Roll roll = new Roll(ROLL_TYPES.DISPEL, chance.getLevel(ref));
            // result = RollMaster.roll(roll);
            // TODO ROLL!

            Integer amount = durationMod.getInt(ref);

            buff.modifyDuration(amount);

        }

        return true;
    }
}
