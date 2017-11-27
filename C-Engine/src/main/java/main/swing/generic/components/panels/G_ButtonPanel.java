package main.swing.generic.components.panels;

import main.swing.generic.components.G_Panel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public abstract class G_ButtonPanel extends G_Panel implements ActionListener {
    private static final String BASE_CONSTRAINT = "sg btn";
    protected int R_HEIGHT = 0;
    protected int C_WIDTH = 0;

    protected int columns;
    protected int maxRows = Integer.MAX_VALUE;
    protected boolean horizontal = true;

    public G_ButtonPanel(String[] commands) {
        this(new ArrayList<>(Arrays.asList(commands)));
    }

    public G_ButtonPanel(List<String> commands) {

        setInts();
        // setLayout(new MigLayout());
        setLayout(new MigLayout("wrap, " + (isHorizontal() ? "flowy" : "")));
        if (columns == 1 && maxRows == Integer.MAX_VALUE) {

            int i = 0;
            for (String command : commands) {
                addButton(command, BASE_CONSTRAINT);
                // (horizontal) ? (BASE_CONSTRAINT + i + " 0")
                // : (BASE_CONSTRAINT + "0 " + i));
                i++;
            }
        } else {
            int rows;
            if (columns != 1) {
                rows = commands.size() / columns + 1;
            } else {
                rows = Math.min(commands.size(), maxRows);
                columns = commands.size() / rows + 1;
            }
            Iterator<String> iterator = commands.iterator();
            boolean next = false;
            for (int x = 0; iterator.hasNext(); x++) {

                for (int y = 0; y < rows; y++) {
                    if (iterator.hasNext()) {
                        String constr = BASE_CONSTRAINT;
                        if (next) {
                            constr += ",wrap";
                        }
                        addButton(iterator.next(), constr);
                        next = false;
                    } else {
                        break;
                    }
                }
                next = true;
            }
        }
    }

    public int getColumns() {
        return 1;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    private void addButton(String command, String constraints) {

        JButton button;

        // icon =
        // if (icon !=null)
        // {
        //
        // }
        // else
        button = new JButton(command);

        button.setActionCommand(command);
        button.addActionListener(this);
        // button.setMnemonic(String.valueOf(y).toCharArray()[0]);
        add(button, constraints);
    }

    private String getPos(int column, int row) {
        int x = column * C_WIDTH;
        int y = row * R_HEIGHT;

        return x + " " + y;
    }

    protected void setInts() {
        columns = getColumns();
    }

}
