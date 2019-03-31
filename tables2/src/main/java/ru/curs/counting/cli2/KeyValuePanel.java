package ru.curs.counting.cli2;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.AbsoluteLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;

public class KeyValuePanel {
    private final static int MAX_KEY_WIDTH = 30;
    private final static int MAX_VAL_WIDTH = 20;

    private final Panel panel;
    private final Label keyLabel;
    private final Label valueLabel;
    private String value;

    public KeyValuePanel(String key, String value) {
        this.value = value;
        panel = new Panel(new AbsoluteLayout());
        keyLabel = new Label(key);
        valueLabel = new Label(value);
        keyLabel.setPosition(new TerminalPosition(0, 0))
                .setSize(new TerminalSize(MAX_KEY_WIDTH, 1))
                .addStyle(SGR.BOLD);
        valueLabel.setPosition(new TerminalPosition(MAX_KEY_WIDTH + 3, 0))
                .setSize(new TerminalSize(MAX_VAL_WIDTH + 1, 1));
        panel.addComponent(keyLabel);
        panel.addComponent(valueLabel);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
        this.value = value;
    }


    public String getValue() {
        return value;
    }

    public void red() {
        valueLabel.setForegroundColor(TextColor.ANSI.RED);
    }

    public void green() {
        valueLabel.setForegroundColor(TextColor.ANSI.GREEN);
    }

    public void black() {
        valueLabel.setForegroundColor(TextColor.ANSI.BLACK);
    }

    public Panel getPanel() {
        return panel;
    }

    public void highlight() {
        keyLabel.setBackgroundColor(TextColor.ANSI.YELLOW);
    }

    public void old(){
        keyLabel.setBackgroundColor(TextColor.ANSI.WHITE);
    }
}
