package ru.curs.counting.configuration;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ScreenBuilder {

    @Bean
    public Screen getScreen() throws IOException {
        SwingTerminalFrame st = (new DefaultTerminalFactory()).createSwingTerminal();
        st.setVisible(true);
        return new TerminalScreen(st);
    }
}
