package net.xiaoyu233.classkit.api;

import net.xiaoyu233.classkit.api.pos.Area;
import net.xiaoyu233.classkit.api.pos.IClassPosition;
import net.xiaoyu233.classkit.api.unit.Student;

import java.util.List;

public interface IAreaProvider {
    List<Student> filter(Class classManager, Area area, IClassPosition<?> position, List<Student> candidates);
}
