package net.xiaoyu233.classkit.random.gender;

import net.xiaoyu233.classkit.api.ClassManager;
import net.xiaoyu233.classkit.api.pos.GroupDeskPosition;
import net.xiaoyu233.classkit.api.pos.IClassPosition;
import net.xiaoyu233.classkit.api.unit.Gender;
import net.xiaoyu233.classkit.random.ClassRandomizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InheritOriginalGenderProvider implements IGenderMapProvider{
    public static final InheritOriginalGenderProvider INSTANCE = new InheritOriginalGenderProvider();
    private InheritOriginalGenderProvider(){}
    @Override
    public Map<IClassPosition<?>, Gender> getGenderMap(ClassManager originalClass, ClassRandomizer randomizer, List<? extends IClassPosition<?>> poses, int boy, int girl) {
        Map<IClassPosition<?>, Gender> result = new HashMap<>();
        for (IClassPosition<?> groupDeskPosition : originalClass.listAllPositionsWithStudent()) {
            result.put(groupDeskPosition, originalClass.getStudent(groupDeskPosition)
                    .getGender());
        }
        return result;
    }
}
