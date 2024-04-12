package net.xiaoyu233.classkit.api;

import net.xiaoyu233.classkit.api.pos.GroupDeskPosition;
import net.xiaoyu233.classkit.api.pos.IClassPosition;
import net.xiaoyu233.classkit.api.pos.Side;
import net.xiaoyu233.classkit.api.unit.*;
import net.xiaoyu233.classkit.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Class {
    private  final  List<Group> groups;
    private final ClassSize size;
    private final Map<Gender,List<Student>> genderListMap = new HashMap<>();
    private final List<ChangeHistory> history = new ArrayList<>();
    public  Class(ClassSize size){
        this.size = size;
        this.groups = new ArrayList<>(size.getGroupCount());
        for (Gender value : Gender.values()) {
            genderListMap.put(value,new ArrayList<>());
        }
        for (int groupIndex = 0;groupIndex < size.getGroupCount();groupIndex++){
            this.groups.add(groupIndex,new Group(size.getGroupMax(groupIndex)));
        }
    }

    public Map<Gender, List<Student>> getGenderListMap() {
        return genderListMap;
    }

    public List<IClassPosition<?>> listAllPositions(){
        List<IClassPosition<?>> result = new ArrayList<>();
        for (int groupIndex = 0; groupIndex < this.getSize().getGroupCount(); groupIndex++) {
            for (int deskIndex = 0; deskIndex < this.size.getGroupMax(groupIndex); deskIndex++) {
                result.add(new GroupDeskPosition(groupIndex, deskIndex, Side.LEFT));
                result.add(new GroupDeskPosition(groupIndex, deskIndex, Side.RIGHT));
            }
        }
        return result;
    }

    public List<IClassPosition<?>> listAllPositionsWithStudent(){
        return this.listAllPositions().stream().filter((groupDeskPosition -> !this.getStudent(groupDeskPosition).isEmpty())).collect(Collectors.toList());
    }

    public ClassSize getSize() {
        return this.size;
    }

    public Group getGroup(int index) {
        return groups.get(index);
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void updateGender(){
        for (List<Student> value : genderListMap.values()) {
            value.clear();
        }
        for (Group group : this.groups) {
            for (Desk desk : group.getDesks()) {
                Student left = desk.getLeft();
                Student right = desk.getRight();
                left.setDeskAt(desk);
                right.setDeskAt(desk);
                if (!left.getName().isEmpty()) {
                    genderListMap.get(left.getGender()).add(left);
                }
                if (!right.getName().isEmpty()) {
                    genderListMap.get(right.getGender()).add(right);
                }
            }
        }
        genderListMap.get(Gender.None).clear();
    }

    public List<Student> getAllStudents(){
        List<Student> result = new ArrayList<>();
        for (List<Student> value : this.genderListMap.values()) {
            result.addAll(value);
        }
        return result;
    }


    public Optional<ChangeHistory> exchangeStudent(Student from,Student to,boolean addToHistory){
        if (this.hasStudent(from) && this.hasStudent(to)) {
            boolean toLeft = to.getDeskAt().getLeft() == to;
            boolean fromLeft = from.getDeskAt().getLeft() == from;
            Desk fromDesk = from.getDeskAt();
            Desk toDesk = to.getDeskAt();
            if (fromLeft){
                fromDesk.setLeft(to);
            }else {
                fromDesk.setRight(to);
            }
            if (toLeft){
                toDesk.setLeft(from);
            }else{
                toDesk.setRight(from);
            }
            from.setDeskAt(toDesk);
            to.setDeskAt(fromDesk);
            if (addToHistory){
                ChangeHistory history = new ChangeHistory(Calendar.getInstance().getTime(),new Pair<>(from,to));
                this.history.add(history);
                return Optional.of(history);
            }else {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public List<ChangeHistory> getHistory() {
        return history;
    }

    public  boolean hasStudent(Student student){
        return this.genderListMap.get(student.getGender()).contains(student);
    }

    public Student getStudent(IClassPosition<?> position){
        return this.getGroups()
                .get(position.getGroupIndex())
                .getDesks()
                .get(position.getDeskIndex())
                .get(position.getSide());
    }

    public List<Student> getStudentsOfGender(Gender gender){
        if (gender == Gender.None){
            return this.getAllStudents();
        }
        return genderListMap.get(gender);
    }

    public void setStudent(Student student,IClassPosition<?> position){
        this.getGroup(position.getGroupIndex()).getDesks(position.getDeskIndex()).set(position.getSide(),student);
    }

}
