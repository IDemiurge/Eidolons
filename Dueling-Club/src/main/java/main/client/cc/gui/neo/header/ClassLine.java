package main.client.cc.gui.neo.header;

import main.client.cc.gui.views.ClassView;
import main.content.DC_TYPE;
import main.content.PROPS;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.obj.attach.DC_FeatObj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.list.ListItem;
import main.system.graphics.GuiManager;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager.BORDER;

import java.awt.*;

public class ClassLine extends G_Panel {
    public static final int MAX_CLASSES = 4;
    boolean vertical;
    boolean prime;
    Unit hero;

    public ClassLine(boolean vertical, boolean prime, Unit hero) {
        this.vertical = vertical;
        this.prime = prime;
        this.hero = hero;
        init();
        panelSize = (new Dimension(getWidth(), getHeight()));
    }

    @Override
    public void refresh() {
        removeAll();
        init();
        revalidate();
    }

    public void init() {
        int x = 0;
        int y = 0;
        // TODO empty items?
        for (String string : StringMaster.openContainer(hero.getProperty(PROPS.CLASSES))) {
            string = VariableManager.removeVarPart(string);
            ObjType type = DataManager.getType(string, DC_TYPE.CLASSES);

            if (!StringMaster.isEmpty(hero.getProperty(PROPS.SECOND_CLASS))) {
                String property = (prime) ? hero.getProperty(PROPS.FIRST_CLASS) : hero
                        .getProperty(PROPS.SECOND_CLASS);
                if (hero.getProperty(PROPS.FIRST_CLASS).contains(ClassView.MULTICLASS)) {

                    boolean multi = hero.checkProperty(PROPS.MULTICLASSES, type.getName());
                    if (prime) {
                        if (!multi) {
                            continue;
                        }
                    } else if (multi) {
                        continue;
                    }

                } else {

                    String property2 = type.getProperty(G_PROPS.CLASS_GROUP);
                    if (!StringMaster.compare(property, property2)) {
                        continue;
                    }
                }
            }
            String pos = (vertical) ? "pos 0 " + (MAX_CLASSES - y - 1)
                    * GuiManager.getSmallObjSize() : "pos " + x * GuiManager.getSmallObjSize()
                    + " 0";

            final DC_FeatObj classObj = hero.getFeat(false, type);
            ListItem<ObjType> item = new ListItem<ObjType>(type, false, false, GuiManager
                    .getSmallObjSize()) {
                public BORDER getSpecialBorder() {
                    return classObj.getRankBorder();
                }
                // public void initDefaultBorders() {
                // BORDER newBorder=classObj.getRankBorder();
                // if (newBorder!=null)
                // icon =new
                // ImageIcon(ImageManager.applyBorder(getImage(),newBorder));
                // super.initDefaultBorders();
                // }

            };
            add(item, pos);
            if (vertical) {
                y++;
            } else {
                x++;
            }

        }

    }

    private boolean checkMulticlass(String property, ObjType type) {
        String typeName = StringMaster.getSubString(property, "(", ")", false);
        ObjType multiclassType = DataManager.getType(typeName);

        for (String className : StringMaster.openContainer(multiclassType
                .getProperty(PROPS.BASE_CLASSES_ONE))) {
            if (type.getName().equalsIgnoreCase(className)) {
                return true;
            }
        }
        for (String className : StringMaster.openContainer(multiclassType
                .getProperty(PROPS.BASE_CLASSES_TWO))) {
            if (type.getName().equalsIgnoreCase(className)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getHeight() {
        return (vertical) ? GuiManager.getSmallObjSize() * MAX_CLASSES : GuiManager
                .getSmallObjSize();
    }

    @Override
    public int getWidth() {
        return (!vertical) ? GuiManager.getSmallObjSize() * MAX_CLASSES : GuiManager
                .getSmallObjSize();

    }
}
