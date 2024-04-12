package net.xiaoyu233.classkit.api.unit;

import net.xiaoyu233.classkit.api.pos.Side;
import net.xiaoyu233.classkit.util.Pair;

public class Desk {
    private final Pair<Student> deskMates;

    public Student get(Side side) {
        return switch (side) {
            case LEFT -> deskMates.getLeft();
            case RIGHT -> deskMates.getRight();
        };
    }
    public Student getLeft() {
        return deskMates.getLeft();
    }

    public Student getRight() {
        return deskMates.getRight();
    }

    public void setLeft(Student who) {
        deskMates.setLeft(who);
    }

    public void set(Side side,Student student){
        switch (side){
            case LEFT:
                this.deskMates.setLeft(student);
                break;
            case RIGHT:
                this.deskMates.setRight(student);
                break;
        }
    }

    public void setRight(Student who) {
        deskMates.setRight(who);
    }

    public Pair<Student> getDeskMates() {
        return deskMates;
    }

    public Desk(Pair<Student> deskMates) {
        this.deskMates = deskMates;
    }

    public Desk(Student left,Student right) {
        this.deskMates = new Pair<>(left,right);
    }
}
