package net.xiaoyu233.classkit.api;

import net.xiaoyu233.classkit.api.pos.Area;
import net.xiaoyu233.classkit.api.pos.GroupDeskPosition;
import net.xiaoyu233.classkit.api.pos.IClassPosition;
import net.xiaoyu233.classkit.api.unit.Gender;
import net.xiaoyu233.classkit.api.unit.Group;
import net.xiaoyu233.classkit.api.unit.Student;
import net.xiaoyu233.classkit.io.ClassCodec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClassManager {
    private Class theClass;
    private final List<Area> areas = new ArrayList<>();

    public ClassManager(Class theClass) {
        this.theClass = theClass;
    }

    public Map<Gender, List<Student>> getGenderListMap() {
        return this.theClass.getGenderListMap();
    }
    public List<Student> getAllStudents(){
        return this.theClass.getAllStudents();
    }

    public int getStudentCount() {
        return this.theClass.getAllStudents().size();
    }

    public ClassSize getClassSize(){
        return this.theClass.getSize();
    }

    public List<IClassPosition<?>> listAllPositions(){
        return this.theClass.listAllPositions();
    }

    public List<IClassPosition<?>> listAllPositionsWithStudent(){
        return this.theClass.listAllPositionsWithStudent();
    }

    public void manageNewClass(Class clazz){
        this.theClass = clazz;
        this.theClass.updateGender();
    }

    public int getStudentCount(Gender gender) {
        return this.getAllStudentsOfGender(gender).size();
    }

    public List<Student> getAllStudentsOfGender(Gender gender){
        return this.theClass.getStudentsOfGender(gender);
    }

    public Student getStudent(IClassPosition<?> position){
        return this.theClass.getStudent(position);
    }

    public List<ChangeHistory> getHistory() {
        return this.theClass.getHistory();
    }

    public List<Group> getGroups() {
        return this.theClass.getGroups();
    }

    public Optional<ChangeHistory> exchangeStudent(Student left, Student right, boolean addToHistory) {
        return this.theClass.exchangeStudent(left, right, addToHistory);
    }

    public void writeToFile(File file) throws IOException {
        ClassCodec.ClassSerializer.writeToFile(file,ClassCodec.ClassSerializer.toJson(this.theClass));
    }

    public void writeToCSV(File file) throws IOException {
        ClassCodec.ClassSerializer.writeToCSV(file,(this.theClass));
    }
}
