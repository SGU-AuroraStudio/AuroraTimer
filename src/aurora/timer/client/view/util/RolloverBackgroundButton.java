package aurora.timer.client.view.util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * 鼠标悬浮和按下以及选中时能切换背景颜色/边框颜色/前景颜色的Button
 *
 * @author tang https://blog.csdn.net/u012643122/article/details/39081595
 * @since 2014/8/23
 */
public class RolloverBackgroundButton extends JButton {

    private static final long serialVersionUID = 1L;

    protected Color normalBackground;// 默认无状态时背景颜色,此属性父类中已定义(background)
    protected Color pressedBackground;// 鼠标按下时背景颜色
    protected Color rolloverBackground;// 鼠标悬浮时背景颜色
    protected Color selectedBackground;// 选中时背景颜色

    protected Color normalBorderColor;// 默认无状态时边框颜色
    protected Color pressedBorderColor;// 鼠标按下时边框颜色
    protected Color rolloverBorderColor;// 鼠标悬浮时边框颜色
    protected Color selectedBorderColor;// 选中时边框颜色

    protected Color normalForeground;// 默认时前景颜色
    protected Color pressedForeground;// 鼠标按下时前景颜色
    protected Color rolloverForeground;// 鼠标悬浮时前景颜色
    protected Color selectedForeground;// 选中时前景颜色

    {
        initRolloverButton();
    }

    public RolloverBackgroundButton() {
    }

    public RolloverBackgroundButton(Icon icon) {
        super(icon);
    }

    public RolloverBackgroundButton(String text, Icon icon) {
        super(text, icon);
    }

    public RolloverBackgroundButton(String text) {
        super(text);
    }

    public RolloverBackgroundButton(Action a) {
        super(a);
    }

    private void initRolloverButton() {
        setRolloverEnabled(true);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setFont(new Font(Font.DIALOG, Font.PLAIN, 14));

        setNormalBackground(new Color(216, 216, 216));
        setPressedBackground(new Color(216, 216, 216, 100));
        setNormalBorderColor(new Color(174, 174, 174));
        setRolloverBorderColor(new Color(95, 205, 245));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Paint oldPaint = g2d.getPaint();
        if (isSelected() && selectedBackground != null) {// 选中时
            g2d.setPaint(selectedBackground);
            g2d.fillRect(0, 0, getWidth(), getHeight());// 背景
        } else if (getModel().isPressed() && pressedBackground != null) {// 鼠标按下时
            g2d.setPaint(pressedBackground);
            g2d.fillRect(0, 0, getWidth(), getHeight());// 背景
        } else if (getModel().isRollover() && rolloverBackground != null) {// 鼠标悬浮时
            g2d.setPaint(rolloverBackground);
            g2d.fillRect(0, 0, getWidth(), getHeight());// 背景
        } else if (normalBackground != null) {// 默认无状态时
            g2d.setPaint(normalBackground);
            g2d.fillRect(0, 0, getWidth(), getHeight());// 背景
        }
        g2d.setPaint(oldPaint);

        if (isSelected() && selectedForeground != null) {// 选中时
            setForeground(selectedForeground);
        } else if (getModel().isPressed() && pressedForeground != null) {// 鼠标按下时
            setForeground(pressedForeground);
        } else if (getModel().isRollover() && rolloverForeground != null) {// 鼠标悬浮时
            setForeground(rolloverForeground);
        } else if (normalForeground != null) {// 默认无状态时
            setForeground(normalForeground);
        }

        super.paint(g2d);

        if (isSelected() && selectedBorderColor != null) {// 选中时
            g2d.setPaint(selectedBorderColor);
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);// 边框
        } else if (getModel().isPressed() && pressedBorderColor != null) {// 鼠标按下时
            g2d.setPaint(pressedBorderColor);
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);// 边框
        } else if (getModel().isRollover() && rolloverBorderColor != null) {// 鼠标悬浮时
            g2d.setPaint(rolloverBorderColor);
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);// 边框
        } else if (normalBorderColor != null) {// 默认无状态时
            g2d.setPaint(normalBorderColor);
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);// 边框
        }
        g2d.setPaint(oldPaint);
    }

    /**
     * 清空设置的默认属性值：<br>
     * <p>
     * setNormalBackground(null);<br>
     * setPressedBackground(null); <br>
     * setNormalBorderColor(null); <br>
     * setRolloverBorderColor(null);
     */
    public void clearDefaultAttribute() {
        setNormalBackground(null);
        setPressedBackground(null);
        setNormalBorderColor(null);
        setRolloverBorderColor(null);
    }

    public Color getNormalBackground() {
        return normalBackground;
    }

    public void setNormalBackground(Color normalBackground) {
        this.normalBackground = normalBackground;
    }

    public Color getPressedBackground() {
        return pressedBackground;
    }

    public void setPressedBackground(Color pressedBackground) {
        this.pressedBackground = pressedBackground;
    }

    public Color getRolloverBackground() {
        return rolloverBackground;
    }

    public void setRolloverBackground(Color rolloverBackground) {
        this.rolloverBackground = rolloverBackground;
    }

    public Color getNormalBorderColor() {
        return normalBorderColor;
    }

    public void setNormalBorderColor(Color normalBorderColor) {
        this.normalBorderColor = normalBorderColor;
    }

    public Color getPressedBorderColor() {
        return pressedBorderColor;
    }

    public void setPressedBorderColor(Color pressedBorderColor) {
        this.pressedBorderColor = pressedBorderColor;
    }

    public Color getRolloverBorderColor() {
        return rolloverBorderColor;
    }

    public void setRolloverBorderColor(Color rolloverBorderColor) {
        this.rolloverBorderColor = rolloverBorderColor;
    }

    public Color getPressedForeground() {
        return pressedForeground;
    }

    public void setPressedForeground(Color pressedForeground) {
        this.pressedForeground = pressedForeground;
    }

    public Color getRolloverForeground() {
        return rolloverForeground;
    }

    public void setRolloverForeground(Color rolloverForeground) {
        this.rolloverForeground = rolloverForeground;
    }

    public Color getNormalForeground() {
        return normalForeground;
    }

    public void setNormalForeground(Color normalForeground) {
        this.normalForeground = normalForeground;
    }

    public Color getSelectedBackground() {
        return selectedBackground;
    }

    public void setSelectedBackground(Color selectedBackground) {
        this.selectedBackground = selectedBackground;
    }

    public Color getSelectedBorderColor() {
        return selectedBorderColor;
    }

    public void setSelectedBorderColor(Color selectedBorderColor) {
        this.selectedBorderColor = selectedBorderColor;
    }

    public Color getSelectedForeground() {
        return selectedForeground;
    }

    public void setSelectedForeground(Color selectedForeground) {
        this.selectedForeground = selectedForeground;
    }
}