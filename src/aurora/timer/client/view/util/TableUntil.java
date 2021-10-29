package aurora.timer.client.view.util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created from 'http://blog.csdn.net/caiandyong/article/details/54236260'
 */
public class TableUntil {
    public static void setOneRowBackgroundColor(JTable table, int[] rowIndex, Color color, int page) {
        JLabel jl = new JLabel();
        try {
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {

                public Component getTableCellRendererComponent(JTable table,
                                                               Object value, boolean isSelected, boolean hasFocus,
                                                               int row, int column) {

                    boolean flag = false;
                    for(int i=0;i<rowIndex.length;i++){
                        if(row==rowIndex[i] && column==0){
                            flag = true;
//                            jl.setHorizontalAlignment(CENTER);
                            jl.setIcon(new ImageIcon(getClass().getClassLoader().getResource("aurora/timer/img/main/mvp_icon.png")));
                            jl.setText((String) table.getValueAt(row, 0));
                            jl.setOpaque(true); // 开启后可以设置背景色
//                            jl.setBackground(new Color(200, 200, 200, 100));
                            jl.setBackground(color);
//                            jl.setForeground(new Color(230, 230, 230, 230));
                            jl.setForeground(Color.orange);
                            jl.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 16));

                        }
                    }
//                    for (int i = 0; i < rowIndex.length; i++) {
                        //把名字和前n的渲染
//                        if (row == rowIndex[i] && column == 0) {
//                            System.out.println(rowIndex[i]+" "+row+" "+table.getValueAt(row,0));
                        //if (row == rowIndex[i]) {
//                            flag = true;
//                            setBackground(color);

//                            setIcon(new ImageIcon("res/aurora/timer/img/main/mvp_icon.png"));
//                            if(color!=null){
//                                setBackground(color);
//                                setIcon(new ImageIcon("res/aurora/timer/img/main/mvp_icon.png"));
//                                System.out.println(rowIndex[i]+" "+getText());
//                            }else{
//                                setIcon(new ImageIcon("res/aurora/timer/img/main/mvp_icon.png"));
//                            }
//                            setForeground(new Color(230, 230, 230, 230));

//                        }
//                    }
                    if (!flag) {
                        setBackground(new Color(200, 200, 200, 100));
                        setForeground(Color.black);
                    }else{
                        return jl;
                    }

                    return super.getTableCellRendererComponent(table, value,
                            isSelected, hasFocus, row, column);
                }
            };
            tcr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
            int columnCount = table.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                table.getColumn("姓名").setCellRenderer(tcr);
//                if (page == 0) {
//                    table.getColumn("本周在线总时间").setCellRenderer(tcr);
//                    table.getColumn("本学期在线总时间").setCellRenderer(tcr);
//
//
//                } else {
//                    table.getColumn("前" + page + "周在线总时间").setCellRenderer(tcr);
//                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
