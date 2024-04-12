package net.xiaoyu233.classkit.random.gender;

import net.xiaoyu233.classkit.api.ClassManager;
import net.xiaoyu233.classkit.api.pos.IClassPosition;
import net.xiaoyu233.classkit.api.unit.Gender;
import net.xiaoyu233.classkit.random.ClassRandomizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomSameGenderProvider implements IGenderMapProvider{
    public static final RandomSameGenderProvider INSTANCE = new RandomSameGenderProvider();
    private RandomSameGenderProvider() {}

    @Override
    public Map<IClassPosition<?>, Gender> getGenderMap(ClassManager originalClass, ClassRandomizer randomizer, List<? extends IClassPosition<?>> poses, int boy, int girl) {
        Map<IClassPosition<?>, Gender> result = new HashMap<>();
        boolean boysOdd = boy % 2 != 0;
        boolean girlsOdd = girl % 2 != 0;
        Map<Gender,Integer> genderCountMap = new HashMap<>();
        genderCountMap.put(Gender.Girl, boy);
        genderCountMap.put(Gender.Boy, girl);
        for (IClassPosition<?> groupDeskPosition : poses) {
            Gender gender = randomizer.getRandom().nextBoolean() ? Gender.Boy : Gender.Girl;
            boolean leftEmpty = originalClass.getStudent(groupDeskPosition).isEmpty();
            boolean rightEmpty = originalClass.getStudent(groupDeskPosition.getDeskMate()).isEmpty();
            if (leftEmpty && rightEmpty){
                continue;
            }
            boolean isSingleDesk = leftEmpty || rightEmpty;
            if (isSingleDesk) {
                Gender singleGender;
                if (!boysOdd && !girlsOdd){
                    if (genderCountMap.get(Gender.Boy) <= 0){
                        singleGender = Gender.Girl;
                    }else if (genderCountMap.get(Gender.Girl) <= 0){
                        singleGender = Gender.Boy;
                    }else {
                        singleGender = randomizer.getRandom().nextBoolean() ? Gender.Boy : Gender.Girl;
                    }
                }else {
                    if (boysOdd) {
                        singleGender = Gender.Boy;
                    }else {
                        singleGender = Gender.Girl;
                    }
                }
                if (leftEmpty) {
                    result.put(groupDeskPosition.getDeskMate(),singleGender);
                }else {
                    result.put(groupDeskPosition,singleGender);
                }
                genderCountMap.put(singleGender,(genderCountMap.get(singleGender))-1);
                boysOdd = genderCountMap.get(Gender.Boy) % 2 != 0;
                girlsOdd = genderCountMap.get(Gender.Girl) % 2 != 0;
            }else {
                Integer count = genderCountMap.get(gender);
                if ((gender == Gender.Girl &&( (count <= 1 && girlsOdd) || count <= 0))){
                    gender = Gender.Boy;
                }else if ((gender == Gender.Boy &&( (count <= 1 && boysOdd) || count <= 0))){
                    gender = Gender.Girl;
                }
                count = genderCountMap.get(gender);
                result.put(groupDeskPosition.getDeskMate(), gender);
                result.put(groupDeskPosition, gender);
                genderCountMap.put(gender, count -2);
            }
        }
        return result;
    }
}
