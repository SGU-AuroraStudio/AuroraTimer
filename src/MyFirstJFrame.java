import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.*;


public class MyFirstJFrame extends JFrame {

    // 作为测试的main方法
    public static void main(String[] args) {
        new MyFirstJFrame().setVisible(true);
    }

    /**
     * 构造方法
     */
    public MyFirstJFrame() {
        InitialComponent();
    }

    /**
     * 初始化组件的方法
     */
    private void InitialComponent(){
        // 设置窗体参数

        // 设置布局模式
        setLayout(null);
        // 设置窗体大小
        setSize(480, 360);
        // 设置窗体居中（非常规方法）
        setLocationRelativeTo(null);
        // 关闭窗体退出程序
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // 初始化面板
        panel = new JPanel();
        panel.setSize(this.getWidth(), this.getHeight());
        panel.setLocation(0,0);
        panel.setLayout(null);

        // 初始化表格
        table = new JTable(new DefaultTableModel(new Object[][]{{"第一行"},{"第二行"},{"第三行"},{"第四行"}}, new String[]{"测试行1","测试行2"}){
            /* (non-Javadoc)
             * 重写方法，判断表单元格是否可编辑
             * 可以通过row和column索引判断某一个单元格是否可编辑
             * 此处设为都不可编辑
             * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        // 开始向表格中添加复选框（注意：此示例较为简单，缺省很多判断，也没有动态代码支持）
        // 通过设置列渲染

        // 方法一：直接方式 使用TableColumn的setCellRenderer方法（推荐）
        // 此方法可以设置某一列的渲染（即使用某一个组件--即控件来显示单元格数据）
        table.getColumnModel().getColumn(1).setCellRenderer(new TableCellRenderer(){

            /*(non-Javadoc)
            * 此方法用于向方法调用者返回某一单元格的渲染器（即显示数据的组建--或控件）
            * 可以为JCheckBox JComboBox JTextArea 等
            * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
            */
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                // 创建用于返回的渲染组件
                JCheckBox ck = new JCheckBox();
                // 使具有焦点的行对应的复选框选中
                ck.setSelected(isSelected);
                // 设置单选box.setSelected(hasFocus);
                // 使复选框在单元格内居中显示
                ck.setHorizontalAlignment((int) 0.5f);
                return ck;
            }});

        // 方法二：先设置列编辑器，然后设置单元格渲染
        // 设置列编辑器
        // 在以复选框为对象设置列编辑器时，必须保证该列能够被编辑，否则无法更改状态
        // （此步骤可以省略，省略时不要忘记将列设为不可编辑）
        // table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JCheckBox()));

        // 设置单元格渲染（这里是设置表格级别的渲染）
         /*table.setDefaultRenderer(Object.class, new TableCellRenderer(){

             @Override
             public Component getTableCellRendererComponent(JTable table,
                     Object value, boolean isSelected, boolean hasFocus,
                     int row, int column) {
                 // 判断是否为需要渲染的列
                 if(column == 1){
                     // 和方法一基本一致
                     JCheckBox box = new JCheckBox();
                     box.setSelected(isSelected);
                     // 设置单选box.setSelected(hasFocus);
                     box.setHorizontalAlignment((int) CENTER_ALIGNMENT);    // 0.5f
                     return box;
                     }
                 // 如果不是需要渲染的列，封装文本域显示数据
                 return new JTextArea(value.toString());
             }});*/

        // 在多选是需要按住Ctrl键或者鼠标按住拖过连续的需要选中的行，应该给用户说明
        // 第一种方法是被推荐的，因为它具有选中的高亮显示，界面能更加友好
        table.setSize(panel.getWidth(),panel.getHeight() - 90);
        table.setLocation(0, 0);


        btn = new JButton("Test");
        btn.setSize(80,40);
        btn.setLocation((panel.getWidth()) / 2 - 40, panel.getHeight() - 80);

        // 按钮点击时显示当前选中项
        btn.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                for(int rowindex : table.getSelectedRows()){
                    JOptionPane.showMessageDialog(null, rowindex + " " + table.getValueAt(rowindex, 0));
                }
            }});

        panel.add(table);
        panel.add(btn);
        this.add(panel);

    }

    // 定义一些必要的组件
    private JPanel panel;
    private JTable table;
    private JButton btn;
}
