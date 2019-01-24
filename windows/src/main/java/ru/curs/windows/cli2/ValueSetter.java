package ru.curs.windows.cli2;

import com.googlecode.lanterna.gui2.AsynchronousTextGUIThread;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

public class ValueSetter implements AutoCloseable {
    private final static int KEEP_COLOR = 3000;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final AsynchronousTextGUIThread guiThread;
    private final ConcurrentHashMap<KeyValuePanel, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<KeyValuePanel, ScheduledFuture<?>> lineTasks = new ConcurrentHashMap<>();

    public ValueSetter(AsynchronousTextGUIThread guiThread) {
        this.guiThread = guiThread;
    }

    public void setValue(KeyValuePanel panel, String value) {
        guiThread.invokeLater(() -> {
            String previousValue = panel.getValue();
            if (!Objects.equals(value, previousValue)) {
                panel.red();
                scheduleValueHighlightRemoval(panel);
            }
            panel.setValue(value);
        });
    }

    private void scheduleValueHighlightRemoval(KeyValuePanel panel) {
        Optional.ofNullable(tasks.put(panel, executorService.schedule(() -> {
            guiThread.invokeLater(() -> panel.black());
        }, KEEP_COLOR, TimeUnit.MILLISECONDS))).ifPresent(
                t -> t.cancel(false)
        );
    }

    public void lineHightlight(KeyValuePanel panel) {
        guiThread.invokeLater(() -> {
            panel.highlight();
            Optional.ofNullable(lineTasks.put(panel, executorService.schedule(() -> {
                guiThread.invokeLater(() -> panel.old());
            }, KEEP_COLOR, TimeUnit.MILLISECONDS))).ifPresent(
                    t -> t.cancel(false)
            );

        });

    }

    @Override
    public void close() {
        executorService.shutdownNow();
    }
}
