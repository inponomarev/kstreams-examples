package ru.curs.windows.cli2;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

@Component
public class GUITextText implements AutoCloseable {

    private final Screen screen;
    private final AsynchronousTextGUIThread guiThread;
    private final Map<String, KeyValuePanel> panelMap = new TreeMap<>();
    private final ValueSetter valueSetter;
    private final Panel mainPanel;

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
                panelMap.put(item, panel);
                guiThread.invokeLater(() -> {
                    mainPanel.removeAllComponents();
                    panelMap.forEach((k, v) ->
                            mainPanel.addComponent(v.getPanel()));
                });
                valueSetter.lineHightlight(panel);
            } else {
                valueSetter.setValue(panel, newVal);
            }

        }

}
