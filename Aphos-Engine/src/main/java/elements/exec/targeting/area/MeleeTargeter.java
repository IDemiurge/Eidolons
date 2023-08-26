package elements.exec.targeting.area;

import elements.exec.EntityRef;
import elements.exec.condition.Condition;

/**
 * Created by Alexander on 8/23/2023
 *
 * custom melee adjacency! Perhaps if we do that first anyway, then we'll see how to do ... geometric adj
 * Btw - that graze random on miss may have to use Melee adjacency! E.g. if Rev is on Van cell - ?
 *
 * Then there is the Close/Long Reach.
 * >> Close can never attack over max diagonal
 * >> Long can do so even when blocked; plus reach the back-row target directly in front of them (with penalty?)
 * Wazzup with penalties btw? Maybe we should do that actually!
 * Disadvantage - atk roll grade is 1 below (1 auto-fail)
 * Range - a la SoC, 2|3? Using geometric diff:
 * From rear - Van=3, Front=4, Back=5, Rear=6... so there is quite a bit of space for this.
 * What about diagonal? by logic of steps:
 * from B Flank - van=3/4, front=4/5/6, back = 5/6/7, rear=7 (assuming that we count it as 2 steps to our own front)
 *
 * OK, I like the idea of range via steps
 * The idea of DISADV is also great - and perhaps it can be proportional to range excess!
 * Aye, so with range, let's let them shoot always...
 * Auto-fails
 *
 * Is there inversely, advantage? When closer than range? Hardly!
 * So with ranged - we basically allow ALL visible targets (that are not blocked), but count DISADV?
 * For melee - close quarters gets disadv? and Long Reach - in front-melee?
 *
 * i: Flanked - when an ally is on enemy flank, adj front enemy has disadv on def
 *
 * Geom vs melee - we do let unit attack the next front unit, but with disadv?
 * Mind it - such stuff as Disadv has to be precalced to show tooltip!
 *
 */
public class MeleeTargeter {
    public static Condition getCloseQuartersCondition() {
        return new Condition() {
            @Override
            public boolean check(EntityRef ref) {
                return false;
            }
        };
    }

}
