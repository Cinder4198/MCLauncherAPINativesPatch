package net.deftera.Util.Natives;

import java.io.File;
import java.util.List;

public class NativesCommandPatcher {
    public List<String> patcher(List<String> command, String version) {

        for (int i = 0; i < command.size(); i++) {

            String argument = command.get(i);

            if(argument.contains("-Djava.library.path")) {

                String newLibraryCmd = argument.replaceAll("/natives", File.separator + version + "-natives");
                List<String> newCommand = command;
                newCommand.set(i, newLibraryCmd);
                System.out.println("New library arg: " + newLibraryCmd );
                return newCommand;

            }
        }
        return command;
    }
}
