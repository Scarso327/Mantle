package gb.Jack.mantle.utils;

import java.util.List;

public class PrettyPrint {
    public static String printString(List<String> strings) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < strings.size(); i++) {
            result.append(strings.get(i));

            if (i != (strings.size() - 1)) {
                result.append(", ");
            }
        }

        return result.toString();
    }
}
