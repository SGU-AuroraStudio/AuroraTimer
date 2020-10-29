package aurora.timer.client.view;

import aurora.timer.client.ServerURL;
import aurora.timer.client.service.TimerYeah;
import aurora.timer.client.service.UserDataService;
import aurora.timer.client.service.UserOnlineTimeService;
import aurora.timer.client.view.until.TableUntil;
import aurora.timer.client.vo.UserData;
import aurora.timer.client.vo.UserOnlineTime;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicPanelUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Time;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * Created by hao on 17-2-22.
 */
public class Main2Form {
    private static MainFrame FRAME;
    private JPanel parent;
    private JButton minButton;
    private JButton outButton;
    private JPanel timePanel;
    private JPanel headPanel;
    private JLabel timeLabel;
    private JButton changeButton;
    private JButton settingButton;
    private TrayIcon trayIcon;
    private SystemTray systemTray;
    private Long thisWeekTime = 0L;
    private UserOnlineTime onlineTime;
    private UserData userData;
    private Timer freshAddTimer; // 用来加时的计时器
    private Timer paintTimer; // 用来不停的画的计时器
    private JPanel weekAllPane; // 指向周计时的panel
    private JTable thisWeekList; // 指向本周计时的表
    Point mousePoint; //鼠标位置，判断挂机用
    WeekInfoForm weekInfoForm; //用来查看周计时的panel
    Vector<UserOnlineTime> userOnlineTimes; //本周时间所有人的集合，本周时间存在u.todayOnlineTime
    String[] theRedPerson;
    int page; //查看周计时的页面
    int mx, my, jfx, jfy; //鼠标位置，给自己设置的拖动窗口用的
    Logger logger = Logger.getLogger("MAIN");

