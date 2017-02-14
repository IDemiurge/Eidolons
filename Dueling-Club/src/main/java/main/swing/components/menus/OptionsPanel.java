package main.swing.components.menus;

import main.entity.obj.unit.DC_HeroObj;
import main.swing.generic.components.G_Panel;
import main.swing.generic.services.dialog.DialogPanel;
import main.system.auxiliary.EnumMaster;
import main.system.options.Options;
import main.system.options.OptionsMaster;
import main.system.options.OptionsMaster.OPTIONS_GROUP;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class OptionsPanel<T extends Enum<T>> extends DialogPanel implements MouseListener {
    OptionsMaster master;
    private Options options;

    public OptionsPanel(Options o) {
        options = o;
    }

    public OptionsPanel(DC_HeroObj target) {
        super(target);
        for (OPTIONS_GROUP group : OPTIONS_GROUP.values()) {
            Options options = OptionsMaster.getOptions(group);
            G_Panel subpanel = new G_Panel("flowy");
            for (Object v : options.getValues().keySet()) {
                Enum option = options.getEnumConst(v.toString());
                Component comp = getOptionComp(options, option);
                subpanel.add(comp);
            }
            add(subpanel);
            // subpanel.setPanelSize(size);
        }

    }

    private Component getOptionComp(Options options, Enum option) {
        switch (options.getValueClass(option).getSimpleName()) {
            case "Integer":
                return new TextField(options.getValue(option));
            case "Boolean":
                boolean state = options.getBooleanValue(option);
                return new Checkbox(option.toString(), state);
            // maxWidth
        }
        return null;
//		return new JComboBox<String>(options.getOptionItems(option));
    }

    public void initGui() {
        for (Object s : options.getValues().keySet()) {
            OPTION o = new EnumMaster<OPTION>().retrieveEnumConst(OPTION.class, ((String) s));
            Object value = options.getValue((T) o);
//			if (o.getDefaultValue() != null) {
//				addCheckbox(o, value);
//			} else if (o.getOptions() != null) {
//				addComboBox(o, value);
//				// checkbox?
//			} else
//				y += addSlider(o, value);

        }
    }

    public void save() {

    }

    public void loadProfile() {

    }

    public void cancel() {

    }

    public void toDefaults() {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public interface OPTION {
        Integer getMin();

        Integer getMax();

        Object getDefaultValue();

        Boolean isExclusive();

        Object[] getOptions();

    }

}
