package aurora.timer.client.view;

import aurora.timer.client.ServerURL;
import aurora.timer.client.service.TimerYeah;
import aurora.timer.client.service.UserDataService;
import aurora.timer.client.vo.UserData;
import org.apache.commons.codec.digest.DigestUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.prefs.Preferences;

/**
 * Created by hao on 17-1-25.
 */
public class LoginForm {
    private static LoginFrame FRAME = null;
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
    int mx, my, jfx, jfy;

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
                        desktop.browse(new URI(ServerURL.REGISTER_URL));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "您的系统不支持跳转，请手动打开\n" +
                            ServerURL.REGISTER_URL, "提示", JOptionPane.ERROR_MESSAGE);
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
                loginLogic(); //因为loginButton.doClick()不知为何用不了，所以分离注册来复用代码
            }
        });

        autoLoginCheckBox.addItemListener(new ItemListener() {
            /**
             * 选中自动登陆当然就要自动选中记住密码嘛
             * @param e
             */
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (autoLoginCheckBox.isSelected()) {
                    remPasswordCheckBox.setSelected(true);
                    remPasswordCheckBox.setEnabled(false);
                } else {
                    remPasswordCheckBox.setEnabled(true);
                }
            }
        });
        parent.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (my < 20) {
                    FRAME.setLocation(jfx + (e.getXOnScreen() - mx), jfy + (e.getYOnScreen() - my));
                }
            }
        });

        parent.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mx = e.getX();
                my = e.getY();
                jfx = parent.getX();
                jfy = parent.getY();
                if (mx > 244 && mx < 261 && my < 20) {
                    System.exit(0);
                }
            }
        });
    }

    //初始化
    public void init() {
        parent.setUI(new LoginPanelUI());
        loginButton.setUI(new LoginButtonUI());
        registerButton.setUI(new LoginButtonUI());
        idText.setBorder(new EmptyBorder(0, 0, 0, 0));
        pwdText.setBorder(new EmptyBorder(0, 0, 0, 0));

        Preferences preferences = Preferences.userRoot().node(ServerURL.PRE_PATH);
        remPasswordCheckBox.setSelected(preferences.getBoolean("rem", false));
        autoLoginCheckBox.setSelected(preferences.getBoolean("auto", false));
        idText.setText(preferences.get("id", ""));
        if (remPasswordCheckBox.isSelected()) {
            pwdText.setText(preferences.get("pwd", ""));
        }
        if (autoLoginCheckBox.isSelected()) {
            remPasswordCheckBox.setEnabled(false);
        }
    }

    public void loginLogic() {
        UserDataService service = new UserDataService();
        UserData vo = new UserData();
        vo.setID(idText.getText());
        vo.setPassWord(DigestUtils.md5Hex(String.valueOf(pwdText.getPassword())).toString()); //转换成32位md5值
        if (service.LoginService(vo)) {
            String inf[] = new String[2];
            //传值给主界面
            inf[0] = vo.getID();
            inf[1] = vo.getPassWord();
            //存个档
            Preferences preferences = Preferences.userRoot().node(ServerURL.PRE_PATH);
            preferences.putBoolean("rem", remPasswordCheckBox.isSelected());
            preferences.putBoolean("auto", autoLoginCheckBox.isSelected());
            preferences.put("id", vo.getID());
            if (remPasswordCheckBox.isSelected()) {
                preferences.put("pwd", String.valueOf(pwdText.getPassword()));
            }
            //跳个转
//            MainForm.main(inf);
//            TimerYeah.addTime(vo.getID());
            Main2Form.main(inf);
            FRAME.dispose();
        } else {
            JOptionPane.showMessageDialog(null, "登陆失败", "提示", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String args[]) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        try {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    FRAME = new LoginFrame("极光");
                    LoginForm form = new LoginForm();
                    FRAME.setContentPane(form.parent);
                    int width = 270;
                    int height = 190;
                    FRAME.setBounds((d.width - width) / 2, (d.height - height) / 2, width, height);
                    FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                    FRAME.pack();
                    FRAME.setResizable(false);
                    FRAME.setVisible(true);
                    Preferences preferences = Preferences.userRoot().node(ServerURL.PRE_PATH);
                    if (preferences.getBoolean("auto", false)) {
                        form.loginLogic();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
