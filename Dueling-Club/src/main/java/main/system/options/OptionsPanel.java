package main.system.options;

import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_TabbedPanel;
import main.swing.generic.components.misc.G_Button;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.options.Options.OPTION;
import main.system.options.OptionsMaster.OPTIONS_GROUP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class OptionsPanel<T extends Enum<T>> extends G_Panel implements ActionListener {
    OptionsMaster master;
    private Map<OPTION, Component> map = new HashMap<>();
    private G_TabbedPanel tabs;

    public OptionsPanel(Map<OPTIONS_GROUP, Options> optionsMap) {
        super(("flowy "));
        tabs = new G_TabbedPanel(new Dimension(600, 800));
        for (OPTIONS_GROUP group : optionsMap.keySet()) {
            G_Panel subpanel = new G_Panel("flowy");
            Options options = optionsMap.get(group);
            for (Object v : options.getValues().keySet()) {
                OPTION option = options.getKey(v.toString());
                if (option == null)
                    continue;
                Component comp = getOptionComp(options, option);
                if (comp == null)
                    continue;
                subpanel.add(comp, "sg comps");

            }
            tabs.addTab(subpanel, StringMaster.getWellFormattedString(group.toString()));
            // subpanel.setPanelSize(size);
        }
        add(tabs, "x 100");
        G_Panel btnpanel = new G_Panel();
        btnpanel.add(new G_Button("Ok", () -> ok()), "sg btns");
        btnpanel.add(new G_Button("Save", () -> save()), "sg btns");
        btnpanel.add(new G_Button("Apply", () -> update()), "sg btns");
        btnpanel.add(new G_Button("Cancel", () -> cancel()), "sg btns");
        btnpanel.add(new G_Button("Defaults", () -> toDefaults()), "sg btns");
        add(btnpanel);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    private Component getOptionComp(Options options, OPTION option) {
        switch (options.getValueClass(option).getSimpleName()) {
            case "String":
                JComboBox<String> box = new JComboBox<>(new Vector<>(
                 ListMaster.toStringList(option.getOptions())
//                 options.getContainerValues((Enum) option))
                ));
                box.setSelectedItem(options.getValue(option.toString()));
                map.put(option, box);
                return new G_Panel(
                 new JLabel(option.getName()), box); //limit?
            case "Integer":
//                JTextField tbox = new JTextField(options.getValue((Enum) option));
//                map.put(option, tbox);
//                return new G_Panel(
//                 new JLabel(option.getName()), tbox
//                );
//            case "Long":
                JSlider s = new JSlider(option.getMin(), option.getMax(),
                 (Integer) options.getIntValue(option.toString()));
            map.put(option, s);
                return new G_Panel(
                 new JLabel(option.getName()), s
                );
            case "Boolean":
                JCheckBox cbox = new JCheckBox("", options.getBooleanValue((Enum) option));
                map.put(option, cbox);
                return new G_Panel(
                 new JLabel(option.getName()), cbox
                );
        }
        return null;
//		return new JComboBox<String>(options.getOptionItems(option));
    }

//    public void initGui() {
//        for (Object s : options.getValues().keySet()) {
//            OPTION o = new EnumMaster<OPTION>().retrieveEnumConst(OPTION.class, ((String) s));
//            Object value = options.getValue((T) o);
//            if (o.getDefaultValue() != null) {
//                addCheckbox(o, (Boolean) value);
//            } else if (o.getOptions() != null) {
//                addComboBox(o, value, panel);
//                // checkbox?
//            } else
//                y += addSlider(o, value);
//
//        }
//    }
//
//    private int addSlider(OPTION o, Object value) {
//    }
//
//    private void addComboBox(OPTION o, Object value) {
//    }
//
//    private void addCheckbox(OPTION o, boolean value) {
////        new JLabel(o.toString());
//        JCheckBox box = new JCheckBox(o.toString());
//        box.setSelected(value);
//        add(box);
//    }

    public void update() {
        OptionsMaster.cacheOptions();
        map.keySet().forEach(o -> {
            Component comp = map.get(o);
            Object value = null;

            if (comp instanceof JSlider) {
                value = ((JSlider) comp).getValue();
            }
            if (comp instanceof JComboBox) {
                value = ((JComboBox) comp).getSelectedItem();
            }
            if (comp instanceof JCheckBox) {
                value = ((JCheckBox) comp).isSelected();
            }
            if (comp instanceof JTextField) {
                value = ((JTextField) comp).getText();
            }
            OptionsMaster.getOptions(o).setValue(o.toString(), value.toString());
//            if (o instanceof GRAPHIC_OPTION) {
//                OptionsMaster.getGraphicsOptions().setValue((GRAPHIC_OPTION) o, value.toString());
//            }
//            if (o instanceof ANIMATION_OPTION) {
//                OptionsMaster.getAnimOptions().setValue((ANIMATION_OPTION) o, value.toString());
//            }
        });

       try{ OptionsMaster.applyOptions();             }catch(Exception e){                e.printStackTrace();            }
    }

    public void save() {
        update();
        OptionsMaster.saveOptions();
    }


    public void cancel() {
        OptionsMaster.resetToCached();
        close();
    }

    public void toDefaults() {
        OptionsMaster.resetToDefaults();
    }


    private void ok() {
        update();
        close();
    }

    private void close() {
        setVisible(false);

        if (getFrame() !=null ) {
            (getFrame()).dispose();
        }
    }



}
