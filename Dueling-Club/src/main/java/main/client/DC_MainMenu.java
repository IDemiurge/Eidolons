package main.client;

import main.client.gui.DC_MenuBuilder;
import main.client.gui.menu.DC_Menu;
import main.swing.View;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;
import main.system.net.socket.ServerConnection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Random;

/**

 */
public class DC_MainMenu extends View {
    private static JFrame window;
    private static ServerConnection handler;
    private static DC_MainMenu MAIN_MENU;
    private JLabel background;
    private DC_MenuBuilder builder;
    private ImageIcon img;
    private JComponent menuComp;
    private DC_Menu menu;
    private LoginPanel loginPanel;
    private JComponent mainView;

    public DC_MainMenu() {

    }

    public static void createAndShowGUI() {
        MAIN_MENU = new DC_MainMenu();
        MAIN_MENU.initMainPanel();
        initMainWindow();
        MAIN_MENU.setMenuView();
    }

    private static void initMainWindow() {
        window = new JFrame(DuelingClub.GAME_TITLE);
        window.setSize(GuiManager.getScreenSize());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setLayout(new BorderLayout());

        window.add(MAIN_MENU.getComp());
        window.show();

        setCustomIcon();
    }

    private static void setCustomIcon() {
        ImageIcon img = ImageManager.getIcon("UI\\" + "PentagramX3.png");

        window.setIconImage(img.getImage());
    }

    public static void setHandler(ServerConnection handler_) {
        handler = handler_;
    }

    public void setMenuView() {
        add(menu, "pos container.x2/2 container.y2/2");
        if (DuelingClub.ADMIN_MODE) {
            menu.handleClick("Play");
        }
    }

    public void setMainView() {
        if (mainView == null) {
            initMainView();
        }
        add(mainView, "pos 0 0");

    }

    public void setLoginView() {
        if (loginPanel == null) {
            initLoginPanel();
        }
        add(loginPanel, "pos container.x2/2-100 container.y2/2-100");
        if (DuelingClub.ADMIN_MODE) {
            loginPanel.login();
        }
    }

    protected void addBackground(int i) {
        comp.add(background, "pos 0 0 container.x2 container.y2");
        comp.setComponentZOrder(background, i);
    }

    private void initLoginPanel() {
        loginPanel = new LoginPanel(this);
    }

    private void initMainPanel() {
        comp.setOpaque(false);
        // initBackGround();
        initMenu();

    }

    private void initMenu() {
        this.menu = new DC_Menu(this);

    }

    private void initMainView() {
        if (builder == null) {
            builder = new DC_MenuBuilder();
        }
        this.mainView = builder.build();
    }

    private void initBackGround() {

        background = new JLabel(getBackgroundPic());

    }

    private ImageIcon getBackgroundPic() {
        img = ImageManager.getIcon("big\\gatheringstorm.jpg");

        File f = new File(ImageManager.getDefaultImageLocation() + "big\\");
        if (f.isDirectory()) {
            String[] array = f.list();
            String filename = f.list()[new Random().nextInt(array.length)];
            img = ImageManager.getIcon("big\\" + filename);
        }
        return new ImageIcon(ImageManager.getSizedVersion(img.getImage(), GuiManager
                .getScreenSize()));

    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

    @Override
    public void initView() {
        // TODO Auto-generated method stub

    }

    @Override
    public void activateView() {
        // TODO Auto-generated method stub

    }

}
