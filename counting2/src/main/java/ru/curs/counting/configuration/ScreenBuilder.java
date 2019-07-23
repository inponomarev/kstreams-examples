package ru.curs.counting.configuration;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ScreenBuilder {

    @Bean
    public Screen getScreen() throws IOException {
        if (Optional.ofNullable(System.getProperty("os.name"))
                .map(s -> s.contains("Windows")).orElse(false)) {
            SwingTerminalFrame st = (new DefaultTerminalFactory()).createSwingTerminal();
            st.setVisible(true);
            return new TerminalScreen(st);
        } else {
            return (new DefaultTerminalFactory()).createScreen();
        }
    }

    @Bean("localtable")
    Map<String, Long> getLocalTable(){
        return new ConcurrentHashMap<>();
    }
}
