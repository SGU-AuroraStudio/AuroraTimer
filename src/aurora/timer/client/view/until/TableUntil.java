package aurora.timer.client.view.until;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created from 'http://blog.csdn.net/caiandyong/article/details/54236260'
 */
public class TableUntil {
    public static void setOneRowBackgroundColor(JTable table, int[] rowIndex, Color color) {
        try {
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {

                public Component getTableCellRendererComponent(JTable table,
                                                               Object value, boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
//                    if (row == rowIndex) {
//                        setBackground(color);
//                        setForeground(Color.WHITE);
//                    }else if(row > rowIndex){
//                        setBackground(Color.BLACK);
//                        setForeground(Color.WHITE);
//                    }else{
//                        setBackground(Color.BLACK);
//                        setForeground(Color.WHITE);
//                    }

                    boolean flag = false;
                    for (int i = 0; i < rowIndex.length; i ++) {
                        if (row==rowIndex[i]) {
                            flag = true;
                            setBackground(new Color(147,224,255,100));
                            setForeground(color);
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
                table.getColumn(table.getColumnName(i)).setCellRenderer(tcr);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
