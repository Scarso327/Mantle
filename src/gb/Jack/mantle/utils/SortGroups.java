package gb.Jack.mantle.utils;

import gb.Jack.mantle.groups.Group;

import java.util.Comparator;

public class SortGroups implements Comparator<Group> {

    @Override
    public int compare(Group g1, Group g2) {
        return g1.getDbID() - g2.getDbID();
    }
}