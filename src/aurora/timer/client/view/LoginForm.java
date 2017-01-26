package aurora.timer.client.view;

import aurora.timer.client.ServerURL;
import aurora.timer.client.service.UserDataService;
import aurora.timer.client.vo.UserData;
import org.apache.commons.codec.digest.DigestUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.prefs.Preferences;

/**
 * Created by hao on 17-1-25.
 */
public class LoginForm {
    private JButton registerButton;
    private JButton loginButton;
    private JPanel parent;
    private JPanel under;
    private JPasswordField pwdText;
    private JCheckBox remPasswordCheckBox;
    private JCheckBox autoLoginCheckBox;
    private JTextField idText;
    private JLabel count;
    private JLabel password;

    public LoginForm() {
        this.init();

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
             * @param e 鼠标单击事件
             * 登陆登陆登陆
             */
            @Override
            public void mouseClicked(MouseEvent e) {
//                UserDataService service = new UserDataService();
//                UserData vo = new UserData();
//                vo.setID(idText.getText());
//                vo.setPassWord(DigestUtils.md5Hex(String.valueOf(pwdText.getPassword())).toString()); //转换成32位md5值
//                if (service.LoginService(vo)) {
//                    String inf[] = new String[2];
//                    inf[0] = vo.getNickName();
//                    inf[1] = vo.getID();
//                    MainForm.main(inf);
//                } else {
//                    JOptionPane.showMessageDialog(null, "登陆失败", "提示", JOptionPane.ERROR_MESSAGE);
//                }
            }
        });
    }

    public void init() {
//        Preferences preferences = Preferences.userNodeForPackage(this.getClass());
//        remPasswordCheckBox.setSelected(preferences.getBoolean("rem", false));
//        loginButton.setIcon(new ImageIcon(this.getClass().getResource("/") + "res/but.png"));
//        ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("/") + "res/but.png");
//        System.out.println(imageIcon.getImage());
    }

    public static void main(String args[]) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("极光");
        frame.setContentPane(new LoginForm().parent);
        int width = 300;
        int height = 190;
        frame.setBounds((d.width-width)/2, (d.height-height)/2, width, height);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
    }

}
