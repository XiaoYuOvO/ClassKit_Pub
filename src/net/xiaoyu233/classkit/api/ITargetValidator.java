package net.xiaoyu233.classkit.api;

import net.xiaoyu233.classkit.api.pos.IClassPosition;
import net.xiaoyu233.classkit.api.unit.Student;

import java.util.List;

public interface ITargetValidator {
    boolean isValid(ClassManager classManager, Class newClass, IClassPosition<?> position, Student student, List<Student> candidateRemains);
}
