package aurora.timer.client.view;

import aurora.timer.client.ServerURL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

/**
 * Created by hao on 17-1-25.
 */
public class LoginForm {
    private JButton registerButton;
    private JButton loginButton;
    private JPanel parent;
    private JPanel under;
    private JPasswordField passwordField;
    private JCheckBox remPasswordCheckBox;
    private JCheckBox autoLoginCheckBox;
    private JTextField countTextField;
    private JLabel count;
    private JLabel password;

    public LoginForm() {
        registerButton.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             * 用户注册，实现自动打开本地浏览器，进入web页面进行注册
             * @param e
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                Desktop desktop;
                if (Desktop.isDesktopSupported()) {
                    desktop = Desktop.getDesktop();
                    try {
                        desktop.browse(new URI(ServerURL.REGISTERURL));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "您的系统不支持跳转，请手动打开\n" +
                            ServerURL.REGISTERURL, "提示", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        loginButton.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mouseClicked(MouseEvent e) {

            }
        });
    }

    public static void main(String args[]) {
        JFrame frame = new JFrame("LoginForm");
        frame.setContentPane(new LoginForm().parent);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
