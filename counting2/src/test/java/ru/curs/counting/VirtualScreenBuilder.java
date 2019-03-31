package ru.curs.counting;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.virtual.DefaultVirtualTerminal;

import java.io.IOException;

public class VirtualScreenBuilder {

    public static Screen screen() throws IOException {
        DefaultVirtualTerminal defaultVirtualTerminal = new DefaultVirtualTerminal();
        Screen r = new TerminalScreen(defaultVirtualTerminal);
        return r;
    }
}
