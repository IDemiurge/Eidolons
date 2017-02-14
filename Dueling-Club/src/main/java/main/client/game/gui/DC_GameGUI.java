package main.client.game.gui;

import main.client.DuelingClub;
import main.client.dc.Launcher;
import main.game.DC_Game;
import main.game.DC_Game.GAME_MODES;
import main.swing.builders.DC_Builder;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.editors.ImageChooser;
import main.swing.generic.windows.G_Frame;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class DC_GameGUI implements MouseListener {
    public static final int BF_WIDTH = 9;
    public static final int BF_HEIGHT = 5;
    private static final String ICON_PATH = "UI\\Death Combat.png"
            // "UI\\PentagramX3.png"
            ;
    private static final String SV_POS = "pos 400 400";

    private JFrame window;
    private G_Panel panel;
    private JLabel bg;
    private DC_Builder bfBuilder;
    private JComponent bfComp;
    private DC_Game game;
    // private SceneViewer sv;
    private boolean fullscreen;
    private boolean standalone;
    private Image backgroundOverlays;
    private String backgroundPath;

    public DC_GameGUI(DC_Game game, boolean fullscreen) {
        this(game, fullscreen, true);
    }

    public DC_GameGUI(DC_Game game, boolean fullscreen, boolean standalone) {
        this.fullscreen = fullscreen;
        GuiManager.setFullscreen(fullscreen);
        this.game = game;
        this.standalone = standalone;

    }

    public static Image createBackgroundOverlay() {
        Image img = ImageManager.getNewBufferedImage(GuiManager.getScreenWidthInt(), GuiManager
                .getScreenHeightInt());
        int width = img.getWidth(null);
        int height = img.getHeight(null);

        Image applied = ImageManager.getImage("ui\\bf\\bf frame vertical stripe.png");
        img = ImageManager.applyImage(img, applied, 0, 0, false, false);
        img = ImageManager
                .applyImage(img, applied, width - applied.getWidth(null), 0, false, false);
        applied = ImageManager.getImage("ui\\bf\\bf frame horizontal stripe.png");
        // img = ImageManager.applyImage(img, applied, 0, 0, false, false);
        img = ImageManager.applyImage(img, applied, 0, height - applied.getHeight(null), false,
                false);
        //
        applied = ImageManager.getImage("ui\\bf\\bf frame corner.png");
        img = ImageManager.applyImage(img, applied, 0, height - applied.getHeight(null), false,
                false);
        applied = main.system.graphics.ImageTransformer.flipHorizontally(ImageManager
                .getBufferedImage(applied));
        img = ImageManager.applyImage(img, applied, width - applied.getWidth(null), height
                - applied.getHeight(null), false, false);

        applied = main.system.graphics.ImageTransformer.flipVertically(ImageManager
                .getBufferedImage(applied));
        img = ImageManager
                .applyImage(img, applied, width - applied.getWidth(null), 0, false, false);
        applied = main.system.graphics.ImageTransformer.flipHorizontally(ImageManager
                .getBufferedImage(applied));
        img = ImageManager.applyImage(img, applied, 0, 0, false, false);

        return img;
    }

    public static String getIconPath() {
        return ICON_PATH;
    }

    public static int getBfWidth() {
        return BF_WIDTH;
    }

    public static int getBfHeight() {
        return BF_HEIGHT;
    }

    //

    public static String getSvPos() {
        return SV_POS;
    }

    public void initGUI() {
        game.setGUI(this);
        // G_Panel.setPanelSize(GuiManager.getSize());

        bfBuilder = (DC_Builder) game.getBattleField().getBuilder();
        initMainPanel();
        if (standalone) {
            initMainWindow();
        }

    }

    private void initMainPanel() {
        setPanel(new G_Panel());
        getPanel().setIgnoreRepaint(true);
        getPanel().setOpaque(false);

        initBattleField();
    }

    private void initBattleField() {
        // WaitMaster.waitForInput(WAIT_OPERATIONS.GUI_READY);
        bfComp = bfBuilder.getComp();
        ((G_Panel) bfComp).setPanelSize(GuiManager.DEF_DIMENSION);
        initBackground();
        getPanel().add(bfComp);
        setDefaultComponentZOrder();

    }

    private void setDefaultComponentZOrder() {
        getPanel().setComponentZOrder(bfComp, 0);
        getPanel().setComponentZOrder(bg, 1);

    }

    private void initBackground() {
        ImageIcon icon = getBackgroundPic();

        bg = new JLabel(icon);
        bg.addMouseListener(this);
        getPanel().add(bg, "pos 0 0 container.x2 container.y2");
    }

    public void resetBgIcon(String string) {
        if (!ImageManager.isImage(string)) {
            return;
        }
        if (getBg() == null) {
            return;
        }
        backgroundPath = string;
        getBg().setIcon(getBackgroundPic(string));

    }

    private ImageIcon getBackgroundPic() {
        return getBackgroundPic(null);
    }

    // public void setSceneViewer(SceneViewer sv) {
    // // bf sized
    // this.sv = sv;
    // }
    //
    // public void showSceneViewer(boolean fullscreen) {
    // addSceneViewer(fullscreen);
    // sv.activate();
    // }
    //
    // private void addSceneViewer(boolean fullscreen) {
    // panel.add(sv.getComp(), (fullscreen) ? "pos 0 0" : SV_POS);
    // panel.setComponentZOrder(sv.getComp(), 0);
    // panel.setComponentZOrder(bfComp, 1);
    // panel.setComponentZOrder(lbl, 2);
    // }
    //
    // public void hideSceneViewer() {
    // removeSceneViewer();
    // setDefaultComponentZOrder();
    // }
    //
    // private void removeSceneViewer() {
    // panel.remove(sv.getComp());
    //
    // }

    private ImageIcon getBackgroundPic(String path) {
        if (path == null) {

            path = ImageManager.DEFAULT_BACKGROUND;
            try {
                path = ((DC_Builder) game.getBattleField().getBuilder()).getGrid().getMap()
                 .getBackground();
            } catch (Exception e) {
                LogMaster.log(1, "failed to load background!");
            }
            if (game.getGameMode() == GAME_MODES.ARENA) {
                path = game.getArenaManager().getMap().getBackground();
            }
        }
        int height = GuiManager.getScreenHeightInt();
        int width = GuiManager.getScreenWidthInt();
        BufferedImage bgPic = ImageManager.getNewBufferedImage(width, height);
        Graphics graphics = bgPic.getGraphics();
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, width, height);

        Image img = ImageManager.getSizedIcon(path, GuiManager.DEF_DIMENSION).getImage();
        if (!ImageManager.isValidImage(img)) {
            img = ImageManager.getImage(ImageManager.DEFAULT_BACKGROUND);
            // main.system.auxiliary.LogMaster.log(1, "no background! " + path);
            // throw new RuntimeException();
        }
        if (backgroundOverlays == null) {
            backgroundOverlays = createBackgroundOverlay();
        }
        img = ImageManager.applyImage(img, backgroundOverlays, 0, 0, false, false);

        bgPic = (BufferedImage) ImageManager.applyImage(bgPic, img, ImageManager.CENTERED,
         ImageManager.CENTERED, false, false);
        return new ImageIcon(bgPic);

    }

    private void initMainWindow() {
        window = new G_Frame(DuelingClub.GAME_TITLE);
        window.setSize(GuiManager.getScreenSize());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // window.addWindowListener(this);
        window.setLocationRelativeTo(null);
        window.setLayout(new GridLayout());
        window.add(getPanel());
        // if (fullscreen)
        window.setUndecorated(true);
        window.setVisible(true);
        // window.setIgnoreRepaint(true);
        setCustomIcon();
        if (fullscreen) {
            setFullscreen();
        }

    }

    private void setFullscreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        GraphicsDevice device = null;
        int WIDTH = -1;
        for (int i = 0; i < gd.length; i++) {
            DisplayMode dm = gd[i].getDisplayMode();
            int screenWidth = dm.getWidth();
            // int screenHeight = dm.getHeight();
            if (screenWidth > WIDTH) {
                device = gd[i];
                WIDTH = screenWidth;
            }
        }

        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        device.setFullScreenWindow(window);
    }

    private void setCustomIcon() {
        ImageIcon img = ImageManager.getIcon(ICON_PATH);

        window.setIconImage(img.getImage());
    }

    public G_Panel getPanel() {
        return panel;
    }

    public void setPanel(G_Panel panel) {
        this.panel = panel;
    }

    public JFrame getWindow() {
        return window;
    }

    public void setWindow(JFrame window) {
        this.window = window;
    }

    public JLabel getBg() {
        return bg;
    }

    public DC_Builder getBfBuilder() {
        return bfBuilder;
    }

    public JComponent getBfComp() {
        return bfComp;
    }

    public DC_Game getGame() {
        return game;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public boolean isStandalone() {
        return standalone;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.isAltDown()) {
            if (Launcher.DEV_MODE) {
                String portrait = new ImageChooser().launch("Image", backgroundPath);
                if (portrait != null) {
                    if (ImageManager.isImage(portrait)) {
                        resetBgIcon(portrait);
                    }
                }

            }
        }

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

}
