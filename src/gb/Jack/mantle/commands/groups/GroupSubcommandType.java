package gb.Jack.mantle.commands.groups;

import java.util.ArrayList;
import java.util.List;

public enum GroupSubcommandType {
    INFO,
    CREATE,
    DISBAND,
    KICK,
    JOIN,
    LEAVE;

    public static GroupSubcommandType getCommand(String name) {

        switch (name.toLowerCase()) {
            case "info":
                return INFO;
            case "create":
                return CREATE;
            case "disband":
                return DISBAND;
            case "kick":
                return KICK;
            case "join":
                return JOIN;
            case "leave":
                return LEAVE;
        }

        return null;
    }

    // Couldn't get the methods I found on google to work so used the knowledge I do have to create my own solution...
    public static List<String> toList() {
        List<String> list = new ArrayList<>();

        for (GroupSubcommandType s : GroupSubcommandType.values()) {
            String name = s.toString().toLowerCase();

            list.add(name);
        }

        return list;
    }
}
