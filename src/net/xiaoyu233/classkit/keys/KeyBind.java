package net.xiaoyu233.classkit.keys;

public class KeyBind {
    private final Keys keyId;
    private final KeyModifier keyModifier;
    public KeyBind(Keys keyId, KeyModifier keyModifier){
        this.keyId = keyId;
        this.keyModifier = keyModifier;
    }

    public int getKeyModifier() {
        return keyModifier.getMask();
    }

    public int getKeyId() {
        return keyId.getVk_code();
    }
}
