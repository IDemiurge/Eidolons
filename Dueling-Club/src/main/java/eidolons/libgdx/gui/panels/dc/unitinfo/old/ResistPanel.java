package eidolons.libgdx.gui.panels.dc.unitinfo.old;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.content.PARAMS;
import eidolons.content.ValuePages;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import main.content.values.parameters.PARAMETER;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;
import static main.content.enums.GenericEnums.DAMAGE_TYPE;
import static main.content.enums.GenericEnums.DAMAGE_TYPE.*;
import static main.system.images.ImageManager.getDamageTypeImagePath;

public class ResistPanel extends TablePanel {

    private Map<DAMAGE_TYPE, ValueContainer> map;

    public ResistPanel() {

        map = new HashMap<>();

        List<Pair<TextureRegion, DAMAGE_TYPE>> pairs =
         Arrays.stream(ValuePages.RESISTANCES)
          .map(parameter -> {
              DAMAGE_TYPE damageType = getFromParams((PARAMS) parameter);
              TextureRegion textureRegion = getOrCreateR(getDamageTypeImagePath(
               damageType.getName(), true));
              return new ImmutablePair<>(textureRegion, damageType);
          }).collect(Collectors.toList());

        Iterator<Pair<TextureRegion, DAMAGE_TYPE>> iter = pairs.iterator();

        for (int j = 0; j < 6; j++) {
            final int j6 = j + 6;
            final int j12 = j + 12;

            addContainer(pairs.get(j));

            if (j6 < pairs.size()) {
                addContainer(pairs.get(j6));
            }

            if (j12 < pairs.size()) {
                addContainer(pairs.get(j12));
            }

            row();
        }
    }

    private static DAMAGE_TYPE getFromParams(PARAMS parameter) {
        DAMAGE_TYPE damageType = null;
        switch (parameter) {
            case FIRE_RESISTANCE:
            case FIRE_ARMOR:
            case FIRE_DURABILITY_MOD:
                damageType = FIRE;
                break;
            case COLD_RESISTANCE:
            case COLD_ARMOR:
            case COLD_DURABILITY_MOD:
                damageType = COLD;
                break;
            case ACID_RESISTANCE:
            case ACID_ARMOR:
            case ACID_DURABILITY_MOD:
                damageType = ACID;
                break;
            case LIGHTNING_RESISTANCE:
            case LIGHTNING_ARMOR:
            case LIGHTNING_DURABILITY_MOD:
                damageType = LIGHTNING;
                break;
            case SONIC_RESISTANCE:
            case SONIC_ARMOR:
            case SONIC_DURABILITY_MOD:
                damageType = SONIC;
                break;
            case LIGHT_RESISTANCE:
            case LIGHT_ARMOR:
            case LIGHT_DURABILITY_MOD:
                damageType = LIGHT;
                break;
            case CHAOS_RESISTANCE:
            case CHAOS_ARMOR:
            case CHAOS_DURABILITY_MOD:
                damageType = CHAOS;
                break;
            case ARCANE_RESISTANCE:
            case ARCANE_ARMOR:
            case ARCANE_DURABILITY_MOD:
                damageType = ARCANE;
                break;
            case HOLY_RESISTANCE:
            case HOLY_ARMOR:
            case HOLY_DURABILITY_MOD:
                damageType = HOLY;
                break;
            case SHADOW_RESISTANCE:
            case SHADOW_ARMOR:
            case SHADOW_DURABILITY_MOD:
                damageType = SHADOW;
                break;
            case PSIONIC_RESISTANCE:
            case PSIONIC_ARMOR:
            case PSIONIC_DURABILITY_MOD:
                damageType = PSIONIC;
                break;
            case DEATH_RESISTANCE:
            case DEATH_ARMOR:
            case DEATH_DURABILITY_MOD:
                damageType = DEATH;
                break;
            case PIERCING_RESISTANCE:
            case PIERCING_ARMOR:
            case PIERCING_DURABILITY_MOD:
                damageType = PIERCING;
                break;
            case BLUDGEONING_RESISTANCE:
            case BLUDGEONING_ARMOR:
            case BLUDGEONING_DURABILITY_MOD:
                damageType = BLUDGEONING;
                break;
            case SLASHING_RESISTANCE:
            case SLASHING_ARMOR:
            case SLASHING_DURABILITY_MOD:
                damageType = SLASHING;
                break;
            case POISON_RESISTANCE:
                //case POISON_ARMOR:
                //case POISON_DURABILITY_MOD:
                damageType = POISON;
                break;
        }
        return damageType;
    }

    void addContainer(Pair<TextureRegion, DAMAGE_TYPE> pair) {
        ValueContainer valueContainer = new ValueContainer(pair.getLeft(), "n/a");
        valueContainer.setSize(32, 32);
        map.put(pair.getRight(), valueContainer);
        addElement(valueContainer);
    }

    @Override
    public void updateAct(float delta) {
        List<Pair<PARAMETER, String>> source = (List<Pair<PARAMETER, String>>) getUserObject();

        source.forEach(pair -> {
            PARAMS param = (PARAMS) pair.getLeft();
            DAMAGE_TYPE damageType = getFromParams(param);
            if (map.containsKey(damageType)) {
                final ValueContainer container = map.get(damageType);
                ValueTooltip valueTooltip = new ValueTooltip();
                valueTooltip.setUserObject((Supplier) () ->
                 Arrays.asList(new ValueContainer(param.getName(), "")));
                container.addListener(valueTooltip.getController());
                container.updateValue(pair.getRight() + "%");
            }
        });
    }
}
