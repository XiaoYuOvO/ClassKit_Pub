package net.xiaoyu233.classkit.api;

import net.xiaoyu233.classkit.api.pos.IClassPosition;
import net.xiaoyu233.classkit.api.unit.Student;

import java.util.Collection;
import java.util.List;

public interface IPositionValidator {
    boolean isValid(ClassManager classManager,IClassPosition<?> position, Student target);
}
