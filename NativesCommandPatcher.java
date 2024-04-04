package net.deftera.Util.Natives;

import java.io.File;
import java.util.List;

public class NativesCommandPatcher {
    public List<String> patcher(List<String> command, String version) {
         String newLibraryCmd = command.get(10).replaceAll("/natives", File.separator + version + "-natives");
         List<String> newCommand = command;
         newCommand.set(10, newLibraryCmd);
         return newCommand;
    }
}
