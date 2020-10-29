package aurora.timer.client.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class WorkForm {
    public static JFrame FRAME;
    public JTextArea announceText;
    public JPanel parent;
    public JScrollPane jsp;
    public JButton announceButton;
    private JTable dutyList;
    private static final int MAX_CHARACTERS = 300;
    public WorkForm(){
        parent.setOpaque(false);

        jsp.setOpaque(false);
        jsp.getViewport().setOpaque(false);
        jsp.setBorder(null);
        jsp.setViewportBorder(null);
        jsp.setPreferredSize(new Dimension(450,500));
        jsp.setBorder(BorderFactory.createEtchedBorder());

        announceText.setFont(new Font("YaHei Consolas Hybrid", Font.PLAIN, 18));
        announceText.setLineWrap(true);
        announceButton.setUI(new LoginButtonUI());

        Object[] columnNames = {"星期天","星期一","星期二","星期三","星期四","星期五","星期六"};
        Object[][] data = {{"张三","张三","张三","张三","张三","张三","张三"}};
        DefaultTableModel model = new DefaultTableModel(data,columnNames);
        dutyList.setModel(model);
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

