package aurora.timer.client.view;

import aurora.timer.client.ServerURL;
import aurora.timer.client.service.UserOnlineTimeService;
import aurora.timer.client.vo.UserOnlineTime;
import sun.applet.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

/**
 * Created by hao on 17-1-26.
 */
public class MainForm {
    private static JFrame FRAME = null;
    private JPanel parent;
    private JLabel myTimeLabel;
    private JTabbedPane panelGroup;
    private JPanel rightPanel;
    private JPanel leftPanel;
    private JPanel todayTimePanel;
    private JPanel me;
    private JPanel week;
    private JPanel welcome;
    private JMenuBar jMenuBar;
    private JMenu countMenu;
    private JMenuItem logoutItem;
    private JMenu aboutMenu;
    private JMenuItem aboutItem;

    public MainForm() {
        init();
    }

    public void init() {
        addBar();
    }

    public void addBar() {
        jMenuBar = new JMenuBar();
        countMenu = new JMenu("账户");
        aboutMenu = new JMenu("关于");
        aboutItem = new JMenuItem("说明");
        logoutItem = new JMenuItem("注销");

        zuce();

        countMenu.add(logoutItem);
        aboutMenu.add(aboutItem);
        jMenuBar.add(countMenu);
        jMenuBar.add(aboutMenu);
        FRAME.setJMenuBar(jMenuBar);
    }

    public void zuce(){
        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Preferences preferences = Preferences.userRoot().node(ServerURL.PREPATH);
                preferences.putBoolean("auto", false);
                LoginForm.main(new String[1]);
                FRAME.dispose();
            }
        });

        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, ServerURL.ABOUT, "关于",
                        JOptionPane.INFORMATION_MESSAGE, new ImageIcon("res/geass.png"));
            }
        });
    }

    public static void main(String[] args) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        FRAME = new JFrame("Hello, " + args[0]);
        MainForm mainForm = new MainForm();
        mainForm.addBar();

        UserOnlineTimeService uots = new UserOnlineTimeService();
        uots.startTimer(args[0]);
        FRAME.setContentPane(mainForm.parent);
        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int height = 518;
        int width = 700;
        FRAME.setBounds((d.width-width)/2, (d.height-height)/2, width, height);
        FRAME.setResizable(false);
//        FRAME.pack();
        FRAME.setVisible(true);
    }
}
