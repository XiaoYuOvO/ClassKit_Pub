package net.xiaoyu233.classkit.api.pos;

import java.util.Objects;

public class CoordPosition implements IClassPosition<CoordPosition>{
    private final int group,desk;
    private final Side side;

    public CoordPosition(int x, int y) {
        this.group = x/2;
        this.desk = y;
        this.side = Side.values()[x % 2];
    }

    private CoordPosition(int group,int deskIndex,Side side){
        this.group = group;
        this.desk = deskIndex;
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
    public CoordPosition getDeskMate() {
        return new CoordPosition(this.group,this.desk,this.side.getOther());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordPosition that = (CoordPosition) o;
        return group == that.group && desk == that.desk && side == that.side;
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, desk, side);
    }
}
