package aurora.timer.client.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;

public class WorkForm {
    private static JFrame FRAME;
    private JPanel parent;
    private JTable dutyList;
    private JTextField announceTextField;
    private JScrollPane jsp;

    public WorkForm(){

        Object[] columnNames = {"星期天","星期一","星期二","星期三","星期四","星期五","星期六"};
        Object[][] data = {{"张三","张三","张三","张三","张三","张三","张三"}};
        // 初始化表格
        TableModel dataModle = new DefaultTableModel(data,columnNames);
        dutyList.setModel(dataModle);
        //dutyList.setBorder(BorderFactory.createEtchedBorder());

        // 设置表头
        JTableHeader tableHeader = dutyList.getTableHeader();
        //tableHeader.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 16));
        tableHeader.setBackground(new Color(200, 200, 200, 100));
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(false);


        // 设置表格内容格式
        dutyList.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 14));
        dutyList.setRowHeight(16);
        DefaultTableCellRenderer defaultTableCellRenderer = (DefaultTableCellRenderer)dutyList.getDefaultRenderer(Object.class);
        defaultTableCellRenderer.setHorizontalAlignment(JLabel.CENTER);
        defaultTableCellRenderer.setOpaque(false);
        dutyList.setDefaultRenderer(Object.class,defaultTableCellRenderer);

        dutyList.setEnabled(false);
        //parent.setUI(new MainParentPanelUI());

        jsp.getViewport().setOpaque(false);//将JScrollPane设置为透明
        jsp.setOpaque(false);//将中间的viewport设置为透明
        dutyList.setOpaque(false);
        parent.setOpaque(false);

        parent
    }

    public static void main(String[] args) {
        FRAME=new JFrame("公告");
        FRAME.setSize(565, 300);
        FRAME.setUndecorated(true);
        WorkForm workForm = new WorkForm();
        FRAME.add(workForm.parent);

        // 启动居中
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        FRAME.setLocation((d.width - FRAME.getWidth()) / 2, (d.height - FRAME.getHeight()) / 2);
        FRAME.setVisible(true);
        FRAME.setAlwaysOnTop(true);
        FRAME.setAlwaysOnTop(false);
    }
}
