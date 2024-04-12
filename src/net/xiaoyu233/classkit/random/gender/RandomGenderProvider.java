package net.xiaoyu233.classkit.random.gender;

import net.xiaoyu233.classkit.api.ClassManager;
import net.xiaoyu233.classkit.api.pos.GroupDeskPosition;
import net.xiaoyu233.classkit.api.pos.IClassPosition;
import net.xiaoyu233.classkit.api.unit.Gender;
import net.xiaoyu233.classkit.random.ClassRandomizer;

import java.util.*;

public class RandomGenderProvider implements IGenderMapProvider {
    public static final RandomGenderProvider INSTANCE = new RandomGenderProvider();
    private RandomGenderProvider() {}
    @Override
    public Map<IClassPosition<?>, Gender> getGenderMap(ClassManager originalClass, ClassRandomizer randomizer, List<? extends IClassPosition<?>> poses, int boy, int girl) {
        Map<IClassPosition<?>, Gender> result = new HashMap<>();
        Random random = randomizer.getRandom();
        Collections.shuffle(poses, random);
        Stack<IClassPosition<?>> posStack = new Stack<>();
        for (IClassPosition<?> position : poses) {
            posStack.push(position);
        }
        for (int i = 0; i < boy; i++) {
            result.put(posStack.pop(), Gender.Boy);
        }

        for (int i = 0; i < girl; i++) {
            result.put(posStack.pop(), Gender.Girl);
        }

        return result;
    }
}
