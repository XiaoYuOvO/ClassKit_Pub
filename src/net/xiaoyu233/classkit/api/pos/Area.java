package net.xiaoyu233.classkit.api.pos;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class Area {
    private final List<IClassPosition<?>> poses = new ArrayList<>();

    public static Area inSquares(IClassPosition<?> from,IClassPosition<?> to){
        int fromGroup = from.getGroupIndex();
        int toGroup = to.getGroupIndex();
        int groupCount = Math.abs(fromGroup - toGroup);
        int fromDesk = from.getDeskIndex();
        int toDesk = to.getDeskIndex();
        int deskCount = fromDesk - toDesk;
        Area area = new Area();
        for (int i = 0; i < groupCount; i++) {
            for (int j = 0; j < deskCount; j++) {
                area.addPos(new CoordPosition(i, j));
            }
        }

        return area;
    }

    public static Area of(IClassPosition<?>... position){
        Area area = new Area();
        area.poses.addAll(Lists.newArrayList(position));
        return area;
    }

    public boolean contains(IClassPosition<?> position) {
        return poses.contains(position);
    }

    public void addPos(IClassPosition<?> pos){
        this.poses.add(pos);
    }

    public List<IClassPosition<?>> allPoses(){
        return poses;
    }

    @Override
    public String toString() {
        return "{" +
                poses +
                '}';
    }
}
