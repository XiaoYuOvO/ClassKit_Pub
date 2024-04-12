package net.xiaoyu233.classkit.api.unit;

public enum Gender {
    Boy,Girl,None;

    @Override
    public String toString() {
        return switch (this) {
            case Boy -> "男生";
            case Girl -> "女生";
            case None -> "任意";
        };
    }
}
