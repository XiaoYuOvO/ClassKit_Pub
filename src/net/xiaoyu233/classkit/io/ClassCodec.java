package net.xiaoyu233.classkit.io;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.xiaoyu233.classkit.api.ChangeHistory;
import net.xiaoyu233.classkit.api.Class;
import net.xiaoyu233.classkit.api.ClassSize;
import net.xiaoyu233.classkit.api.unit.Desk;
import net.xiaoyu233.classkit.api.unit.Group;
import net.xiaoyu233.classkit.api.unit.Student;
import net.xiaoyu233.classkit.util.Pair;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.csv.CSVFormat.DEFAULT;

public class ClassCodec {


    public static final TypeAdapter<Class> codec =  new TypeAdapter<Class>() {
        @Override
        public void write(JsonWriter out, Class value) {
            JsonObject theClass = new JsonObject();
            JsonArray groups = new JsonArray();
            for (Group group : value.getGroups()) {
                JsonArray desks = new JsonArray();
                for (Desk desk : group.getDesks()) {
                    desks.add(desk.getDeskMates().codec.toJsonTree(desk.getDeskMates()));
                }
                groups.add(desks);
            }
            theClass.add("sit",groups);
            JsonArray histories = new JsonArray();
            for (ChangeHistory history : value.getHistory()) {
                histories.add(ChangeHistory.CODEC.toJsonTree(history));
            }
            theClass.add("change_histories",histories);
            new Gson().newBuilder().setPrettyPrinting().create().toJson(theClass,out);
        }

        private Class readFromArray(JsonArray array){
            List<Group> groupList = new ArrayList<>();
            for (JsonElement groupEle : array) {
                if (groupEle.isJsonArray()) {
                    Group group = new Group(groupEle.getAsJsonArray().size());
                    int deskIndex = 0;

                    for (JsonElement deskEle : groupEle.getAsJsonArray()) {
                        if (deskEle.isJsonObject()) {
                            JsonObject deskObj = deskEle.getAsJsonObject();
                            Pair<Student> deskMate = Pair.EMPTY.clone();
                            deskMate = deskMate.codec.fromJsonTree(deskObj);
                            if (!deskMate.getLeft().getName().isEmpty() || !deskMate.getRight().getName().isEmpty()){
                                group.setDeskAt(deskIndex,new Desk(deskMate));
                                deskIndex++;
                            }
                        }
                    }
                    groupList.add(group);
                }
            }
            ClassSize.Builder builder = ClassSize.Builder.create();
            for (Group group : groupList) {
                builder.addGroup(group.getDesks().size() );
            }
            Class theClass = new Class(builder.build());
            theClass.getGroups().clear();
            theClass.getGroups().addAll(groupList);
            theClass.updateGender();
            return theClass;
        }

        @Override
        public Class read(JsonReader in) {

            JsonElement classEle = JsonParser.parseReader(in);
            if (classEle.isJsonArray()){
                JsonArray sitArray = ((JsonArray) classEle);
                return this.readFromArray(sitArray);
            }else if (classEle.isJsonObject()){
                JsonObject classObject = ((JsonObject) classEle);
                if (classObject.has("sit")){
                    JsonArray sitArray = classObject.get("sit").getAsJsonArray();
                    Class theClass = this.readFromArray(sitArray);
                    if (classObject.has("change_histories")){
                        JsonArray histories = classObject.get("change_histories").getAsJsonArray();
                        for (JsonElement history : histories) {
                            theClass.getHistory().add(ChangeHistory.CODEC.fromJsonTree(history));
                        }
                    }
                    return theClass;
                }
            }
            throw new JsonSyntaxException("Class json incorrect");

        }
    };
    public static class ClassDeserializer {
        public static Class readFromFile(File file) throws IOException {
            return codec.fromJson(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        }
    }
    public static class ClassSerializer {
        public static void writeToFile(File file,Class theClass) throws IOException {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),StandardCharsets.UTF_8);
            new Gson().newBuilder().setPrettyPrinting().create().toJson(codec.toJsonTree(theClass),writer);
            writer.flush();
            writer.close();
        }

        public static void writeToFile(File file,JsonElement jsonElement) throws IOException {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),StandardCharsets.UTF_8);
            new Gson().newBuilder().setPrettyPrinting().create().toJson(jsonElement,writer);
            writer.flush();
            writer.close();
        }

        public static JsonElement toJson(Class theClass){
            return codec.toJsonTree(theClass);
        }

        public static void writeToCSV(File file,Class theClass) throws IOException{
            CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(file,false), Charset.forName("GBK")),DEFAULT);
            int maxColumn = 0;
            ClassSize size = theClass.getSize();
            for (int i = 0; i < size.getGroupCount(); i++) {
                maxColumn = Math.max(maxColumn,size.getGroupMax(i));
            }
            for (int deskIndex = maxColumn;deskIndex >= 0;deskIndex--){
                for (int groupIndex = theClass.getGroups().size()-1; groupIndex >= 0;--groupIndex){
                    Group theGroup = theClass.getGroups().get(groupIndex);
                    if (deskIndex >= theGroup.size()){
                        printer.print("");
                        printer.print("");
                        printer.print("");
                        continue;
                    }
                    Desk desk = theGroup.getDesks(deskIndex);
                    printer.print(desk.getRight().getName());
                    printer.print(desk.getLeft().getName());
                    printer.print("");
                }
                printer.println();
            }
            printer.close();
        }
    }
}
