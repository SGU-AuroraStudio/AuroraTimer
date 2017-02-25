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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
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
    private JScrollPane thisWeekPanel;
    private JLabel todayTime;
    private JPanel pluginPanel;
    private JLabel refreshLabel;
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
        refreshLabel.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             * @param e 刷新啊。
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                refreshAll();
            }
        });
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
        loadSystemTray();

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    public void refreshAll() {
        UserDataService uds = new UserDataService();
        TimerYeah.addTime(data.getID());
        this.setData(uds.findById(data.getID()));
        //这里要减去格林威治时间和本地相差的8小时。。当然，如果国际化的话就不是这么写的了。。
        todayTime.setText(new Time(onlineTime.getTodayOnlineTime()).toLocalTime().minusHours(Long.decode("8")).toString());
        refreshThisWeekList();
//        todayTime.setText(onlineTime.getTodayOnlineTime()/3600000+":"+(onlineTime.getTodayOnlineTime()%3600000)/60000);
    }

    public void loadTable() {
        DefaultTableModel model = (DefaultTableModel) thisWeekList.getModel();
        model.addColumn("ID");
        model.addColumn("本周在线总时间");
        thisWeekList.setEnabled(false);
        thisWeekList.setVisible(true);
    }

    //初始化托盘
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
            trayIcon = new TrayIcon(ImageIO.read(getClass().getResource("trayIcon.png")));
            trayIcon.setPopupMenu(popupMenu);
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
                Preferences preferences = Preferences.userRoot().node(ServerURL.PRE_PATH);
                preferences.putBoolean("auto", false);
                TimerYeah.addTime(data.getID());
                addTimeThread.isStop = false;
                LoginForm.main(new String[1]);
                FRAME.dispose();
                systemTray.remove(trayIcon);
                logger.info("已退出帐号");
            }
        });

        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, ServerURL.ABOUT, "关于",
                JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("geass.png"), "geass"));
            }
        });
    }

    //刷新本周计时统计那个表
    public void refreshThisWeekList() {
        UserOnlineTimeService service = new UserOnlineTimeService();
        Vector<UserOnlineTime> userOnlineTimes = service.getLastXWeekTime(0);
        Iterator<UserOnlineTime> uiIt = userOnlineTimes.iterator();
        DefaultTableModel model = (DefaultTableModel) thisWeekList.getModel();
        int index;
        for (index = model.getRowCount() - 1; index >= 0; index --) {
            model.removeRow(index);
        }
        while (uiIt.hasNext()) {
            UserOnlineTime t = uiIt.next();
//            System.out.println(t.getID()+":"+t.getTodayOnlineTime());
            //这里的todayOnlineTime是本周的时间，我也不造本周应该放哪,只好

//            String s = t.getID() + " : " + new Time(t.getTodayOnlineTime()).toLocalTime().minusHours(Long.decode("8"));
            StringBuffer sb = new StringBuffer("");
            if (t.getTodayOnlineTime()/3600000<10) {
                sb.append("0");
            }
            sb.append((t.getTodayOnlineTime()/3600000)+":");
            if (t.getTodayOnlineTime()%3600000/60000<10){
                sb.append("0");
            }
            sb.append(t.getTodayOnlineTime()%3600000/60000);
            model.addRow(new Object[]{t.getID(),sb});
        }
    }

    public static void main(String[] args) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        try {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    FRAME = new JFrame("Hello, " + args[0]);
                    MainForm mainForm = new MainForm();
                    mainForm.addBar();

                    UserOnlineTimeService uots = new UserOnlineTimeService();
                    UserDataService uds = new UserDataService();
                    mainForm.setAddTimeThread(uots.startTimer(args[0])); //将后台发送计时请求的加载
                    mainForm.setData(uds.findById(args[0])); //将用户信息加载
                    mainForm.refreshAll();
                    FRAME.setContentPane(mainForm.parent);
                    FRAME.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                    int height = 520;
                    int width = 700;
                    FRAME.setBounds((d.width - width) / 2, (d.height - height) / 2, width, height);
                    FRAME.setResizable(false);
//        FRAME.pack();
                    FRAME.setVisible(true);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
