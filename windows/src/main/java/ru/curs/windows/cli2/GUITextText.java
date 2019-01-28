package ru.curs.windows.cli2;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class GUITextText implements AutoCloseable {

    private final Screen screen;
    private final AsynchronousTextGUIThread guiThread;
    private final Map<String, KeyValuePanel> panelMap = new TreeMap<>();
    private final ValueSetter valueSetter;
    private final Panel mainPanel;

    private final Deque<String> values = new LinkedList<>();

    public GUITextText(Screen screen) throws IOException {
        this.screen = screen;

        final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen);
        guiThread = (AsynchronousTextGUIThread) textGUI.getGUIThread();
        screen.startScreen();

        final Window window = new BasicWindow("Information");
        Panel wrapperPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));

        wrapperPanel.addComponent(mainPanel);
        wrapperPanel.addComponent(new EmptySpace());

        window.setComponent(wrapperPanel);
        textGUI.addWindow(window);

        valueSetter = new ValueSetter(guiThread);
        guiThread.start();
    }

    @Override
    public void close() throws IOException, InterruptedException {
        guiThread.stop();
        guiThread.waitForStop();
        screen.close();
        valueSetter.close();
    }

    public void update(String item, String newVal) {
        KeyValuePanel panel = panelMap.get(item);
        if (panel == null) {
            panel = new KeyValuePanel(item, newVal);
            values.add(item);

            panelMap.put(item, panel);
            guiThread.invokeLater(() -> {
                mainPanel.removeAllComponents();
                panelMap.forEach((k, v) ->
                        mainPanel.addComponent(v.getPanel()));
            });
            valueSetter.lineHightlight(panel);

            while (values.size() > 12) {
                String toRemove = values.remove();
                Optional.ofNullable(panelMap.remove(toRemove)).ifPresent(
                        p -> guiThread.invokeLater(() ->
                                mainPanel.removeComponent(p.getPanel()))
                );
            }
        } else {
            valueSetter.setValue(panel, newVal);
        }

    }

}