    /**
     * 初始化函数
     */
    public void init() {
        page = 0;
        loadSystemTray();
        mousePoint = MouseInfo.getPointerInfo().getLocation();
        weekInfoForm = new WeekInfoForm();
        weekInfoForm.changeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() - 24;
                int y = e.getY() - 25;
//                System.out.print(x + " " + y);
                if (!(x + y <= 55 || x + y >= 165 || x - y >= 55 || x - y <= -55)) {
                    parent.remove(weekAllPane);
                    parent.repaint();
                    timePanel.setVisible(true);
                }
            }
        });
        weekInfoForm.leftButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (page <= 5) {
                    page++;
                    loadWeekTime(page);
                    setAllTime();
                }
            }
        });
        weekInfoForm.rightButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (page > 0) {
                    page--;
                    loadWeekTime(page);
                    setAllTime();
                }
            }
        });
        thisWeekList = weekInfoForm.weekList;
        weekAllPane = weekInfoForm.parent;

        parent.setUI(new MainParentPanelUI());
        minButton.setUI(new LoginButtonUI());
        outButton.setUI(new LoginButtonUI());
        changeButton.setUI(new LoginButtonUI());
        changeButton.setContentAreaFilled(false);
        timePanel.setUI(new BasicPanelUI() {
            @Override
            public void update(Graphics g, JComponent c) {
                super.update(g, c);
                c.setSize(565, 780);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 40, 50, 140));
                g2.fillOval(c.getWidth() / 2 - 242, 38, 484, 484);

                g2.setStroke(new BasicStroke(26));
                //如果时间大于24小时，那么进度条就要画金黄色
                if (Integer.parseInt(parseTime(thisWeekTime).split(":")[0]) >= 24) {
                    g2.setColor(new Color(88, 222, 234, 200));
                    g2.drawArc(c.getWidth() / 2 - 187, 86, 375, 375, 0, 360);

                    g2.setColor(new Color(251, 216, 96, 255));
                } else {
                    g2.setColor(new Color(12, 96, 108, 140));
                    g2.drawArc(c.getWidth() / 2 - 187, 86, 375, 375, 0, 360);

                    g2.setColor(new Color(88, 222, 234, 200));
                }
                int angel = (int) (thisWeekTime / (60 * 1000 * 4)); //转成分，每分钟0.25度
                angel = -(angel % 360); //这是drawArc的角度
                g2.drawArc(timePanel.getWidth() / 2 - 187, 86, 375, 375, 90, angel);
                g2.setColor(Color.white);
                g2.drawArc(timePanel.getWidth() / 2 - 187, 86, 375, 375, angel + 89, 1);
            }
        });
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 120));
    }

    /**
     * 将userOnlineTimes中的数据画到表上
     */
    public void setAllTime() {
        Iterator<UserOnlineTime> uiIt = userOnlineTimes.iterator();
        DefaultTableModel model = (DefaultTableModel) thisWeekList.getModel();
        if (page == 0) {
            thisWeekList.getColumnModel().getColumn(1).setHeaderValue("本周在线总时间");
        } else {
            thisWeekList.getColumnModel().getColumn(1).setHeaderValue("前" + page + "周在线总时间");
        }
        int index;
        for (index = model.getRowCount() - 1; index >= 0; index--) {
            model.removeRow(index);
        }

        //使用list存储并排序
        java.util.List<UserOnlineTime> list = new LinkedList<>();
        while (uiIt.hasNext()) {
            UserOnlineTime t = uiIt.next();
            list.add(t);
        }
        list.sort(new Comparator<UserOnlineTime>() {
            @Override
            public int compare(UserOnlineTime o1, UserOnlineTime o2) {
                if (o1.getTodayOnlineTime() > o2.getTodayOnlineTime()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        uiIt = list.iterator();

        //显示出来
        int redListFlag = 0;
        int[] redList = new int[theRedPerson.length];
        while (uiIt.hasNext()) {
            UserOnlineTime t = uiIt.next();
            for (int i = 0; i < theRedPerson.length; i++) {
                if (t.getID().equals(theRedPerson[i])) {
                    redList[redListFlag] = model.getRowCount();
                    redListFlag++;
                }
            }
            try {
                byte[] bytes = t.getName().getBytes();
                String s = new String(bytes, "utf-8");
                model.addRow(new Object[]{"   " + s.substring(0, s.length() - 1), "   " + parseTime(t.getTodayOnlineTime())});
            } catch (Exception e) {
                e.printStackTrace();
            }
            //将红名的index集合传入变色
        }
        if (page == 0) {
            TableUntil.setOneRowBackgroundColor(thisWeekList, redList, new Color(255, 77, 93, 150), page);
        } else {
            TableUntil.setOneRowBackgroundColor(thisWeekList, new int[0], Color.black, page);
        }
        parent.repaint();
    }

    /**
     * 获取用户信息
     * 这里之前写的时候就把servlet中一次返回了userData和userOnlineTime
     *
     * @param id 用户ID
     */
    public void loadUserData(String id) {
        UserDataService uds = new UserDataService();
        JSONObject object = uds.findById(id);
        userData = new UserData();
        onlineTime = new UserOnlineTime();
        userData.setID((String) object.get("id"));
        userData.setNickName((String) object.get("name"));
        userData.setDisplayURL((String) object.get("disp"));
        userData.setTelNumber((String) object.get("tel"));
        userData.setShortTelNumber((String) object.get("stel"));
        onlineTime.setID((String) object.get("id"));
        onlineTime.setTodayOnlineTime(Long.decode((String) object.getOrDefault("totime", "0")));
        onlineTime.setLastOnlineTime(new Time((Long) object.getOrDefault("laslog", Long.decode("0"))));
    }

    /**
     * 一次性加载前lastX周所有人的计时
     *
     * @param lastX 表示前第多少周，0表示本周
     */
    public void loadWeekTime(int lastX) {
        UserOnlineTimeService service = new UserOnlineTimeService();
        userOnlineTimes = service.getLastXWeekTime(lastX);
        Iterator<UserOnlineTime> uiIt = userOnlineTimes.iterator();
        UserOnlineTime uot;
        //当加载的周计时为0的时候刷新本地的周计时
        logger.info("加载第" + lastX + "周计时数据");
        while (uiIt.hasNext() && lastX == 0) {
            uot = uiIt.next();
            if (uot.getID().equals(userData.getID())) {
                thisWeekTime = uot.getTodayOnlineTime();
                break;
            }
        }
    }

    /**
     * 后台计时，每隔24分钟提交一次
     */
    public void backAddTime() {
        freshAddTimer = new Timer(5 * 60 * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    TimerYeah.addTime(userData.getID());
                    loadUserData(userData.getID());
                    loadWeekTime(0);
                } catch (Throwable throwable) {
                    JOptionPane.showMessageDialog(null, "计时线程异常，请检查网络或者服务器\n", "提示", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        freshAddTimer.setRepeats(true);
        freshAddTimer.start();

        Timer checkTimer = new Timer(24 * 60 * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //挂机检测，就是鼠标24分钟前后在相同位置则暂停加时，在对话框被取消后继续加时
                if (MouseInfo.getPointerInfo().getLocation().equals(mousePoint)) {
                    // TODO:每次在停止计时前向服务器询问时间，如果在挂机时间内，就不执行停止计时（为防止在比如18：01向服务器询问，然后停止计时，应该将挂机时间段设置多一点点）
                    freshAddTimer.stop();
                    createDialog();//打开提示框，此时计时线程会停止
                    freshAddTimer.start();
                }
                mousePoint = MouseInfo.getPointerInfo().getLocation();
            }
        });
        checkTimer.setRepeats(true);
        checkTimer.start();
    }

    /**
     * 创建检测到挂机时候的dialog
     */
    public void createDialog() {
        String[] option = {"不在", "不在"};
        int o = JOptionPane.showOptionDialog(null, "在？", "提示", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, option, option[0]);
    }

    /**
     * 画界面的Timer
     */
    public void backPaintTime() {
        paintTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thisWeekTime += 1000;
                timePanel.repaint();
                timeLabel.setText(parseTime(thisWeekTime));
            }
        });
        paintTimer.setRepeats(true);
        paintTimer.start();
    }

    /**
     * 将Long转换成 时:分 的字符串
     *
     * @param time Long型的时间
     * @return 转换后的字符串
     */
    public String parseTime(Long time) {
        StringBuffer sb = new StringBuffer("");
        if (time / 3600000 < 10) {
            sb.append("0");
        }
        sb.append((time / 3600000) + ":");
        if (time % 3600000 / 60000 < 10) {
            sb.append("0");
        }
        sb.append(time % 3600000 / 60000);
        return sb.toString();
    }

    /**
     * 构造函数，进行初始化和开启Timer
     */
    public Main2Form() {
        init();
        backAddTime();
        backPaintTime();

        //缩小到托盘按钮
        minButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                FRAME.setVisible(false);
            }
        });
        //关闭按钮
        outButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onExit();
            }
        });
        //设置拖动
        headPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mx = e.getX();
                my = e.getY();
                jfx = headPanel.getX();
                jfy = headPanel.getY();
            }
        });
        headPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                FRAME.setLocation(jfx + (e.getXOnScreen() - mx), jfy + (e.getYOnScreen() - my));
            }
        });
        changeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() - 227;
                int y = e.getY() - 25;
                if (!(x + y < 55 || x + y > 165 || x - y > 55 || x - y < -55)) {
                    timePanel.setVisible(false);
                    TimerYeah.addTime(userData.getID());
                    loadWeekTime(0);
                    setAllTime();
                    parent.add(weekAllPane);
                    parent.repaint();
                }
            }

        });
        settingButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SettingForm.main(new String[0]);
            }
        });
    }

    /**
     * 加载托盘图标
     */
    public void loadSystemTray() {
        if (!SystemTray.isSupported()) {
            return;
        }
        systemTray = SystemTray.getSystemTray();
        PopupMenu popupMenu = new PopupMenu();
        popupMenu.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 10));
        Menu pluginMenu = new Menu("插件");
        MenuItem restoreItem = new MenuItem("还原");
        MenuItem logoutItem = new MenuItem("注销");
        MenuItem exitItem = new MenuItem("退出");

        // 通过反射加载插件
