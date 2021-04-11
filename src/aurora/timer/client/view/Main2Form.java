package aurora.timer.client.view;

import aurora.timer.client.ServerURL;
import aurora.timer.client.service.AdminDataService;
import aurora.timer.client.service.TimerYeah;
import aurora.timer.client.service.UserDataService;
import aurora.timer.client.service.UserOnlineTimeService;
import aurora.timer.client.view.util.SaveBg;
import aurora.timer.client.view.util.TableUntil;
import aurora.timer.client.vo.UserData;
import aurora.timer.client.vo.UserOnlineTime;
import jdk.nashorn.internal.scripts.JO;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicPanelUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * Created by hao on 17-2-22.
 * Updated by Yao on 20-12-02.
 */
public class Main2Form {
    public static UserData userData;
    public static CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel parent;
    private static MainFrame FRAME;
    private JButton minButton;
    private JButton outButton;
    private JPanel timePanel;
    private JPanel headPanel;
    private JLabel timeLabel;
    private JButton changeButton;
    private JButton settingButton;
    private JPanel clockPanel;
    private TrayIcon trayIcon;
    private SystemTray systemTray;
    private Long thisWeekTime = 0L;
    private Timer freshAddTimer; // 用来加时的计时器
    private Timer paintTimer; // 用来不停的画的计时器
    private JTable thisWeekList; // 指向本周计时的表
    private Point mousePoint; //鼠标位置，判断挂机用
    private WeekInfoForm weekInfoForm; //用来查看周计时的panel
    private WorkForm workForm;
    private SettingForm settingForm;
    private List<UserOnlineTime> userOnlineTimes; //本周时间所有人的集合，本周时间存在u.todayOnlineTime
    private UserOnlineTimeService userOnlineTimeService;
    private String[] theRedPerson;
    private boolean loadingWeekTime = false; //是否正在加载计时，防止多次按钮多次加载，浪费资源
    private int page; //查看周计时的页面
    private int pageLimited = 20; //查看上x周最大值
    private int mx, my, jfx, jfy; //鼠标位置，给自己设置的拖动窗口用的
    Logger logger = Logger.getLogger("MAIN");

    /**
     * 构造函数，进行初始化和开启Timer
     */
    public Main2Form() {
        // 加载背景图片地址，在MainParentPanelUI里会用 设置背景图,所以要在这之前从服务器加载背景图片。loadBg要用到id所以要在loadUserData之后
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadBg();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        userOnlineTimeService = new UserOnlineTimeService();
        cardLayout = (CardLayout) cardPanel.getLayout();
        loadUserData(userData.getID());
        initMain2Form();
        initWeekInfoForm();
        initWorkForm();
        initSettingForm();
        backAddTime();
        backPaintTime();
        TimerYeah.addTime(userData.getID());
    }

