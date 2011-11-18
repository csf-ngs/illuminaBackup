package com.beust.jcommander;

public class JCommanderFactory {
    public static JCommander createWithArgs(Object cmdLineArgs) {
        return new JCommander(cmdLineArgs);
    }
}
