package aurora.timer.client.view;

import javafx.scene.control.ScrollBar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Created by hao on 17-2-24.
 */
public class WeekInfoForm {
    public JPanel parent;
    public JButton changeButton;
    public JScrollPane infoPane;
    public JTable weekList;
    public JButton leftButton;
    public JButton rightButton;

    public void init() {
        LoginButtonUI buttonUI = new LoginButtonUI();
        leftButton.setUI(buttonUI);
        rightButton.setUI(buttonUI);
        changeButton.setUI(buttonUI);
        changeButton.setContentAreaFilled(false);
        infoPane.getViewport().setOpaque(false);
        DefaultTableModel model = (DefaultTableModel) weekList.getModel();
        model.addColumn("姓名");
        model.addColumn("本周在线总时间");
        weekList.setBackground(new Color(200, 200, 200, 100));
        weekList.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 16));
        JTableHeader tableHeader = weekList.getTableHeader();
        tableHeader.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 18));
        tableHeader.setBackground(new Color(200, 200, 200, 100));
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(false);
        weekList.setEnabled(false);
        weekList.setVisible(true);

        JScrollBar scrollBar = new JScrollBar();
        scrollBar.setBorder(null);
        scrollBar.setOpaque(false);
        scrollBar.setIgnoreRepaint(false);

        infoPane.setVerticalScrollBar(scrollBar);
    }

    public WeekInfoForm() {
        init();
    }
}
