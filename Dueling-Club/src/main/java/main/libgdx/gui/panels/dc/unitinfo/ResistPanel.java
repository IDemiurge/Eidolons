package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.content.enums.GenericEnums;
import main.content.values.parameters.PARAMETER;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static main.content.ValuePages.RESISTANCES;
import static main.content.enums.GenericEnums.DAMAGE_TYPE.values;
import static main.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.images.ImageManager.getDamageTypeImagePath;

public class ResistPanel extends TablePanel {

    private Map<String, ValueContainer> map;

    public ResistPanel() {

        map = new HashMap<>();

        GenericEnums.DAMAGE_TYPE[] damageTypes = values();
        Map<String, GenericEnums.DAMAGE_TYPE> damageTypeMap = new HashMap<>();
        for (int i = 0; i < damageTypes.length; i++) {
            GenericEnums.DAMAGE_TYPE damageType = damageTypes[i];
            damageTypeMap.put(damageType.getName().toLowerCase(), damageType);
        }

        List<Pair<TextureRegion, String>> pairs =
                Arrays.stream(RESISTANCES)
                        .map(parameter -> parameter.name().replace("_RESISTANCE", "").toLowerCase())
                        .filter(parameter -> damageTypeMap.containsKey(parameter))
                        .map(parameter -> {
                            GenericEnums.DAMAGE_TYPE damageType = damageTypeMap.get(parameter);
                            TextureRegion textureRegion = getOrCreateR(getDamageTypeImagePath(damageType.getName()));
                            return new ImmutablePair<>(textureRegion, parameter);
                        }).collect(Collectors.toList());

        Iterator<Pair<TextureRegion, String>> iter = pairs.iterator();
        for (int i = 0; i < 3; i++) {
            addCol();
            for (int j = 0; j < 6; j++) {
                if (iter.hasNext()) {
                    Pair<TextureRegion, String> p = iter.next();
                    ValueContainer valueContainer = new ValueContainer(p.getLeft(), "");
                    map.put(p.getRight(), valueContainer);
                    addElement(valueContainer.fill().left().bottom().pad(0, 10, 10, 0));
                }
            }
        }

        setHeight(getPrefHeight());
        fill().center().bottom();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (updatePanel) {
            Supplier<List<Pair<PARAMETER, String>>> source = (Supplier) getUserObject();

            source.get().forEach(pair -> {
                String ps = pair.getLeft().name().replace("_RESISTANCE", "").toLowerCase();
                if (map.containsKey(ps)) {
                    map.get(ps).updateValue(pair.getRight());
                }
            });

            updatePanel = false;
        }
    }
}
