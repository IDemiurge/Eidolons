package main.ability.effects.oneshot.buff;

import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.entity.obj.Attachment;
import main.entity.obj.BuffObj;
import main.system.math.Formula;

public class DispelEffect extends MicroEffect  implements OneshotEffect {

    private Formula chance;
    private Formula durationMod;
    private boolean friendlyFire;
    private Boolean positiveOnly;
    private Boolean negativeOnly;

    public DispelEffect(Formula chance, Formula durationMod,
                        Boolean friendlyFire, Boolean positiveOnly, Boolean negativeOnly) {

        this.chance = chance;
        this.durationMod = durationMod;
        this.friendlyFire = friendlyFire;
        this.positiveOnly = positiveOnly;
        this.negativeOnly = negativeOnly;
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
            // REF.setID(KEYS.SUMMONER, buff.getOwnerObj()); // is it owner or
            // // basis? ;)
            //
            // Roll roll = new Roll(ROLL_TYPES.DISPEL, chance.getInt(ref));
            // result = RollMaster.roll(roll);
            // TODO ROLL!

            Integer amount = durationMod.getInt(ref);

            buff.modifyDuration(amount);

        }

        return true;
    }
}
