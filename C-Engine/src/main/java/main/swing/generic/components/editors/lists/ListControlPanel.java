package main.swing.generic.components.editors.lists;

import main.content.OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.ability.construct.VarHolder;
import main.data.ability.construct.VariableManager;
import main.entity.type.ObjType;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.swing.generic.components.list.G_List;
import main.system.auxiliary.data.ArrayMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ListControlPanel<E> extends JPanel implements ActionListener {
    String[] commands = new String[]{"Add", "Remove", "Move Up", "Move Down", "Top", "Bottom",
            "Edit"};
    char[] mnemonics = new char[]{};
    private GenericListChooser<E> chooser;
    private G_List<E> secondList;
    private G_List<E> list;
    private OBJ_TYPE TYPE;
    private List<E> selected;
    private OBJ_TYPE VAR_TYPE;
    private List<Object> varTypes;
    private Class<?> varClass;
    private VarHolder varHolder;
    private int lastSelectedIndex;
    private int firstSelectedIndex;

    public ListControlPanel(GenericListChooser<E> chooser) {
        this.chooser = chooser;
        this.setList(chooser.getList());
        this.setSecondList(chooser.getSecondList());
        this.setVarTypes(chooser.getVarTypes());
        this.TYPE = chooser.getTYPE();
        setLayout(new MigLayout("wrap, flowy"));
        int i = 0;
        for (String command : commands) {
            JButton button = new JButton(command);
            button.setActionCommand(command);
            button.addActionListener(this);
            button.setToolTipText(getTooltip(command));
            button.setMnemonic(String.valueOf(i).toCharArray()[0]);
            i++;
            add(button);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        initSelected();
        if (selected == null) {
            return;
        }

        switch (((JButton) e.getSource()).getActionCommand()) {
            case "Edit": {
                edit();
                break;
            }
            case "Add": {
                add();
                break;
            }
            case "Move Up": {
                moveOne(true);
                break;
            }
            case "Move Down": {
                moveOne(false);
                break;
            }
            case "Top": {
                moveUp();
                break;
            }
            case "Bottom": {
                moveDown();
                break;
            }
            case "Remove": {
                remove();
                break;
            }
        }

    }

    private String getTooltip(String command) {
        switch (command) {
            case "Edit": {
                return "Right Click";
            }
            case "Add": {
                return "Press Enter";
            }
            case "Move Up": {
                return "Alt-Click";
            }
            case "Move Down": {
                return "Alt-Right Click";
            }
            case "Top": {
                return "Ctrl-Alt-Click";
            }
            case "Bottom": {
                return "Ctrl-Alt-Right Click";
            }
            case "Remove": {
                return "Press Delete";
            }
        }
        return null;
    }

    public void initSelected() {
        if (getSecondList() != null) {
            this.selected = getSecondList().getSelectedValuesList();
            if (!selected.isEmpty()) {
                lastSelectedIndex = getSecondList().getSelectedIndices()[selected.size() - 1];
                firstSelectedIndex = getSecondList().getSelectedIndices()[0];
            } else {
                selected = getList().getSelectedValuesList();
                lastSelectedIndex = getList().getSelectedIndices()[selected.size() - 1];
                firstSelectedIndex = getList().getSelectedIndices()[0];
            }
        } else {
            selected = getList().getSelectedValuesList();
            lastSelectedIndex = getList().getSelectedIndices()[selected.size() - 1];
            firstSelectedIndex = getList().getSelectedIndices()[0];
        }

    }

    public void remove() {
        initSelected();

        ((DefaultListModel<E>) getSecondList().getModel()).removeRange(firstSelectedIndex,
                lastSelectedIndex);

        // if (((DefaultListModel<E>) getSecondList().getModel()).getSize()
        // < selected.size()){
        // Enumeration<E> elements = ((DefaultListModel<E>)
        // getSecondList().getModel()).elements();
        // ((DefaultListModel<E>)
        // getSecondList().getModel()).removeAllElements();
        // while (elements.hasMoreElements()){
        // ((DefaultListModel<E>) getSecondList().getModel()).
        // addElement(elements.nextElement()) ;
        // }
        // }
        // for (E element : selected){
        // ((DefaultListModel<E>) getSecondList().getModel())
        // .removeElement(element);
        // }

    }

    public void edit() {
        if (chooser.getMode() == SELECTION_MODE.SINGLE) {
            return;
        }
        if (getSecondList() == null) {
            return;
        }
        selected = getSecondList().getSelectedValuesList();
        if (selected.isEmpty()) {
            return;
        }
        if (selected.size() > 1) {
            return;
        }

        E e = selected.get(0);
        if (!(e instanceof String)) {
            return;
        }
        String newValue = JOptionPane.showInputDialog("Edit element", e.toString());
        if (newValue == null) {
            return;
        }
        ((DefaultListModel<E>) getSecondList().getModel()).set(getSecondList().getSelectedIndex(),
                (E) newValue);

    }

    public void removeOrAdd(SELECTION_MODE mode) {
        if (mode == SELECTION_MODE.SINGLE) {
            add();
            return;
        }
        if (getSecondList() != null) {
            selected = getSecondList().getSelectedValuesList();
            if (!selected.isEmpty()) {
                remove();
            } else {
                add();
            }
        } else {
            add();
        }
    }

    @SuppressWarnings("unchecked")
    public void add() {
        initSelected();
        selected = getList().getSelectedValuesList();
        if (selected.isEmpty()) {
            int[] indices = getSecondList().getSelectedIndices();
            for (int n : indices) {
                // getList().getData().add
                E element = ((DefaultListModel<E>) getSecondList().getModel()).get(n);
                ((DefaultListModel<E>) getSecondList().getModel()).add(n, element);
            }
            return;

        }
        for (E element : selected) {
            if (getVarClass() != null) {
                initVarTypes(element);
            }
            if ((varTypes != null || checkVarTYPE()) && (element instanceof String)) {
                String stringElement = (String) element;

                String variables = getVariables(element);

                List<Object> varTypes = getVarTypes();
                if (varTypes == null) {
                    varTypes = (List<Object>) getVarTypes(stringElement);
                }

                if (StringMaster.isEmpty(variables)) {
                    variables = "variable";
                }

                String vars = VariableManager.promptInputForVariables(variables, varTypes);
                if (vars == null) {
                    continue;
                }
                stringElement += vars;
                element = (E) stringElement;
                // TODO String only?

            }
            ((DefaultListModel<E>) getSecondList().getModel()).addElement(element);
        }
    }

    private boolean checkVarTYPE() {
        return TYPE == DC_TYPE.ABILS;
    }

    private List<?> getVarTypes(String stringElement) {
        ObjType type = DataManager.getType(stringElement, TYPE);
        String containerString = type.getProperty(G_PROPS.VARIABLE_TYPES);
        if (StringMaster.isEmpty(containerString)) {
            return null;
        }
        return StringMaster.openContainer(containerString);
    }

    private void initVarTypes(E element) {

        if (varClass.isEnum()) {
            Object e = EnumMaster.getEnumConst(varClass, element.toString());
            if (e instanceof VarHolder) {
                this.varHolder = (VarHolder) e;
                varTypes = new LinkedList<>(Arrays.asList(varHolder.getVarClasses()));
            }
        } else if (varClass == VariableManager.STRING_VAR_CLASS) {
            varTypes = new LinkedList<>(Arrays.asList(String.class));
        }
    }

    private String getVariables(E element) {
        ObjType newType = DataManager.getType(element.toString(), TYPE);
        String variables = null;
        if (newType != null) {
            variables = newType.getProperty(G_PROPS.VARIABLES);
        } else {
            if (varHolder != null) {
                variables = varHolder.getVariableNames();
            }
        }
        return variables;
    }

    public void moveDown() {
        initSelected();
        int[] newIndices = new int[selected.size()];
        int i = 0;
        for (E element : selected) {
            ((DefaultListModel<E>) getSecondList().getModel()).removeElement(element);
            ((DefaultListModel<E>) getSecondList().getModel()).add(
                    ((DefaultListModel<E>) getSecondList().getModel()).size(), element);
            newIndices[i] = selected.size() - i - 1;
        }
        getSecondList().setSelectedIndices(newIndices);
    }

    public void moveUp() {
        move(true);
    }

    public void move(boolean up) {
        initSelected();

        int[] selectedIndices = getSecondList().getSelectedIndices();
        int[] newIndices = new int[selectedIndices.length];
        int i = 0;
        while (true) {
            int n =
                    // (up) ? i :
                    selectedIndices.length - 1 - i;
            if (n > selectedIndices.length - 1 || n < 0) {
                break;
            }
            int index = selectedIndices[n];
            if (up) {
                index += i;
            }
            E element = getSecondList().getModel().getElementAt(index);
            ((DefaultListModel<E>) getSecondList().getModel()).removeElementAt(index);

            int newIndex = (up) ? 0 : getSecondList().getData().size() - 1;
            ((DefaultListModel<E>) getSecondList().getModel()).add(newIndex, element);
            // newIndices[i] = i;
            i++;
        }
        newIndices = up ? ArrayMaster.getIntArrayBetween(0, i) : ArrayMaster.getIntArrayBetween(
                getSecondList().getData().size() - 1 - i, getSecondList().getData().size() - 1);
        // new ListMaster<Integer>().getal
        getSecondList().setSelectedIndices(newIndices);
    }

    public void moveOne(boolean up) {
        int[] selectedIndices = getSecondList().getSelectedIndices();
        int[] newIndices = new int[selectedIndices.length];
        int i = 0;
        while (true) {
            int n = ((up) ? i : selectedIndices.length - 1 - i);
            if (n > selectedIndices.length - 1 || n < 0) {
                break;
            }
            int index = selectedIndices[n];
            E element = getSecondList().getModel().getElementAt(index);
            // try {
            // index = getSecondList().getSelectedIndex();
            // element = getSecondList().getData(). get(index);
            // } catch (Exception e) {
            // return;
            // }

            int newIndex = index + ((up) ? -1 : +1);
            if (newIndex < 0 || newIndex > getSecondList().getModel().getSize() - 1) {
                break;// TODO
            }
            ((DefaultListModel<E>) getSecondList().getModel()).remove(index);
            ((DefaultListModel<E>) getSecondList().getModel()).add(newIndex, element);
            // TODO via selectedValues?
            newIndices[i] = newIndex; // +((up) ? -1 : 1) * i;
            i++;
        }
        getSecondList().setSelectedIndices(newIndices);
    }

    public List<Object> getVarTypes() {
        return varTypes;
    }

    public void setVarTypes(List<Object> varTypes) {
        this.varTypes = varTypes;
    }

    public Class<?> getVarClass() {
        return varClass;
    }

    public void setVarClass(Class<?> varClass) {
        this.varClass = varClass;
    }

    public G_List<E> getSecondList() {
        return secondList;
    }

    public void setSecondList(G_List<E> secondList) {
        this.secondList = secondList;
    }

    public G_List<E> getList() {
        return list;
    }

    public void setList(G_List<E> list) {
        this.list = list;
    }

}
