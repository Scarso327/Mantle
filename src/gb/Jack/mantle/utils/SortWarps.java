package gb.Jack.mantle.utils;

import gb.Jack.mantle.warps.Warp;

import java.util.Comparator;

public class SortWarps implements Comparator<Warp> {

    @Override
    public int compare(Warp w1, Warp w2) {
        return w1.getID() - w2.getID();
    }
}