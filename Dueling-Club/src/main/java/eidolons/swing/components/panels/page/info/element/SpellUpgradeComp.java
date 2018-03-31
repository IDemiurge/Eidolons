package eidolons.swing.components.panels.page.info.element;

import main.content.enums.entity.SpellEnums.SPELL_UPGRADE;
import eidolons.swing.components.buttons.CustomButton;
import main.swing.generic.components.CompVisuals;
import main.swing.generic.components.ComponentVisuals;

public class SpellUpgradeComp extends CustomButton {
    boolean selected;
    private SPELL_UPGRADE upgrade;

    public SpellUpgradeComp(SPELL_UPGRADE su) {
        super(new CompVisuals(null, su.getGlyphImage()));
        this.upgrade = su;

    }

    public ComponentVisuals getGenericVisuals() {
        if (selected) {
            return new CompVisuals(null, upgrade.getGlyphImageActive());
        }
        return new CompVisuals(null, upgrade.getGlyphImage());
    }

    public void handleClick() {
//        upgradeClicked(upgrade);
    }

}
