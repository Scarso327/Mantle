package gb.Jack.mantle.utils;

public class Maths {
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }

        try {
            int d = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    public static int calTotalPages(int listSize, int perPage) {
        int total = (int) Math.ceil((double) listSize / perPage);

        if (total < 1) {
            total = 1;
        }

        return total;
    }
}
