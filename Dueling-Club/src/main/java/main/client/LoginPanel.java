package main.client;

import main.system.net.socket.ServerConnector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private static final String LOGIN = "Log In";
    private static final String BACK = "Back";
    private JLabel lbl;
    private JLabel plbl;
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton backButton;
    private DC_MainMenu menu;
    private Box box;

    public LoginPanel(DC_MainMenu menu) {
        this.menu = menu;
        initLoginBox();

        this.add(box, "pos 0 0");

        initLoginButton();

        this.add(loginButton, "pos 0 0");
        initBackButton();

        this.add(backButton, "pos 0 0");
        checkAdmin();

    }

    private void checkAdmin() {
        if (DuelingClub.ADMIN_MODE) {
            userField.setText(DuelingClub.ADMIN_LOGIN);
            passwordField.setText(DuelingClub.ADMIN_PASSWORD);
            loginButton.grabFocus();
            login();
        } else {
            userField.grabFocus();
        }

    }

    public void login() {
        loginButton.doClick();
    }

    private void initLoginBox() {
        box = Box.createVerticalBox();
        box.add(lbl = new JLabel("Username:"));
        box.add(setUserField(new JTextField("")));
        box.add(Box.createVerticalStrut(10));
        box.add(plbl = new JLabel("Password:"));
        box.add(setPasswordField(new JPasswordField(15)));

    }

    private void initLoginButton() {
        this.loginButton = new JButton(LOGIN);
        this.loginButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String username = getUserField().getText();
                ServerConnector.setTempUserName(username);
                String data = username;
                data += ":" + getPasswordField().getText().toString();

                DuelingClub.login(menu, data);
            }
        });

    }

    private void initBackButton() {
        this.backButton = new JButton(BACK);

        this.backButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                menu.setMenuView();
            }
        });
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JPasswordField setPasswordField(JPasswordField passwordField) {
        this.passwordField = passwordField;
        return passwordField;
    }

    public JTextField getUserField() {
        return userField;
    }

    public JTextField setUserField(JTextField userField) {
        this.userField = userField;
        return userField;
    }

}
