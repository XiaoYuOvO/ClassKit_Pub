package net.xiaoyu233.classkit.api.pos;

import net.xiaoyu233.classkit.util.Utils;

import java.util.HashMap;
import java.util.Map;

public enum Side {
    LEFT(),
    RIGHT();
    static {
        LEFT.other = RIGHT;
        RIGHT.other = LEFT;
    }
    private Side other;

    Side() {
    }

    public Side getOther() {
        return other;
    }
}
