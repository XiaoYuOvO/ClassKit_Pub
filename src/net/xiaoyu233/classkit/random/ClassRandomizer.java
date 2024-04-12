package net.xiaoyu233.classkit.random;

import net.xiaoyu233.classkit.api.Class;
import net.xiaoyu233.classkit.api.*;
import net.xiaoyu233.classkit.api.pos.Area;
import net.xiaoyu233.classkit.api.pos.IClassPosition;
import net.xiaoyu233.classkit.api.unit.Gender;
import net.xiaoyu233.classkit.api.unit.Student;
import net.xiaoyu233.classkit.random.gender.IGenderMapProvider;
import net.xiaoyu233.classkit.random.gender.RandomSameGenderProvider;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ClassRandomizer {
    final ClassManager classManager;
    private final Map<Area, IAreaProvider> areasProviders = new HashMap<>();
    private final Map<Student, ITargetValidator> targetValidators = new HashMap<>();
    private final Map<Student, IStudentListener> studentListener = new HashMap<>();
    private final Map<IClassPosition<?>, IPositionValidator> positionValidators = new HashMap<>();
    private final Supplier<? extends Random> randomProvider;
    private IGenderMapProvider genderMapProvider = RandomSameGenderProvider.INSTANCE;

    public ClassRandomizer(Supplier<? extends Random> randomProvider, ClassManager classManager) {
        this.classManager = classManager;
        this.randomProvider = (randomProvider);
    }

    public void setGenderMapProvider(IGenderMapProvider genderMapProvider) {
        this.genderMapProvider = genderMapProvider;
    }

    private Map<IClassPosition<?>, Gender> createGenderMap(List<? extends IClassPosition<?>> availablePositions,List<Student> allStudents) {
        return this.genderMapProvider.getGenderMap(this.classManager, this, availablePositions,
                (int) allStudents.stream().filter(student -> student.getGender() == Gender.Boy).count(),
                (int) allStudents.stream().filter(student -> student.getGender() == Gender.Girl).count());
    }

    public ClassManager randomNewClass() {
        Class newClass = new Class(this.classManager.getClassSize());
        HashMap<Gender, List<Student>> genderListHashMap = new HashMap<>(this.classManager.getGenderListMap());
        List<Student> studentsRemains = this.classManager.getAllStudents();
        List<IClassPosition<?>> allPositions = classManager.listAllPositionsWithStudent();
        for (Map.Entry<Area, IAreaProvider> areaIAreaProviderEntry : this.areasProviders.entrySet()) {
            Area area = areaIAreaProviderEntry.getKey();
            for (IClassPosition<?> pos : area.allPoses()) {
//                Gender gender = genderMap.get(pos);
                Student target = this.getRandomInList(this.filterStudent(
                        areasProviders.get(area).filter(newClass, area, pos,studentsRemains), pos, newClass));
                if (target != null){
                    newClass.setStudent(target, pos);
                    genderListHashMap.get(target.getGender()).remove(target);
                    studentsRemains.remove(target);
                    allPositions.remove(pos);
                }
            }
        }

        Map<IClassPosition<?>, Gender> genderMap = this.createGenderMap(allPositions,studentsRemains);
        while (!genderMap.isEmpty() && genderListHashMap.entrySet()
                .stream()
                .anyMatch((entry) -> !entry.getValue().isEmpty())) {
            IClassPosition<?> randPos = this.getRandomInList(genderMap.keySet());
            IClassPosition<?> deskMate = Objects.requireNonNull(randPos)
                    .getDeskMate();
            Gender gender = genderMap.get(randPos);
            List<Student> targetList = genderListHashMap.get(gender);
            if (targetList.isEmpty()) {
                continue;
            }
            if (!classManager.getStudent(randPos).isEmpty()) {
                Student target1 = null;
                while (target1 == null && !targetList.isEmpty() && genderMap.containsKey(randPos)) {
                    target1 = this.selectStudentFor(randPos, targetList, newClass);
                    if (target1 != null) {
                        targetList.remove(target1);
                        genderMap.remove(randPos);
                        newClass.setStudent(target1, randPos);
                        this.tryTriggerStudentListener(target1, newClass, randPos, targetList, (genderMap.keySet()));
                    }
                }
            }
            gender = genderMap.get(deskMate);
            if (gender != null) {
                targetList = genderListHashMap.get(gender);
                if (!classManager.getStudent(deskMate).isEmpty()) {
                    Student target2 = null;
                    while (target2 == null && !targetList.isEmpty() && genderMap.containsKey(deskMate)) {
                        target2 = this.selectStudentFor(deskMate, targetList, newClass);
                        if (target2 != null) {
                            targetList.remove(target2);
                            genderMap.remove(deskMate);
                            newClass.setStudent(target2, deskMate);
                            this.tryTriggerStudentListener(target2, newClass, deskMate, targetList, genderMap.keySet());
                        }
                    }
                }
            }


        }
        this.classManager.manageNewClass(newClass);
        return this.classManager;
    }

    private void tryTriggerStudentListener(Student student, Class newClass, IClassPosition<?> position, List<Student> candidatesRemain, Collection<? extends IClassPosition<?>> positionsRemain) {
        IStudentListener iStudentListener = this.studentListener.get(student);
        if (iStudentListener != null) {
            iStudentListener.onAdded(this.classManager, newClass, position, student, candidatesRemain, positionsRemain);
        }
    }

    public void addStudentListener(Student student, IStudentListener listener) {
        this.studentListener.put(student, listener);
    }

    @Nullable
    private Student selectStudentFor(IClassPosition<?> position, List<Student> src, Class newClass) {
        return this.getRandomInList(
                this.filterStudent(src, position, newClass));
    }

    private List<Student> filterStudent(List<Student> src, IClassPosition<?> position, Class newClass) {
        return src.stream().
                filter(student -> !(this.targetValidators.containsKey(student)) || this.targetValidators.get(student).
                        isValid(this.classManager, newClass, position, student, src)).
                filter(student -> !(this.positionValidators.containsKey(position)) || this.positionValidators.get(position).
                        isValid(this.classManager, position, student)).
                collect(Collectors.toList());
    }

    public ClassManager getClassManager() {
        return classManager;
    }

    public void addArea(Area area, IAreaProvider provider) {
        this.areasProviders.put(area, provider);
    }

    public void addTargetValidator(Student student, ITargetValidator validator) {
        this.targetValidators.put(student, validator);
    }

    public void addPositionValidator(IClassPosition<?> position, IPositionValidator validator) {
        this.positionValidators.put(position, validator);
    }

    public Student randomGetStudent() {
        return this.getRandomInList(classManager.getAllStudents());
    }

    public Student randomGetStudent(List<Student> expectList) {
        ArrayList<Student> students = new ArrayList<>(classManager.getAllStudents());
        students.removeAll(expectList);
        return this.getRandomInList(students);
    }

    public Student randomGetStudent(List<Student> expectList, Gender targetGender) {
        ArrayList<Student> students = new ArrayList<>(classManager.getAllStudentsOfGender(targetGender));
        students.removeAll(expectList);
        return this.getRandomInList(students);
    }

    public Student randomGetStudent(Gender targetGender) {
        return this.getRandomInList(this.classManager.getAllStudentsOfGender(targetGender));
    }

    public Random getRandom() {
        return this.randomProvider.get();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <E> E getRandomInList(Collection<E> list) {
        if (list.isEmpty()) {
            return null;
        }
        int size = list.size();
        Object[] objects = list.toArray();
        return (E) (size > 1 ? objects[randomProvider.get()
                .nextInt(size)] : objects[0]);
    }

}
