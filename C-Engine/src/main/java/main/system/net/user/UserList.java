package main.system.net.user;

import main.swing.generic.components.list.GenericList;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.net.RefresherImpl;
import main.system.net.RefresherImpl.REFRESHER_TYPE;
import main.system.net.WaitingThread;
import main.system.net.socket.Connector;
import main.system.net.socket.ServerConnector;
import main.system.net.socket.ServerConnector.CODES;
import main.system.net.socket.ServerConnector.NetCode;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.Vector;

public class UserList extends GenericList<User> {

    private RefresherImpl refresher;
    private NetCode code = CODES.ONLINE_USERS_LIST;
    private REFRESHER_TYPE type = REFRESHER_TYPE.USERLIST;
    private boolean custom;
    private boolean host;
    private Connector connector;

    public UserList() {
        this.setCellRenderer(this);
        addListSelectionListener(this);
        launchUserListRefreshingThread();
        // add(new User());
    }

    public UserList(boolean host, Connector connector) {
        this.host = host;
        this.custom = true;
        this.setCellRenderer(this);
        this.connector = connector;
        addListSelectionListener(this);
    }

    public void launchUserListRefreshingThread() {
        this.refresher = new RefresherImpl(type, this);

    }

    public void setData(String input) {
        String[] usrs = input.split(StringMaster.getDataUnitSeparator());
        System.out.println("DATA: " + input);
        Vector<User> v = new Vector<>();
        for (String user : usrs) {

            v.add(new User(user));
        }

        System.out.println(v.toString());

        this.setListData(v);
        setList(v);

        this.getParent().revalidate();
        this.getParent().repaint();
        this.repaint();
        this.revalidate();
        // Err.warn(this.getList().toString());
        System.out.println(this.getList().toString());
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        LogMaster.log(1, ((UserList) e.getSource())
         .getSelectedValue().getData().replace(";", "\n"));

    }

    @Override
    public Component getListCellRendererComponent(JList<? extends User> list, User value, int index, boolean isSelected, boolean cellHasFocus) {
        return new JLabel(value.getName() + ":" + value.getIP());
    }

    @Override
    public void refresh() {
        if (custom) {
            if (host) {

                return;
            }
        }
        if (connector != null) {
            connector.send(code);
        } else {
            ServerConnector.send(code);
        }
        if (ServerConnector.launchInputWaitingThread(code)) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    setData(WaitingThread.getINPUT(code));

                }

            });
        }
    }

    public NetCode getCode() {
        return code;
    }

    public void setCode(NetCode code) {
        this.code = code;
    }

    public REFRESHER_TYPE getType() {
        return type;
    }

    public void setType(REFRESHER_TYPE type) {
        this.type = type;
    }

}
