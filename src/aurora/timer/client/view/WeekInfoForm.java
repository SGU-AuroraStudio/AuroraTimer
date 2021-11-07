package aurora.timer.client.view;

//import javafx.scene.control.ScrollBar;

import aurora.timer.client.service.UserOnlineTimeService;
import aurora.timer.client.view.baseUI.login.LoginButtonUI;
import aurora.timer.client.vo.UserOnlineTime;

import javax.swing.*;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.*;
import java.awt.*;
import java.util.Comparator;

/**
 * Created by hao on 17-2-24.
 */
public class WeekInfoForm {
    public JPanel weekInfoPanel;
    public JButton changeButton;
    public JScrollPane infoPane;
    public JTable weekList;
    public JButton leftButton;
    public JButton rightButton;
    public JButton announceBtn;
    public Main2Form context; // 上下文环境
    public void init() {
        LoginButtonUI buttonUI = new LoginButtonUI();
        leftButton.setUI(buttonUI);
        rightButton.setUI(buttonUI);
        changeButton.setUI(buttonUI);
        announceBtn.setUI(buttonUI);
        changeButton.setContentAreaFilled(false);
        infoPane.getViewport().setOpaque(false);
        DefaultTableModel model = (DefaultTableModel) weekList.getModel();
        model.addColumn("学号");
        model.addColumn("姓名");
        model.addColumn("本学期在线总时间");
        model.addColumn("本周在线总时间");
        weekList.setBackground(new Color(200, 200, 200, 100));
        weekList.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 16));
        //隐藏“学号”列
        TableColumn ids = weekList.getColumn("学号");
        ids.setWidth(0);
        ids.setPreferredWidth(0);
        ids.setMaxWidth(0);
        ids.setMinWidth(0);
        weekList.getTableHeader().getColumnModel().getColumn(0)
                .setMaxWidth(0);
        weekList.getTableHeader().getColumnModel().getColumn(0)
                .setMinWidth(0);
        //以下这一段转移到了Main2Form里了
        // 不加这段排序后会重影
//        weekList.getRowSorter().addRowSorterListener(new RowSorterListener() {
//            //不知道为什么排序后触发了两次
//            @Override
//            public void sorterChanged(RowSorterEvent e) {
//                weekInfoPanel.repaint();
//                //两次输出
////                System.out.println("排序");
//
//                //不知道为什么会不断调用然后爆栈
////                context.getRedList();
////                context.setAllTime();
//            }
//        });
        DefaultTableCellRenderer defaultTableCellRenderer = (DefaultTableCellRenderer) weekList.getDefaultRenderer(Object.class);
        defaultTableCellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

        // 表头
        JTableHeader tableHeader = weekList.getTableHeader();
        tableHeader.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 18));
        tableHeader.setBackground(new Color(200, 200, 200, 100));
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(false);
        DefaultTableCellRenderer headerTCR = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
        headerTCR.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

        //不加值日表到滚条可能会吹来
        JScrollBar scrollBar = new JScrollBar();
        scrollBar.setBorder(null);
        scrollBar.setOpaque(false);
        scrollBar.setIgnoreRepaint(false);
        infoPane.setVerticalScrollBar(scrollBar);
        //不加会按默认，按字符串ascii排序。自定义按 时间"："前到小时int大小排序
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) weekList.getRowSorter();
        sorter.setComparator(2, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {

                int a = Integer.parseInt(o1.split(":")[0]);
                int b = Integer.parseInt(o2.split(":")[0]);
                int c = Integer.parseInt(o1.split(":")[1]);
                int d = Integer.parseInt(o2.split(":")[1]);
                if(a!=b){
                    return a-b;
                }else{
                    return c-d;
                }
//                int dif = Integer.parseInt(o1.split(":")[0]) - Integer.parseInt(o2.split(":")[0]);
//                return dif;
            }
        });

    }

    public WeekInfoForm() {
        init();
    }

}
