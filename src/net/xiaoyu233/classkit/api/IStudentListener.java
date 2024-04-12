package net.xiaoyu233.classkit.api;

import net.xiaoyu233.classkit.api.pos.IClassPosition;
import net.xiaoyu233.classkit.api.unit.Student;

import java.util.Collection;
import java.util.List;

public interface IStudentListener {
    void onAdded(ClassManager classManager, Class newClass, IClassPosition<?> position, Student student, List<Student> candidateRemains, Collection<? extends IClassPosition<?>> positionsRemain);
}
