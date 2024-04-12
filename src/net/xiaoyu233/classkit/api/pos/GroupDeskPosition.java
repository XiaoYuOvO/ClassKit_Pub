package net.xiaoyu233.classkit.api.pos;

import java.util.Objects;

public class GroupDeskPosition implements IClassPosition<GroupDeskPosition>{
    private final int group,desk;
    private final Side side;

    public GroupDeskPosition(int group, int desk, Side side) {
        this.group = group;
        this.desk = desk;
        this.side = side;
    }

    @Override
    public int getGroupIndex() {
        return this.group;
    }

    @Override
    public int getDeskIndex() {
        return this.desk;
    }

    @Override
    public Side getSide() {
        return this.side;
    }

    @Override
    public GroupDeskPosition getDeskMate() {
        return new GroupDeskPosition(this.group,this.desk,this.side.getOther());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupDeskPosition that = (GroupDeskPosition) o;
        return group == that.group && desk == that.desk && side == that.side;
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, desk, side);
    }
}
