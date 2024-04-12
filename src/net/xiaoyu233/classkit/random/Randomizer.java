package net.xiaoyu233.classkit.random;

import net.xiaoyu233.classkit.api.Class;
import net.xiaoyu233.classkit.api.unit.Gender;
import net.xiaoyu233.classkit.api.unit.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

public class Randomizer extends Thread{
    private int round;
    private final ClassRandomizer aClass;
    private final int second;
    private final Consumer<Student> selectedCallback;
    private final Consumer<Student> targetCallback;
    private Gender targetGender = Gender.None;
    private List<Student> exclude = new ArrayList<>();

    public Randomizer(int second, ClassRandomizer aClass, Consumer<Student> selectedCallback, Consumer<Student> targetCallback) {
        this.aClass = aClass;
        this.selectedCallback = selectedCallback;
        this.second = second;
        this.targetCallback = targetCallback;
    }

    public void setTargetGender(Gender targetGender) {
        this.targetGender = targetGender;
    }

    public void setRound(Function<Random,Integer> rounder) {
        this.round = rounder.apply(this.aClass.getRandom());
    }

    public void setExclude(List<Student> exclude) {
        this.exclude = exclude;
    }

    @Override
    public void run() {
        super.run();
        Student target = new Student(Gender.Boy,"");
        for (int i = 0; i < this.round; i++) {
            try {
                Student student = aClass.randomGetStudent(exclude, targetGender);
                this.selectedCallback.accept(student);
                target = student;
                Thread.sleep(Math.max(0,(long) (2*second*1000*(i-1d)/(round*(round-1d)))));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.targetCallback.accept(target);
    }
}
