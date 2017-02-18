package main.system.net;

import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.Err;
import main.system.images.ImageManager;
import main.system.net.socket.GenericConnection;
import main.system.net.socket.ServerConnector;
import main.system.net.socket.ServerConnector.CODES;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.Calendar;

public class Viewer extends JPanel {

    private String prefix;
    private JTextArea ta;
    private JTextField tf;
    private JFrame frame;
    private Calendar calendar;
    private int i = 1;
    private int max = 60;
    private PrintWriter writer;
    private Class<?> enumClass;
    private GenericConnection connection;

    public Viewer(String prefix, Class<?> enumClass, GenericConnection connection) {
        this.prefix = prefix;
        this.connection = connection;
        this.ta = new JTextArea(i, 30);
        this.enumClass = enumClass;
        calendar = Calendar.getInstance();

        showAndCreateGUI();
        ta.setEditable(false);
    }

    public Viewer(String prefix) {
        this.prefix = prefix;
        this.ta = new JTextArea(i, 60);

        calendar = Calendar.getInstance();
        showAndCreateGUI();
        ta.setEditable(false);
    }

    private boolean isShowGui() {
        // TODO Auto-generated method stub
        return false;
    }

    private void showAndCreateGUI() {
        this.frame = new JFrame(prefix + " Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tf = new JTextField(20);
        add(tf);
        tf.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (writer == null) {
                    Err.warn("WRITER NOT SET");
                    return;
                }
                String s = tf.getText();
                writer.print(s + "\n");
                writer.flush();
                output(s);
                tf.selectAll();
            }

        });
        JScrollPane s = new JScrollPane(ta);

        add(s);
        frame.add(this);

        if (isShowGui()) {
            frame.setVisible(true);
        }

        frame.pack();
        // setCustomIcon();
    }

    private void setCustomIcon() {
        ImageIcon img = ImageManager.getIcon("UI\\" + "custom\\node.jpg");

        frame.setIconImage(img.getImage());
    }

    public void log(String text, String type) {
        int time = (int) ((int) Calendar.getInstance().getTime().getTime() - calendar.getTime()
                .getTime());
        String s = ta.getText() + "\n" + time / 1000 + " " + prefix + " " + type.toUpperCase()
                + ": " + text;
        ta.setRows(i);
        if (i < max) {
            i++;
        }
        ta.setText(s);
        frame.pack();
    }

    public void addMessagePanel(PrintWriter writer) {
        this.writer = writer;
        JPanel mspanel = new JPanel();
        Box box = Box.createVerticalBox();
        if (enumClass == null) {
            for (CODES c : CODES.values()) {
                addCodeButton(c, box, writer, this);
            }
        } else {
            for (String c : EnumMaster.getEnumConstantNames(enumClass)) {
                addCodeButton(c, box, writer, this);
            }
        }
        mspanel.add(box);
        this.add(mspanel);
        frame.pack();
    }

    private void addCodeButton(final String c, Box box, final PrintWriter w, final Viewer viewer) {
        this.writer = w;
        JButton b = new JButton(c);
        box.add(b);
        box.add(Box.createVerticalStrut(15));
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                w.print(c + "\n");
                w.flush();
                viewer.output(c);
                if (connection != null) {
                    connection.send(c);
                } else {
                    ServerConnector.send(c);
                }
            }
        });

    }

    private void addCodeButton(final CODES c, Box box, final PrintWriter w, final Viewer viewer) {
        addCodeButton(c.name(), box, w, viewer);
    }

    public void input(String input) {

        log(input, "input");
    }

    public void output(String input) {
        log(input, "output");
    }

    public void info(String input) {

        log(input, "info");
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

}
