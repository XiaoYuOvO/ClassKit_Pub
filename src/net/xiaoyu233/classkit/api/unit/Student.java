package net.xiaoyu233.classkit.api.unit;

import com.google.common.base.Objects;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.xiaoyu233.classkit.io.IJsonHolder;

public class Student implements IJsonHolder<Student> {
    public static final TypeAdapter<Student> codec = new TypeAdapter<Student>() {
        @Override
        public void write(JsonWriter out, Student value) {
            JsonObject studentObj = new JsonObject();
            studentObj.addProperty("Name",value.getName());
            studentObj.addProperty("Gender",value.getGender().ordinal());
            new Gson().toJson(studentObj,out);
        }

        @Override
        public Student read(JsonReader in) {
            JsonObject studentObj = JsonParser.parseReader(in).getAsJsonObject();
            String name = studentObj.get("Name").getAsString();
            if (!name.equals("")){
                return new Student(Gender.values()[studentObj.get("Gender").getAsInt()],name);
            }else {
                return new Student(Gender.None,"");
            }
        }
    };
    public static final Student EMPTY = new Student(Gender.None,"");
    private final Gender gender;
    private final String name;
    private Desk deskAt;
    public Student(Gender gender, String name){
        this.gender = gender;
        this.name = name;
    }

    public boolean isEmpty(){
        return this.gender == Gender.None || this.name.isEmpty();
    }

    public void setDeskAt(Desk deskAt) {
        this.deskAt = deskAt;
    }

    public Desk getDeskAt() {
        return deskAt;
    }

    public Gender getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "gender=" + gender +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return gender == student.gender && Objects.equal(name, student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(gender, name);
    }

    @Override
    public TypeAdapter<Student> getAdapter() {
        return codec;
    }
}
