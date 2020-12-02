package aurora.timer.client.view;

import aurora.timer.client.service.AdminDataService;
import aurora.timer.client.vo.AdminData;
import aurora.timer.client.vo.UserData;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;

public class WorkForm {
    public static JFrame FRAME;
    public JTextArea announceText;
    public JPanel parent;
    public JScrollPane jspAnnounce;
    public JButton announceBtn;
    public JTable dutyList;
    public JScrollPane jspDutyList;
    public JButton submitBtn;
    private UserData userData;

    public WorkForm(){ init(); }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public void init(){
        announceBtn.setUI(new LoginButtonUI());
        // 点击parent取消编辑表格
        parent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (dutyList.isEditing())
                    dutyList.getCellEditor().stopCellEditing();
                announceText.setFocusable(false);
                announceText.setFocusable(true);
            }
        });
        // 公告栏输入框
        announceText.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 18));
        announceText.setLineWrap(true);
        announceText.setBackground(new Color(200, 200, 200, 100));
        // 公告栏获取焦点，停止表格输入
        announceText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (dutyList.isEditing())
                    dutyList.getCellEditor().stopCellEditing();
                jspAnnounce.setBorder(BorderFactory.createLineBorder(new Color(86, 180, 130)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                jspAnnounce.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));

            }
        });
        // 公告栏外面的框架
        jspAnnounce.setViewportBorder(null);
        jspAnnounce.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        jspAnnounce.getViewport().setOpaque(false);//将中间的viewport设置为透明
        jspAnnounce.setOpaque(false);//将JScrollPane设置为透明
        // 表格
        //   设置表头，和数据
        Object[] columnNames = {"星期天","星期一","星期二","星期三","星期四","星期五","星期六"};
        Object[][] data = {{"阿巴","阿巴","阿巴","阿巴","阿巴","阿巴","阿巴"}};
        DefaultTableModel model = new DefaultTableModel(data,columnNames);
        dutyList.setModel(model);
        dutyList.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 18));
        dutyList.setBackground(new Color(0, 0, 0, 0));
//        dutyList.setSelectionBackground(new Color(86, 209, 149));
        DefaultTableCellRenderer defaultTableCellRenderer = (DefaultTableCellRenderer) dutyList.getDefaultRenderer(Object.class);
        defaultTableCellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        //TODO:点击表格外的位置取消选中

        // 表头
        JTableHeader tableHeader = dutyList.getTableHeader();
        tableHeader.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 20));
        tableHeader.setBackground(new Color(86, 209, 149, 70));
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(false);
        DefaultTableCellRenderer headerTCR = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
        headerTCR.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        // 表格外面的框架
        jspDutyList.getViewport().setOpaque(false);//将JScrollPane设置为透明
        jspDutyList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
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
                for(int i=0;i<7;i++){
                    dutyListStr.append(tableModel.getValueAt(0, i));
                    if(i!=6)
                        dutyListStr.append("|");
                }
                vo.setDutylist(dutyListStr.toString());
                // 上传
                if(ads.uploadAdminData(vo, userData)){
                    JOptionPane.showMessageDialog(null, "上传成功！\n", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(null, "上传失败，请检查网络或者服务器\n", "提示", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        announceText.setBackground(new Color(200, 200, 200, 100));

    }

    // 从服务器加载信息
    public boolean loadWorkInfo(){
        AdminDataService adms = new AdminDataService();
        AdminData data = adms.getAdminData();
        if(data==null) {
            return false;
        }
        announceText.setText(data.getAnnouncement());
        String dutyListData[][] = new String[1][7];
        String testData[] = data.getDutylist().split("\\|"); // 注意要双斜杆
        dutyListData[0] = testData;
        Object[] columnNames = {"星期天","星期一","星期二","星期三","星期四","星期五","星期六"};
        DefaultTableModel model = new DefaultTableModel(dutyListData,columnNames);
        dutyList.setModel(model);
        return true;
    }

//    public static void main(String[] args) {
//        FRAME = new JFrame();
//        WorkForm workForm=new WorkForm();
//        workForm.loadWorkInfo();
//        FRAME.add(workForm.parent);
//        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//        FRAME.setSize(565,400);
//        FRAME.setBounds((d.width - FRAME.getWidth()) / 2, (d.height - FRAME.getHeight()) / 2, FRAME.getWidth(), FRAME.getHeight());
//        FRAME.setVisible(true);
//        FRAME.setAlwaysOnTop(true);
//        FRAME.setAlwaysOnTop(false);
//    }
}

