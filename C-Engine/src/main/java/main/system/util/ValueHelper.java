package main.system.util;

import main.content.ContentManager;
import main.content.VALUE;
import main.data.xml.XML_Writer;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.Game;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import javax.swing.*;
import java.awt.*;
import java.util.*;

//alt+shift+v - setValue! 
//to be used in AV on the selectedType and in debug window (in addition to FunctionHelper!)

public class ValueHelper {
    // prompt for value name and find value

    public static final char HOTKEY_CHAR = 'v';
    Map<String, VALUE> foundValues = new HashMap<>();
    Map<String, Set<VALUE>> rejectedValues = new HashMap<>();
    Stack<String> searches = new Stack<String>();
    private Entity entity;
    private Component parent;
    private Game game;

    public ValueHelper(Game game) {
        this.game = game;

    }

    private boolean checkRejectedValue(String valueName, VALUE v) {
        Set<VALUE> set = rejectedValues.get(valueName);
        if (set == null) {
            return false;
        }
        for (VALUE val : set) {
            if (val == v) {
                return true;
            }
        }
        return false;
    }

    private VALUE checkFoundValues(String valueName) {
        for (String s : foundValues.keySet()) {
            if (StringMaster.compare(s, valueName)) {
                return foundValues.get(s);
            }
            // TODO find similar?
        }
        return null;
    }

    public void promptSetValue() {
        SoundMaster.playStandardSound(RandomWizard.random() ? STD_SOUNDS.OPEN : STD_SOUNDS.CLOCK);
        if (getEntity() == null) {
            JOptionPane.showMessageDialog(parent, "ValueHelper:setValue: No Entity found!");
            return;
        }
        String initialSelectionValue = "";
        if (!searches.isEmpty()) {
            initialSelectionValue = searches.pop();
        }
        String value = JOptionPane.showInputDialog(parent, "Enter value name to be set for "
                + getEntity(), initialSelectionValue);
        if (value == null) {
            return;
        }
        searches.push(value);
        setValue(value);
        SoundMaster.playStandardSound(RandomWizard.random() ? STD_SOUNDS.CLOSE
                : STD_SOUNDS.DONE2);
    }

    public void setValue(String valueName) {
        if (getEntity() == null) {
            return;
        }

        VALUE v = getValue(valueName);

        String amount = "";
        if (v != null) {
            amount = getEntity().getValue(v);
            foundValues.put(valueName, v);
        } else {
            valueName = CounterMaster.findCounter(valueName);
            amount = "" + getEntity().getCounter(valueName);
        }
        String name = valueName;

        if (v != null) {
            name = v.getName();
        }
        String message = "Set value: " + name + " for " + getEntity().getName();

        String input = JOptionPane.showInputDialog(parent, message, amount);

        Set<VALUE> set = rejectedValues.get(valueName);

        if (set == null) {
            set = new HashSet<>();
            rejectedValues.put(valueName, set);
        }
        if (input != null) {
            set.remove(v);
        } else {
            set.add(v);
        }

        if (!StringMaster.isEmpty(input)) {
            if (!input.equalsIgnoreCase(amount)) {
                if (v != null) {
                    getEntity().setValue(v, input);
                    if (getEntity() instanceof Obj) {
                        getEntity().getType().setValue(v, input);
                    }
                    // if (getEntity() instanceof DC_HeroObj ) //set items
                    // initialized false!
                } else {
                    getEntity().setCounter(valueName, StringMaster.getInteger(input));
                }
                game.getManager().reset();
                game.getManager().refreshAll();
            }
        }
        if (getEntity() instanceof ObjType) {
            if (CoreEngine.isArcaneVault()) {
                XML_Writer.writeXML_ForType((ObjType) getEntity());
            }
        }

    }

    public VALUE getValue(String valueName) {
        VALUE value = null;
        value = checkFoundValues(valueName);
        if (value != null) {
            if (!checkRejectedValue(valueName, value)) {
                return value;
            }
        }
        ContentManager.setExcludedValueSet(rejectedValues.get(valueName));
        try {
            value = ContentManager.getValue(valueName);
            if (value == null) {
                value = ContentManager.getValue(valueName, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ContentManager.setExcludedValueSet(null);
        }
        return value;
    }

    public Entity getEntity() {
        if (entity == null) {
            if (game.getManager().getInfoObj() != null) {
                return game.getManager().getInfoObj();
            }
        }

        if (entity == null) {
            if (game.getManager().getActiveObj() != null) {
                return game.getManager().getActiveObj();
            }
        }

        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Component getParent() {
        return parent;
    }

    public void setParent(Component parent) {
        this.parent = parent;
    }

}
