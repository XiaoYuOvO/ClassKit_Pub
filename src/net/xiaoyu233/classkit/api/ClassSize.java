package net.xiaoyu233.classkit.api;

import java.util.ArrayList;
import java.util.List;

public class ClassSize {
    private final List<Integer> groups;
    private final int maxGroupSize;

    private ClassSize(List<Integer> groups,int maxGroupSize){
        this.groups = groups;
        this.maxGroupSize = maxGroupSize;
    }

    public int getGroupMax(int index) {
        return groups.get(index);
    }

    public int getGroupCount() {
        return groups.size();
    }

    public int getMaxGroupSize() {
        return maxGroupSize;
    }

    public static class Builder{
        private final List<Integer> groups = new ArrayList<>();
        private int maxGroupSize;
        private Builder() {}

        public static Builder create(){
            return new Builder();
        }

        public Builder addGroup(int groupSize){
            this.groups.add(groupSize);
            this.maxGroupSize = Math.max(this.maxGroupSize,groupSize);
            return this;
        }

        public ClassSize build() {
            return new ClassSize(this.groups,this.maxGroupSize);
        }
    }
}
