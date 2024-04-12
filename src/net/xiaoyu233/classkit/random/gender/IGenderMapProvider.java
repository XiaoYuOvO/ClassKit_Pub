package net.xiaoyu233.classkit.random.gender;

import net.xiaoyu233.classkit.api.ClassManager;
import net.xiaoyu233.classkit.api.pos.IClassPosition;
import net.xiaoyu233.classkit.api.unit.Gender;
import net.xiaoyu233.classkit.random.ClassRandomizer;

import java.util.List;
import java.util.Map;

public interface IGenderMapProvider {
    Map< IClassPosition<?>, Gender> getGenderMap(ClassManager originalClass, ClassRandomizer randomizer, List<? extends IClassPosition<?>> poses, int boy, int girl);
}
