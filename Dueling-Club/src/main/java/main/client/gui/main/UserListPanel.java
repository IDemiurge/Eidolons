package main.client.gui.main;

import main.swing.generic.components.G_Panel;
import main.system.net.user.UserList;

public class UserListPanel extends G_Panel {
    private UserList list;

    public UserListPanel(UserList list) {
        this.setUserList(list);
        add(list, "pos 0 0");

    }

    public UserList getUserList() {
        return list;
    }

    public void setUserList(UserList list) {
        this.list = list;
    }

}
