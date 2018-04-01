package main.swing.generic.components.editors.abilities;

import main.content.ContentManager;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_TabbedPanel;
import main.swing.generic.components.list.G_List;
import main.system.math.ConstantManager;
import main.system.math.ConstantManager.CONSTANTS;
import main.system.math.FunctionManager;
import main.system.math.FunctionManager.FUNCTIONS;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;

public class FormulaBuilder implements ActionListener, ChangeListener {

    private static final String PROPS = "Properties";
    private static final String PARAMS = "Parameters";
    private static final String CONSTS = "Constants";
    private static final String FUNCS = "Functions";
    private static final String INSERT = "Insert";
    private static final String WRAP_REF = "Wrap Ref";
    private G_TabbedPanel tabs;
    private JTextField textField;
    private boolean initialized;
    private String buffer;
    private G_List currentList;
    private JPanel panel;

    public FormulaBuilder(JTextField textField) {
        this.textField = textField;
        buffer = textField.getText();

        initGUI();

        panel = new G_Panel();
        panel.add(textField, "id tf,pos 0 0");
        panel.add((tabs.getTabs()), "id tabs, pos 0 tf.y2, h 50%");
        initControls();

    }

    public String launch() {
        int result = JOptionPane
         .showConfirmDialog(textField.getParent(), panel);
        if (result != JOptionPane.YES_OPTION) {
            done();
            return textField.getText();
        }
        return null;
    }

    public void done() {
        textField.setText(buffer);
    }

    private void initControls() {
        JButton insertButton = new JButton(INSERT);
        insertButton.setActionCommand(INSERT);
        insertButton.addActionListener(this);
        panel.add(insertButton, "id insert, pos 0 tabs.y2");
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        switch (arg0.getActionCommand()) {
            case INSERT: {
                String string = getSelectedItemString();
                if (string != null) {
                    insert(string);
                }
                break;
            }
        }

    }

    private String getSelectedItemString() {

        return getStringValue(currentList.getSelectedValue());
    }

    private String getStringValue(Object selectedValue) {
        // if (selectedValue instanceof VALUE){
        // return ((VALUE)selectedValue).getShortName();
        // }
        if (selectedValue == null) {
            return null;
        }
        return selectedValue.toString();
    }

    public void insert(String string) {
        int index = textField.getCaretPosition();
        String text = textField.getText();
        text = text.substring(0, index) + string + text.substring(index);
        textField.setText(text);
    }

    private void initGUI() {
        tabs = new G_TabbedPanel();
        tabs.getTabs().addChangeListener(this);
        if (!initialized) {
            initTabs();
        }
    }

    private void initTabs() {
        initParamTab();
        initPropTab();
        initFuncTab();
        initConstTab();
        initialized = true;
    }

    private void initPropTab() {
        List<PROPERTY> data = ContentManager.getPropList();

        // TODO use pages? or enum consts?

        String title = PROPS;
        // tabs.addTab(c, title, "");

    }

    private void initParamTab() {
        Collection<PARAMETER> data = ContentManager.getParamList();
        G_List<PARAMETER> c = new G_List<>(data);
        G_Panel panel = new G_Panel();
        panel.add(c, "pos 0 0");
        String title = PARAMS;
        tabs.addTab(c, title, null);
    }

    private void initConstTab() {
        Collection<CONSTANTS> data = ConstantManager.getConstList();
        G_List<CONSTANTS> c = new G_List<>(data);
        String title = CONSTS;
        tabs.addTab(c, title, null);

    }

    private void initFuncTab() {
        Collection<FUNCTIONS> data = FunctionManager.getFunctionList();
        G_List<FUNCTIONS> c = new G_List<>(data);
        String title = FUNCS;
        tabs.addTab(c, title, null);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        currentList = (G_List) ((JTabbedPane) e.getSource())
         .getSelectedComponent();
    }

    public synchronized JTextField getTextField() {
        return textField;
    }

    public synchronized void setTextField(JTextField textField) {
        this.textField = textField;
    }

    public synchronized JPanel getPanel() {
        return panel;
    }

    public synchronized void setPanel(JPanel panel) {
        this.panel = panel;
    }
}
