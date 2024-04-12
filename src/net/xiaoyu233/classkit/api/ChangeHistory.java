package net.xiaoyu233.classkit.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.xiaoyu233.classkit.api.unit.Student;
import net.xiaoyu233.classkit.util.Pair;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChangeHistory {
    public static final TypeAdapter<ChangeHistory> CODEC = new TypeAdapter<>() {
        private final TypeAdapter<Date> dateTypeAdapter = new DateTypeAdapter();

        @Override
        public void write(JsonWriter out, ChangeHistory value) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("Time", dateTypeAdapter.toJsonTree(value.changeDate));
            jsonObject.add("FromTo", value.fromTo.codec.toJsonTree(value.fromTo));
            new Gson().toJson(jsonObject, out);
        }

        @Override
        public ChangeHistory read(JsonReader in) {
            JsonObject asJsonObject = JsonParser.parseReader(in).getAsJsonObject();
            Pair<Student> fromTo = Pair.EMPTY.codec.fromJsonTree(asJsonObject.get("FromTo"));
            try {
                Date changeDate = dateTypeAdapter.fromJsonTree(asJsonObject.get("Time"));
                return new ChangeHistory(changeDate, fromTo);
            } catch (Exception e) {
                return new ChangeHistory(new Date(), fromTo);
            }
        }
    };
    private  final Date changeDate;
    private final Pair<Student> fromTo;

    public ChangeHistory(Date changeDate, Pair<Student> fromTo) {
        this.changeDate = changeDate;
        this.fromTo = fromTo;
    }

    @Override
    public String toString() {
        return "ChangeHistory{" +
                "changeDate=" + changeDate +
                ", fromTo=" + fromTo +
                '}';
    }

    public String[] toStringArray(){
        return new String[]{new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.changeDate),fromTo.getLeft().getName(),fromTo.getRight().getName()};
    }
}
