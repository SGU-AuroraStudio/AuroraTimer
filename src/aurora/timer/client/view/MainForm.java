package aurora.timer.client.view;

import aurora.timer.client.ServerURL;
import aurora.timer.client.service.TimerYeah;
import aurora.timer.client.service.UserDataService;
import aurora.timer.client.service.UserOnlineTimeService;
import aurora.timer.client.vo.UserData;
import aurora.timer.client.vo.UserOnlineTime;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Time;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;
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
    private JPanel welcome;
    private JTable thisWeekList;
    private JPanel thisWeekPanel;
    private JMenuBar jMenuBar;
    private JMenu countMenu;
    private JMenuItem logoutItem;
    private JMenu aboutMenu;
    private JMenuItem aboutItem;
    private UserData data;
    private UserOnlineTime onlineTime;
    private TimerYeah addTimeThread;
    private TrayIcon trayIcon;
    private SystemTray systemTray;
    private Logger logger = Logger.getLogger("Main");

    public MainForm() {
        init();
    }

    public void setAddTimeThread(TimerYeah addTimeThread) {
        this.addTimeThread = addTimeThread;
    }

    public void setData(JSONObject object) {
        //获取
        data = new UserData();
        onlineTime = new UserOnlineTime();
        data.setID((String)object.get("id"));
        data.setNickName((String)object.get("name"));
        data.setDisplayURL((String)object.get("disp"));
        data.setTelNumber((String)object.get("tel"));
        data.setShortTelNumber((String)object.get("stel"));
        onlineTime.setID((String)object.get("id"));
        onlineTime.setTodayOnlineTime(Long.decode((String)object.getOrDefault("totime","0")));
        onlineTime.setLastOnlineTime(new Time((Long)object.getOrDefault("laslog",Long.decode("0"))));
    }

    public void init() {
        loadTable();
        addBar();
        refreshThisWeekList();
        loadSystemTray();
    }

    public void loadTable() {
        System.out.println(thisWeekList.getColumnCount());
    }

    public void loadSystemTray() {
        if (!SystemTray.isSupported()) {
            return;
        }
        systemTray = SystemTray.getSystemTray();
        PopupMenu popupMenu = new PopupMenu();
        MenuItem restoreItem = new MenuItem("还原");
        MenuItem exitItem  = new MenuItem("退出");

        restoreItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!FRAME.isVisible()) {
                    FRAME.setVisible(true);
                }
            }
        });
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        popupMenu.add(restoreItem);
        popupMenu.addSeparator();
        popupMenu.add(exitItem);

        try {
            trayIcon = new TrayIcon(ImageIO.read(new File("res" +
                    File.separator + "trayIcon.png")),"哦哈哟～",popupMenu);
            trayIcon.addMouseListener(new MouseAdapter() {
                /**
                 * {@inheritDoc}
                 * @param e
                 */
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton()==MouseEvent.BUTTON1 && !FRAME.isVisible()) {
                        FRAME.setVisible(true);
                    }
                }
            });
            systemTray.add(trayIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                TimerYeah.addTime(data.getID());
                addTimeThread.isStop = false;
                LoginForm.main(new String[1]);
                FRAME.dispose();
                logger.info("已退出帐号");
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
    }

    public static void main(String[] args) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        FRAME = new JFrame("Hello, " + args[0]);
        MainForm mainForm = new MainForm();
        mainForm.addBar();

        UserOnlineTimeService uots = new UserOnlineTimeService();
        UserDataService uds = new UserDataService();
        mainForm.setAddTimeThread(uots.startTimer(args[0])); //将后台发送计时请求的加载
        mainForm.setData(uds.findById(args[0])); //将用户信息加载
        FRAME.setContentPane(mainForm.parent);
        FRAME.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        int height = 518;
        int width = 700;
        FRAME.setBounds((d.width-width)/2, (d.height-height)/2, width, height);
        FRAME.setResizable(false);
//        FRAME.pack();
        FRAME.setVisible(true);
    }
}
