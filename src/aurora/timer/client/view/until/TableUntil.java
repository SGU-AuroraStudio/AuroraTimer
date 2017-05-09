package aurora.timer.client.view.until;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created from 'http://blog.csdn.net/caiandyong/article/details/54236260'
 */
public class TableUntil {
    public static void setOneRowBackgroundColor(JTable table, int[] rowIndex, Color color, int page) {
        try {
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {

                public Component getTableCellRendererComponent(JTable table,
                                                               Object value, boolean isSelected, boolean hasFocus,
                                                               int row, int column) {

                    boolean flag = false;
                    for (int i = 0; i < rowIndex.length; i ++) {
                        //把名字和前n的渲染
                        if (row==rowIndex[i] && column==0) {
                            flag = true;
                            setBackground(color);
                            setForeground(new Color(230,230,230,230));
                        }
                    }
                    if (!flag) {
                        setBackground(new Color(200, 200, 200, 100));
                        setForeground(Color.black);
                    }

                    return super.getTableCellRendererComponent(table, value,
                            isSelected, hasFocus, row, column);
                }
            };

            int columnCount = table.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                table.getColumn("姓名").setCellRenderer(tcr);
                if (page==0) {
                    table.getColumn("本周在线总时间").setCellRenderer(tcr);
                } else {
                    table.getColumn("前" + page + "周在线总时间").setCellRenderer(tcr);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
