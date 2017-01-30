package aurora.timer.client.view;

import aurora.timer.client.ServerURL;
import aurora.timer.client.service.UserOnlineTimeService;
import aurora.timer.client.vo.UserData;
import aurora.timer.client.vo.UserOnlineTime;
import sun.applet.Main;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;
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
    private JList thisWeekList;
    private JMenuBar jMenuBar;
    private JMenu countMenu;
    private JMenuItem logoutItem;
    private JMenu aboutMenu;
    private JMenuItem aboutItem;
    private UserData data;
    private UserOnlineTime onlineTime;

    public MainForm() {
        init();
    }

    public void init() {
        addBar();
        refreshThisWeekList();
    }

    public void addBar() {
        jMenuBar = new JMenuBar();
        countMenu = new JMenu("账户");
        aboutMenu = new JMenu("关于");
        aboutItem = new JMenuItem("说明");
        logoutItem = new JMenuItem("注销");

        zhuce();

        countMenu.add(logoutItem);
        aboutMenu.add(aboutItem);
        jMenuBar.add(countMenu);
        jMenuBar.add(aboutMenu);
        FRAME.setJMenuBar(jMenuBar);
    }

    public void zhuce(){
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
                JOptionPane.INFORMATION_MESSAGE, new ImageIcon("res" + File.separator + "geass.png"));
            }
        });
    }

    public void refreshThisWeekList() {
        UserOnlineTimeService service = new UserOnlineTimeService();
        Vector<UserOnlineTime> userOnlineTimes = service.getThisWeekTime();
        Vector<String> vector = new Vector<>();
        Iterator<UserOnlineTime> uiIt = userOnlineTimes.iterator();
        while (uiIt.hasNext()) {
            UserOnlineTime t = uiIt.next();
            //这里要减去格林威治时间和本地相差的8小时。。当然，如果国际化的话就不是这么写的了。。
            String s = t.getID() + " : " + new Time(t.getTodayOnlineTime()).toLocalTime().minusHours(Long.decode("8"));
            vector.add(s);
        }
        thisWeekList.setListData(vector);
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
