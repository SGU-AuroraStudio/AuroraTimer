package aurora.timer.client.view;

import aurora.timer.client.service.AdminDataService;
import aurora.timer.client.view.baseUI.login.LoginButtonUI;
import aurora.timer.client.view.util.TableUntil;
import aurora.timer.client.vo.AdminData;
import aurora.timer.client.vo.UserData;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Yao on 20-10-29
 */
public class WorkForm {
    public static JFrame FRAME;
    public JTextArea announceText;
    public JPanel workPanel;
    public JScrollPane jspAnnounce;
    public JButton announceBtn;
    public JTable dutyList;
//    public JScrollPane jspDutyList;
    public JButton submitBtn;
    private JPanel dutyListPanel;
    private UserData userData;

    public WorkForm() {
        init();
    }

    public UserData getUserData() {
        return userData;
    }

    /**
     * 设置和判断是不是管理员
     *
     * @param userData 当前用户信息
     */
    public void setUserData(UserData userData) {
        this.userData = userData;
        // 判断是不是管理员
        if (userData.getIsAdmin()) {
            announceText.setEditable(true);
            dutyList.setEnabled(true);
            submitBtn.setEnabled(true);
            submitBtn.setVisible(true);
        }
    }

    public void init() {
        announceBtn.setUI(new LoginButtonUI());


        // 点击parent取消编辑表格
        workPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (dutyList.isEditing())
                    dutyList.getCellEditor().stopCellEditing();
                dutyList.clearSelection();
                workPanel.setFocusable(true);
                workPanel.requestFocus();
            }
        });

        // 公告栏输入框
        announceText.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 18));
        announceText.setLineWrap(true);
        announceText.setOpaque(true);
        announceText.setBackground(new Color(200, 200, 200, 140));
        announceText.setForeground(Color.black);
//        announceText.setBackground(new Color(200, 200, 200, 100));
        //撤回功能
        UndoManager undoManager = new UndoManager();
        announceText.getDocument().addUndoableEditListener(undoManager);
        announceText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {
                    if (undoManager.canUndo()) {
                        undoManager.undo();
                    }
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Y) {
                    if (undoManager.canRedo()) {
                        undoManager.redo();
                    }
                }
            }
        });

        // 公告栏获取焦点，停止表格输入
        announceText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (dutyList.isEditing())
                    dutyList.getCellEditor().stopCellEditing();
                jspAnnounce.setBorder(BorderFactory.createLineBorder(new Color(86, 180, 130)));
                announceText.setBackground(new Color(255,255,255));
                dutyList.clearSelection();

            }

            @Override
            public void focusLost(FocusEvent e) {
                jspAnnounce.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
                announceText.setBackground(new Color(200, 200, 200, 140));
            }
        });

        // 公告栏外面的框架
        jspAnnounce.setViewportBorder(null);
        jspAnnounce.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        jspAnnounce.getViewport().setOpaque(false);//将中间的viewport设置为透明
        jspAnnounce.setOpaque(false);//将JScrollPane设置为透明

        // 表格
        // 设置默认数据
        Object[] columnNames = {"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Object[][] data = {{"阿巴", "阿巴", "阿巴", "阿巴", "阿巴", "阿巴", "阿巴"}};
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        dutyList.setModel(model);
        dutyList.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 18));
//        dutyList.setBackground(new Color(0, 0, 0, 0));
        dutyList.setBackground(new Color(184, 207, 229, 150));


//        dutyList.setSelectionBackground(new Color(184, 207, 229, 150));
//        dutyList.setSelectionBackground(new Color(86, 209, 149));
        DefaultTableCellRenderer defaultTableCellRenderer = (DefaultTableCellRenderer) dutyList.getDefaultRenderer(Object.class);
        defaultTableCellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
//        TableUntil.setOneRowBackgroundColor(dutyList,Color.black);


        //TODO:点击表格外的位置取消选中

        // 表头
        JTableHeader tableHeader = dutyList.getTableHeader();
        tableHeader.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 20));
        tableHeader.setBackground(new Color(86, 209, 149, 140));
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(false);
        DefaultTableCellRenderer headerTCR = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
        headerTCR.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        dutyListPanel.add(tableHeader, BorderLayout.NORTH);



        // 表格外面的框架
//        jspDutyList.getViewport().setOpaque(false);//将JScrollPane设置为透明
//        jspDutyList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
//        jspDutyList.setOpaque(false);

        // 管理员用的提交按钮
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminDataService ads = new AdminDataService();
                AdminData vo = new AdminData();
                vo.setId(userData.getID());
                vo.setPassword(userData.getPassWord());
                vo.setAnnouncement(announceText.getText());
                TableModel tableModel = dutyList.getModel();
                StringBuilder dutyListStr = new StringBuilder();
                // 读取值日表
                for (int i = 0; i < 7; i++) {
                    dutyListStr.append(tableModel.getValueAt(0, i));
                    if (i != 6)
                        dutyListStr.append("|");
                }
                vo.setDutylist(dutyListStr.toString());
                // 上传
                if (ads.uploadAdminData(vo, userData)) {
                    JOptionPane.showMessageDialog(null, "上传成功！\n", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

    }

    // 从服务器加载信息
    public boolean loadWorkInfo() {
        AdminDataService adms = new AdminDataService();
        AdminData data = adms.getAdminData();
        if (data == null) {
            return false;
        }
        announceText.setText(data.getAnnouncement());
        String dutyListData[][] = new String[1][7];
        String testData[] = data.getDutylist().split("\\|"); // 注意要双斜杆
        dutyListData[0] = testData;
        Object[] columnNames = {"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        DefaultTableModel model = new DefaultTableModel(dutyListData, columnNames);
        dutyList.setModel(model);
        return true;
    }

//    public static void main(String[] args) {
//        FRAME = new JFrame();
//        WorkForm workForm=new WorkForm();
//        workForm.loadWorkInfo();
//        FRAME.add(workForm.settingPanel);
//        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//        FRAME.setSize(565,400);
//        FRAME.setBounds((d.width - FRAME.getWidth()) / 2, (d.height - FRAME.getHeight()) / 2, FRAME.getWidth(), FRAME.getHeight());
//        FRAME.setVisible(true);
//        FRAME.setAlwaysOnTop(true);
//        FRAME.setAlwaysOnTop(false);
//    }
}

