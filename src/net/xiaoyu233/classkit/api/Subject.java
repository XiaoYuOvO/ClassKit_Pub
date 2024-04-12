package net.xiaoyu233.classkit.api;

import net.xiaoyu233.classkit.util.Utils;

import java.util.HashMap;
import java.util.Map;

public enum Subject {
    CH("语文","yw"),
    EN("英语","yy"),
    MT("数学","sx"),
    CE("化学","hx"),
    PH("物理","wl"),
    BIO("生物","sw"),
    PO("政治","zz"),
    HI("历史","ls"),
    GEO("地理","dl"),
    NONE("无","");

    private static final Map<String,Subject> localizedToInstance = Utils.make(new HashMap<>(),map -> {
        for (Subject value : Subject.values()) {
            map.put(value.localizedName,value);
        }
    });
    private final String localizedName;
    private final String shortEngName;

    Subject(String localizedName, String shortEngName) {
        this.localizedName = localizedName;
        this.shortEngName = shortEngName;
    }

    public String getShortEngName() {
        return shortEngName;
    }

    public static Subject getFromLocalized(String localizedName){
        return localizedToInstance.getOrDefault(localizedName,Subject.NONE);
    }

    @Override
    public String toString() {
        return this.localizedName;
    }

    public String getLocalizedName() {
        return localizedName;
    }
}
