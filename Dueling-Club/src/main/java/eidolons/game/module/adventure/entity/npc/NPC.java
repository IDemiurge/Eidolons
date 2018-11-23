package eidolons.game.module.adventure.entity.npc;

import eidolons.macro.entity.MacroObj;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 3/14/2018.
 * List of values
 * <p>
 * Common systems
 * Faction loyalty
 * Disposition
 * Character (ai)
 * <p>
 * Early usage
 * Prices
 * Dialogue
 * Refusals and other reactions
 * <p>
 * Innkeeper
 * Trader
 * Librarian
 * Mentor
 * Mercenary
 * Free hero
 *
 *
 General way of creating quest givers
 :: NPC intro
 :: Quest-texts + generic responses/comments + flavor from NPC type

 NPC has Profession and Style that determine their flavor texts
 std line variants too of course

 There will be weight maps for their lines perhaps
 And some kind of ‘inheritance’
 in fact, I’d love to have a way to define it with RngStyles too


 Quest generation itself…
 generating quest text
 using some other bits and pieces?

 quest type
 quest location
 quest origin
 conditions (time,
 style/flavor (for items and description)

 */
public class NPC extends MacroObj{

    public NPC(ObjType type) {
        super(type);
    }

    public void interact() {


    }


}