    /**
     * 初始化函数
     */
    private void initMain2Form() {
        page = 0;
        loadSystemTray();
        mousePoint = MouseInfo.getPointerInfo().getLocation();
//        settingForm = new SettingForm(settingPanel, settingButton, userData);
//        settingForm.setTimePanel(timePanel);
//        settingForm.setWeekAllPane(weekAllPane);
        //TODO:bug:timePanel时间圆盘在点切换后会下移(发现是timeLabel变长了)
        changeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(loadingWeekTime || !TimerYeah.addTime(userData.getID()))
                            return;
                        loadWeekTime(0);
                        cardLayout.show(cardPanel, "weekInfoPanel");
                        setAllTime();
                    }
                }).start();
            }
        });
        settingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                settingButton.setEnabled(false);
                cardLayout.show(cardPanel, "settingPanel");
            }
        });

        minButton.setUI(new LoginButtonUI());
        outButton.setUI(new LoginButtonUI());
        changeButton.setUI(new LoginButtonUI());
        changeButton.setContentAreaFilled(false);

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

    private void initWorkForm() {
        workForm = new WorkForm();
        workForm.setUserData(userData);
        cardPanel.add(workForm.workPanel, "workPanel");
        workForm.announceBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //settingPanel.remove(weekAllPane);
                cardLayout.show(cardPanel, "weekInfoPanel");
            }
        });
    }

    private void initSettingForm(){
        settingForm = new SettingForm(parent, cardPanel, userData);
        cardPanel.add(settingForm.settingPanel, "settingPanel");
    }

    private void initWeekInfoForm() {
        weekInfoForm = new WeekInfoForm();
        thisWeekList = weekInfoForm.weekList;
        cardPanel.add(weekInfoForm.weekInfoPanel, "weekInfoPanel");
        weekInfoForm.changeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Card1");
            }
        });
        weekInfoForm.leftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (page < pageLimited && !loadingWeekTime) {
                            page++;
                            loadWeekTime(page);
                            setAllTime();
                        }
                    }
                }).start();
            }
        });
        weekInfoForm.rightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (page > 0 && !loadingWeekTime) {
                            page--;
                            loadWeekTime(page);
                            setAllTime();
                        }
                    }
                }).start();
            }
        });
        weekInfoForm.announceBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if (x < 56 || y < 56 || x > 324 || y > 110)
                    return;
                if (workForm.loadWorkInfo()) {
                    cardLayout.show(cardPanel, "workPanel");
                }
            }
        });
    }

    /**
     * 将userOnlineTimes中的数据画到表上
     */
    public void setAllTime() {
        // 如果这一页为空白，上限就是这一页了
        if (userOnlineTimes.size() == 0)
            pageLimited = page;
        DefaultTableModel model = (DefaultTableModel) thisWeekList.getModel();
        if (page == 0) {
            thisWeekList.getColumnModel().getColumn(2).setHeaderValue("本周在线总时间");
        } else {
            thisWeekList.getColumnModel().getColumn(2).setHeaderValue("前" + page + "周在线总时间");
        }
        int index;
        for (index = model.getRowCount() - 1; index >= 0; index--) {
            model.removeRow(index);
        }
        //使用list存储并排序
        List<UserOnlineTime> list = userOnlineTimes;
        list.sort((o1,o2)->{
            if (o1.getTodayOnlineTime() > o2.getTodayOnlineTime()) {
                return -1;
            } else {
                return 1;
            }
        });
        //显示出来
        int redListFlag = 0;
        int[] redList = new int[theRedPerson.length];
        for (UserOnlineTime t : list) {
            for (String redPerson : theRedPerson) {
                if (t.getID().equals(redPerson)) {
                    redList[redListFlag] = model.getRowCount();
                    redListFlag++;
                }
            }
            try {
                byte[] bytes = t.getName().getBytes();
                // 解决编码问题
                String s = null;
                if (System.getProperty("os.name").contains("Windows")) //Windows用GBK，MAC用UTF-8。MAC调试的时候依然乱码，但是打包后就正常。
                    s = new String(bytes, "GBK");
                else
                    s = new String(bytes, StandardCharsets.UTF_8);
                model.addRow(new Object[]{"   " + s.substring(0, s.length() - 1), parseTime(t.getTermOnlineTime()), "   " + parseTime(t.getTodayOnlineTime())}); //填入表格
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //将红名的index集合传入变色
        if (page == 0) {
            TableUntil.setOneRowBackgroundColor(thisWeekList, redList, new Color(255, 77, 93, 150), page);
        } else {
            TableUntil.setOneRowBackgroundColor(thisWeekList, new int[0], Color.black, page);
        }
        weekInfoForm.weekInfoPanel.repaint();
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
        UserOnlineTime onlineTime = new UserOnlineTime();
        userData.setID((String) object.get("id"));
        userData.setIsAdmin((Boolean)object.get("isAdmin"));
        userData.setNickName((String) object.get("name"));
//        userData.setDisplayURL((String) object.get("disp"));
//        userData.setTelNumber((String) object.get("tel"));
//        userData.setShortTelNumber((String) object.get("stel"));
        onlineTime.setID((String) object.get("id"));
        onlineTime.setTodayOnlineTime(Long.decode((String) object.getOrDefault("totime", "0")));
        onlineTime.setLastOnlineTime(new Time((Long) object.getOrDefault("laslog", 0l)));
    }

    /**
     * 一次性加载前lastX周所有人的计时
     *
     * @param lastX 表示前第多少周，0表示本周
     */
    public void loadWeekTime(int lastX) {
        loadingWeekTime = true;
        userOnlineTimes = userOnlineTimeService.getLastXWeekTime(lastX);
        if(userOnlineTimes==null){
            return;
        }
        //当加载的周计时为0的时候刷新本地的周计时
        logger.info("加载第" + lastX + "周计时数据");
        for (UserOnlineTime userOnlineTime : userOnlineTimes) {
            if (userOnlineTime.getID().equals(userData.getID())) {
                thisWeekTime = userOnlineTime.getTodayOnlineTime();
                break;
            }
        }
        loadingWeekTime = false;
    }

    public void loadBg() throws IOException {
        Preferences preferences = Preferences.userRoot().node(ServerURL.PRE_PATH);
        UserDataService uds = new UserDataService();
        InputStream bg = uds.findBgByid(userData.getID(), userData.getPassWord());
        // 图片不存在或者返回数据过小，失败
        if (bg == null || bg.available() < 1000) {
            logger.warning("从服务器加载背景图片失败");
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null, "从服务器加载背景图片失败，请检查网络或者服务器\n或因为服务器没有您的背景图", "提示", JOptionPane.ERROR_MESSAGE);
                }
            });
        } else {
            String bgPath = System.getProperty("java.io.tmpdir") + File.separator + userData.getID() + "_bg.png";
            if (SaveBg.saveBg(bgPath, bg, true)) {
                // 修改注册表
                preferences.put("bg", bgPath);
                logger.info("从服务器加载背景图片");
            }
        }
        // 优先从注册表里读取，没有就设置为默认。（在上边从服务器读取到到话会改注册表）
        parent.setUI(new MainParentPanelUI());
    }

    /**
     * 后台计时，每隔24分钟提交一次
     */
    public void backAddTime() {
        freshAddTimer = new Timer(5 * 60 * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!TimerYeah.addTime(userData.getID()))
                    return;
                loadUserData(userData.getID());
                loadWeekTime(0);
            }
        });
        freshAddTimer.setRepeats(true);
        freshAddTimer.start();

        Timer checkTimer = new Timer(24 * 60 * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //挂机检测，就是鼠标24分钟前后在相同位置则暂停加时，在对话框被取消后继续加时
                if (MouseInfo.getPointerInfo().getLocation().equals(mousePoint)) {
                    freshAddTimer.stop();
                    AdminDataService ads = new AdminDataService();
                    // 如果不是自由时间，就弹出对话框
                    if (!ads.isFreeTime())
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
        MenuItem aboutItem = new MenuItem("关于");
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
        //TODO:关于窗口
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame aboutFrame = new JFrame();
                aboutFrame.setSize(200, 100);
                JLabel label = new JLabel();
                label.setText("功能待完善");
                JTextArea textArea = new JTextArea();
                textArea.append("功能待完善");
                aboutFrame.add(label);
                Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
                aboutFrame.setLocation((d.width - aboutFrame.getWidth()) / 2, (d.height - aboutFrame.getHeight()) / 2);
                System.out.println(FRAME.getX());
                System.out.println(FRAME.getY());
                aboutFrame.setResizable(false);
                aboutFrame.setVisible(true);
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
        popupMenu.addSeparator();
//        popupMenu.add(pluginMenu);
//        popupMenu.addSeparator();
        popupMenu.add(aboutItem);
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
        //使用list存储并排序
        List<UserOnlineTime> list = userOnlineTimeService.getLastXWeekTime(1);
        list.sort((o1, o2) -> {
            if (o1.getTodayOnlineTime() > o2.getTodayOnlineTime()) {
                return -1;
            } else {
                return 1;
            }
        });
        Iterator<UserOnlineTime> uiIt = list.iterator();
        theRedPerson = new String[x];
        for (int i = 0; i < x; i++) {
            if (uiIt.hasNext()) {
                theRedPerson[i] = list.get(i).getID();
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
                    FRAME = new MainFrame("极光");
                    Main2Form main2Form = new Main2Form();
                    //设置上周前N名至theRedPerson
                    main2Form.loadWeekTime(0);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            main2Form.setLastWeekRedPerson(3);
                        }
                    }).start();
                    FRAME.setContentPane(main2Form.parent);
                    FRAME.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                    FRAME.setLocation((d.width - FRAME.getWidth()) / 2, (d.height - FRAME.getHeight()) / 2);
//                    FRAME.setBounds((d.width - FRAME.getWidth()) / 2, (d.height - FRAME.getHeight()) / 2, FRAME.getWidth(), FRAME.getHeight());
                    FRAME.setResizable(false);
                    FRAME.setAlwaysOnTop(true);
                    FRAME.setVisible(true);
                    FRAME.setAlwaysOnTop(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
