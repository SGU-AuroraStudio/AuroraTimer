package aurora.timer.client.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class WorkForm {
    public static JFrame FRAME;
    public JTextArea announceText;
    public JPanel parent;
    public JScrollPane jspAnnounce;
    public JButton announceButton;
    private JTable dutyList;
    private JScrollPane jspDutyList;
    private static final int MAX_CHARACTERS = 300;
    public WorkForm(){
        parent.setOpaque(false);

        // 设置表头，和数据
        Object[] columnNames = {"星期天","星期一","星期二","星期三","星期四","星期五","星期六"};
        Object[][] data = {{"张三","张三","张三","张三","张三","张三","张三"}};
        DefaultTableModel model = new DefaultTableModel(data,columnNames);
        dutyList.setModel(model);
        // 公告栏输入框
        announceText.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 18));
        announceText.setLineWrap(true);
        announceButton.setUI(new LoginButtonUI());
        // 公告栏外面的框架
        jspAnnounce.setViewportBorder(null);
        jspAnnounce.setBorder(BorderFactory.createEtchedBorder());
        jspAnnounce.getViewport().setOpaque(false);//将JScrollPane设置为透明
        jspAnnounce.setOpaque(false);//将中间的viewport设置为透明
        // 表格
        dutyList.setOpaque(false);
        dutyList.setBorder(null);
        dutyList.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 16));
        dutyList.setBackground(new Color(200, 200, 200, 100));
        DefaultTableCellRenderer defaultTableCellRenderer = (DefaultTableCellRenderer) dutyList.getDefaultRenderer(Object.class);
        defaultTableCellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        //TODO:点击表格外的位置取消选中
        //TODO:管理员用的提交按钮

        // 表头
        JTableHeader tableHeader = dutyList.getTableHeader();
        tableHeader.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 20));
        tableHeader.setBackground(new Color(200, 200, 200, 100));
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(false);
        DefaultTableCellRenderer headerTCR = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
        headerTCR.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        // 表格外面的框架
        jspDutyList.getViewport().setOpaque(false);//将JScrollPane设置为透明
        jspDutyList.setOpaque(false);
    }


    public static void main(String[] args) {
        FRAME = new JFrame();
        WorkForm workForm=new WorkForm();
        FRAME.add(workForm.parent);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        FRAME.setSize(565,400);
        FRAME.setBounds((d.width - FRAME.getWidth()) / 2, (d.height - FRAME.getHeight()) / 2, FRAME.getWidth(), FRAME.getHeight());
        FRAME.setVisible(true);
    }
}

