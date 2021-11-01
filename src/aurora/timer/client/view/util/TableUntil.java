package aurora.timer.client.view.util;

import aurora.timer.client.view.Main2Form;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created from 'http://blog.csdn.net/caiandyong/article/details/54236260'
 */
public class TableUntil {
    @Deprecated
    public static void setOneRowBackgroundColor(JTable table, int[] rowIndex, Color color, int page, Main2Form context) {
        JLabel jl = new JLabel();
//        System.out.println("行号："+rowIndex.toString());
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
                            String name = (String) table.getValueAt(row, 0);
                            jl.setText(name);
//                            System.out.println(name);
                            //调整图标和名字的位置，记得去除前导空格
                            if(name.trim().length()==2){
                                jl.setIcon(new ImageIcon(getClass().getClassLoader().getResource("aurora/timer/img/main/mvp_icon2.png")));
                            }else if(name.trim().length()==3){
                                jl.setIcon(new ImageIcon(getClass().getClassLoader().getResource("aurora/timer/img/main/mvp_icon.png")));
                            }else if(name.trim().length()==4){
                                //留着以后调整
                                jl.setIcon(new ImageIcon(getClass().getClassLoader().getResource("aurora/timer/img/main/mvp_icon.png")));
                            }else{
                                jl.setIcon(new ImageIcon(getClass().getClassLoader().getResource("aurora/timer/img/main/mvp_icon.png")));
                            }
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

    public static void setOneRowBackgroundColor(JTable table, String[] ids, Color color, int page, Main2Form context) {
        JLabel jl = new JLabel();
        try {
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {

                public Component getTableCellRendererComponent(JTable table,
                                                               Object value, boolean isSelected, boolean hasFocus,
                                                               int row, int column) {

                    boolean flag = false;
                    for(int i=0;i<ids.length;i++){
                        if(column==1){
                            String id = (String)table.getValueAt(row, 0);
                            if(id.equals(ids[i])){
                                flag = true;
                                String name = (String) table.getValueAt(row, 1);
                                jl.setText(name);
                                //调整图标和名字的位置，记得去除前导空格
                                if(name.trim().length()==2){
                                    jl.setIcon(new ImageIcon(getClass().getClassLoader().getResource("aurora/timer/img/main/mvp_icon2.png")));
                                }else if(name.trim().length()==3){
                                    jl.setIcon(new ImageIcon(getClass().getClassLoader().getResource("aurora/timer/img/main/mvp_icon.png")));
                                }else if(name.trim().length()==4){
                                    //留着以后调整
                                    jl.setIcon(new ImageIcon(getClass().getClassLoader().getResource("aurora/timer/img/main/mvp_icon.png")));
                                }else{
                                    jl.setIcon(new ImageIcon(getClass().getClassLoader().getResource("aurora/timer/img/main/mvp_icon.png")));
                                }
                                jl.setOpaque(true); // 开启后可以设置背景色
                                jl.setBackground(color);
                                jl.setForeground(Color.orange);
                                jl.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 16));
                            }
                        }
                    }
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
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void setOneRowBackgroundColor(JTable table, Color color) {

        try {
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {

                public Component getTableCellRendererComponent(JTable table,
                                                               Object value, boolean isSelected, boolean hasFocus,
                                                               int row, int column) {


                    if(row==1){
                        setBackground(new Color(200, 200, 200, 100));
                        setForeground(color);
                    }

                    return super.getTableCellRendererComponent(table, value,
                            isSelected, hasFocus, row, column);
                }
            };
            tcr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
            int columnCount = table.getColumnCount();
            System.out.println(columnCount);
            for (int i = 0; i < columnCount; i++) {

                System.out.println(table.getValueAt(0,i));
//                table.getColumn(table.getValueAt(0,i)).setCellRenderer(tcr);
//
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
