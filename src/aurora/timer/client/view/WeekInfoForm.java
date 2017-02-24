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

    public void init() {
        changeButton.setUI(new LoginButtonUI());
        changeButton.setContentAreaFilled(false);
        infoPane.getViewport().setOpaque(false);
        DefaultTableModel model = (DefaultTableModel) weekList.getModel();
        model.addColumn("ID");
        model.addColumn("本周在线总时间");
        weekList.setBackground(new Color(200, 200, 200, 100));
        weekList.setFont(new Font("Arial", Font.PLAIN, 18));
        JTableHeader tableHeader = weekList.getTableHeader();
        tableHeader.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 20));
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
