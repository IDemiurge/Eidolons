package eidolons.system.text;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.feat.active.UnitAction;
import eidolons.entity.feat.active.Spell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.tools.future.FutureBuilder;
import eidolons.game.battlecraft.ai.tools.target.AI_SpellMaster;
import eidolons.game.battlecraft.rules.combat.damage.DamageCalculator;
import eidolons.game.core.game.DC_Game;
import main.content.enums.entity.ActionEnums;
import main.content.enums.system.AiEnums.AI_LOGIC;
import main.swing.generic.components.G_Panel;

import java.util.ArrayList;
import java.util.List;

public class ToolTipMaster {

    private final List<TextItem> toolTipTextItems = new ArrayList<>();
    private TextItem actionRequirementText;
    private TextItem unitToolTip;
    private TextItem targetingText;
    private TextItem buffTooltip;
    private TextItem itemTooltip;
    private TextItem paramTooltip;
    private G_Panel panel;

    // separate by type? - req, anim, hover-info ...
    public ToolTipMaster(DC_Game game) {
    }

    public static String getActionTargetingTooltip(DC_Obj target, ActiveObj active) {
        if (active == null) {
            return "";
        }
        String tooltip = "";
        ACTION_TOOL_TIP_CASE _case = null;
        AI_LOGIC spellLogic = AI_SpellMaster.getSpellLogic(active);
        if (spellLogic == null) {
            if (active instanceof UnitAction) {
                if (active.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.ATTACK) {
                    _case = ACTION_TOOL_TIP_CASE.DAMAGE;
                }
            }
        }
        if (spellLogic != null) {
            switch (spellLogic) {
                case BUFF_NEGATIVE:
                case BUFF_POSITIVE:
                    _case = ACTION_TOOL_TIP_CASE.BUFF;
                    break;
                case CUSTOM_HOSTILE:
                case CUSTOM_SUPPORT:
                    _case = ACTION_TOOL_TIP_CASE.SPECIAL;
                    break;
                case AUTO_DAMAGE:
                case DAMAGE:
                case DAMAGE_ZONE:
                    _case = ACTION_TOOL_TIP_CASE.DAMAGE;
                    break;
                case RESTORE:
                    break;
            }
        }
        if (_case == null) {
            return "";
        }
        switch (_case) {
            case BUFF:
            case SPECIAL:
                break;
            case DAMAGE:
                boolean attack = !(active instanceof Spell);
                int damage = FutureBuilder.precalculateDamage(active, target, attack);
                // ++ damage type
                tooltip += "avrg. damage: " + damage; // MIN-MAX is really in
                // order
                if (DamageCalculator.isLethal(damage, target)) {
                    tooltip += "(lethal)"; // TODO possibly lethal
                } else {
                    if (attack) {
                        if (((Unit) target).canCounter(active)) {
                            tooltip += "(will retaliate)"; // TODO precalc dmg?
                        }
                    }
                }
                break;
        }

        return tooltip;
    }

    public enum ACTION_TOOL_TIP_CASE {
        DAMAGE, BUFF, SPECIAL,
    }

    public enum SCREEN_POSITION {
        BF_BOTTOM,
        BF_TOP,
        BF_ABOVE,
        BF_BELOW,
        ACTIVE_UNIT_BELOW,
        INFO_UNIT_BELOW,
        ACTIVE_UNIT_ABOVE,
        INFO_UNIT_ABOVE,
        ACTIVE_UNIT_TOP,
        INFO_UNIT_TOP,
        ACTIVE_UNIT_BOTTOM,
        INFO_UNIT_BOTTOM,

        CENTER,
        BOTTOM,
        TOP,
        LOG_BELOW,

    }

    public enum TOOLTIP_TYPE {
        DC_INFO_PAGE_PASSIVE,
        DC_ACTIVE_PANEL_BUFF,
        DC_INFO_PANEL_BUFF,
        DC_ACTIVE_PANEL_ITEM,
        DC_INFO_PANEL_ITEM,
        DC_QUICK_ITEM,
        DC_SPELL,
        DC_INFO_PAGE_PARAMETER,
        DC_INFO_PAGE,
        DC_DYNAMIC_PARAM,
        CUSTOM_TOOLTIP,
    }

}
