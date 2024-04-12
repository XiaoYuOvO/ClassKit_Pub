package net.xiaoyu233.classkit.api.unit;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private  final List<Desk> desks;

    public Group(int deskCount) {
        desks = new ArrayList<>(deskCount);
        for (int index = 0;index < deskCount;index++){
            desks.add(new Desk(Student.EMPTY,Student.EMPTY));
        }
    }

    public void setDeskAt(int index,Desk desk){
        this.desks.set(index,desk);
    }

    public Desk getDesks(int index) {
        return desks.get(index);
    }

    public List<Desk> getDesks() {
        return desks;
    }

    public int size() {
        return this.desks.size();
    }
}
