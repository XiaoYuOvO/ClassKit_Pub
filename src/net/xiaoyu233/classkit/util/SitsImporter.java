package net.xiaoyu233.classkit.util;

import net.xiaoyu233.classkit.api.Class;
import net.xiaoyu233.classkit.api.ClassSize;
import net.xiaoyu233.classkit.api.unit.Desk;
import net.xiaoyu233.classkit.api.unit.Gender;
import net.xiaoyu233.classkit.api.unit.Group;
import net.xiaoyu233.classkit.api.unit.Student;
import net.xiaoyu233.classkit.io.ClassCodec;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SitsImporter {
    public static void main(String[] args) throws IOException {
        File file = new File("./Class.csv");
        List<CSVRecord> records = CSVParser.parse(new FileReader(file), CSVFormat.DEFAULT).getRecords();
        List<Group> groupList =  new ArrayList<>();
        int maxColumn = records.size();
        int groupCount = 0;
        for (CSVRecord strings : records) {
            groupCount = Math.max(groupCount, strings.size() / 2);

        }
        for (int i = 0; i < groupCount; i++) {
            groupList.add(new Group(maxColumn));
        }

        int columnIndex = maxColumn-1;
        for (CSVRecord record : records) {
            int groupIndex = 0;
            for (int index = record.size();index >0 ;index-=2) {
                String left = record.get(index-1);
                String right = record.get(index-2);
                Desk desk = new Desk(new Student(Gender.Girl,left),new Student(Gender.Girl,right));
                groupList.get(groupIndex).setDeskAt(columnIndex,desk);
                groupIndex++;
            }
            columnIndex--;
        }
        Class theClass = new Class(ClassSize.Builder.create()
                                           .addGroup(records.size())
                                           .addGroup(records.size())
                                           .addGroup(records.size())
                                           .addGroup(records.size())
                                           .build());
        theClass.getGroups().clear();
        theClass.getGroups().addAll(groupList);
        ClassCodec.ClassSerializer.writeToFile(new File("Class.json"),theClass);
    }
}