//         try {
//             File pluginFile = new File("src/plugins");
//             if (pluginFile.isDirectory() && pluginFile.exists()) {
//                 File[] files = pluginFile.listFiles(new FileFilter() {
//                     @Override
//                     public boolean accept(File pathname) {
//                         String name = pathname.getName();
//                         if (name.contains(".class")) {
//                             return true;
//                         }
//                         return false;
//                     }
//                 });
//                 URL[] urls;
//                 if (files.length != 0 && files != null) {
//                     System.out.println("存在url");
//                     urls = new URL[files.length];
//                     for (int i = 0; i < files.length; i++) {
//                         urls[i] = files[i].toURI().toURL();
//                     }
//                     URLClassLoader classLoader = new URLClassLoader(urls);
//                     Class<?> plugin = classLoader.loadClass("aurora.timer.client.plugin.TimerPlugin");
//                     Method startMethod = plugin.getMethod("getName");
//                     String he = (String) startMethod.invoke(plugin.newInstance());
//                     //TODO:The Plugin System
//                 }
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }

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
                onExit();
            }
        });
        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Preferences preferences = Preferences.userRoot().node(ServerURL.PRE_PATH);
                preferences.putBoolean("auto", false);
                TimerYeah.addTime(userData.getID());
                if (freshAddTimer.isRunning()) {
                    freshAddTimer.stop();
                }
                if (paintTimer.isRunning()) {
                    paintTimer.stop();
                }
                FRAME.dispose();
                systemTray.remove(trayIcon);
                LoginForm.main(new String[1]);
            }
        });

        popupMenu.add(restoreItem);
        popupMenu.addSeparator();
        popupMenu.add(logoutItem);
