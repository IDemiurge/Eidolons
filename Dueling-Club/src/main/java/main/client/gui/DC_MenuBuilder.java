package main.client.gui;

import main.client.DuelingClub;
import main.client.gui.main.ChatPanel;
import main.client.gui.main.ControlPanel;
import main.client.gui.main.GameListPanel;
import main.client.gui.main.UserListPanel;
import main.swing.generic.components.Builder;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.ColorManager;
import main.system.net.user.UserList;

/**
 * Menu items: Hero creator AV Play (Login to game server) => add userpanel,
 * online_users, gamelist etc
 * <p>
 * game tabs
 * <p>
 * info
 *
 * @author JustMe
 */
public class DC_MenuBuilder extends Builder {
    ChatPanel chatPanel;
    MainTabbedPanel tabbedPanel;
    UserListPanel userListPanel;
    GameListPanel gameListPanel;
    ControlPanel controlPanel;
    private UserList list;

    public DC_MenuBuilder() {
        list = new UserList();
    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init() {
        comp = new G_Panel();
        comp.setBackground(ColorManager
                .getTranslucent(ColorManager.OBSIDIAN, 0));
        userListPanel = new UserListPanel(list);

        gameListPanel = new GameListPanel();
        chatPanel = new ChatPanel(DuelingClub.SERVER_ADDRESS, true);
        controlPanel = new ControlPanel();
        compArray = new G_Component[]{userListPanel, chatPanel, controlPanel,
                gameListPanel,};
        cInfoArray = new String[]{
                "pos " + screenSize.width + "/4*3 15, id ulp",
                // "pos 0 15, w 250!, h 850!, id ulp",
                "pos 375 215 ulp.x " + screenSize.height + "-100, id chat",

                "pos " + screenSize.width + "/2 25, id cp",
                "id gameListPanel, pos 100 100 chat.x chat.y2",};
    }

}
