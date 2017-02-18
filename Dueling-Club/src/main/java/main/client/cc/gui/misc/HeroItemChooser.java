package main.client.cc.gui.misc;

import main.content.OBJ_TYPE;
import main.content.values.properties.PROPERTY;
import main.entity.type.ObjType;
import main.swing.generic.components.editors.lists.ListChooser;

import java.util.List;

public class HeroItemChooser extends ListChooser {

    private Class<? extends Enum<?>> CLASS;
    private PROPERTY prop;
    private ObjType hero;

    public HeroItemChooser(ObjType hero, List<String> options, PROPERTY prop,
                           boolean ENUM, OBJ_TYPE TYPE) {
        super(options, ENUM, TYPE);
        this.hero = hero;
        this.prop = prop;
    }

    @Override
    public String getString() {
        String result = super.getString();
        hero.setProperty(prop, result);
        return result;
    }

}