//        popupMenu.addSeparator();
//        popupMenu.add(pluginMenu);
        popupMenu.addSeparator();
        popupMenu.add(exitItem);

        try {
            trayIcon = new TrayIcon(ImageIO.read(getClass().getResource("trayIcon.png")));
            trayIcon.setPopupMenu(popupMenu);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1 && !FRAME.isVisible()) {
                        FRAME.setVisible(true);
//                        systemTray.remove(trayIcon);
                    }
                }
            });
            systemTray.add(trayIcon);
        } catch (Exception e) {
            logger.warning("托盘初始化失败");
        }
    }

    public void onExit() {
        String[] option = {"退出", "取消"};
        int isExit = JOptionPane.showOptionDialog(null, "是否退出计时器？", "提示", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, option, option[0]);
        if (isExit == 0) {
            System.exit(2);
        }
    }

    public void setLastWeekRedPerson(int x) {
        loadWeekTime(1);
        Iterator<UserOnlineTime> uiIt = userOnlineTimes.iterator();

        //使用list存储并排序
        java.util.List<UserOnlineTime> list = new LinkedList<>();
        while (uiIt.hasNext()) {
            UserOnlineTime t = uiIt.next();
            list.add(t);
        }
        list.sort(new Comparator<UserOnlineTime>() {
            @Override
            public int compare(UserOnlineTime o1, UserOnlineTime o2) {
                if (o1.getTodayOnlineTime() > o2.getTodayOnlineTime()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        uiIt = list.iterator();
        theRedPerson = new String[x];
        for (int i = 0; i < x; i++) {
            if (uiIt.hasNext()) {
                theRedPerson[i] = uiIt.next().getID();
//                System.out.println(theRedPerson[i]);
            }
        }
    }

    public static void main(String args[]) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        try {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    FRAME = new MainFrame();
                    Main2Form main2Form = new Main2Form();
                    main2Form.loadUserData(args[0]);

                    //设置上周前N名至theRedPerson
                    main2Form.setLastWeekRedPerson(3);

                    main2Form.loadWeekTime(0);

                    FRAME.setContentPane(main2Form.parent);
                    FRAME.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                    FRAME.setLocation((d.width - FRAME.getWidth()) / 2, (d.height - FRAME.getHeight()) / 2);
//                    FRAME.setBounds((d.width - FRAME.getWidth()) / 2, (d.height - FRAME.getHeight()) / 2, FRAME.getWidth(), FRAME.getHeight());
                    FRAME.setResizable(false);
                    FRAME.setVisible(true);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
