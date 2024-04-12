package net.xiaoyu233.classkit.keys;

public class KeyModifier {
    public static final KeyModifier EMPTY = new KeyModifier();
    private int mask;
    public KeyModifier(Modifier... modifiers){
        for (Modifier modifier : modifiers) {
            this.mask |= modifier.ji_code;
        }
    }

    public int getMask() {
        return mask;
    }

    public enum Modifier {
        WIN(8),
        SHIFT(4),
        CONTROL(2),
        ALT(1);
        private final int ji_code;
        Modifier(int ji_code){
            this.ji_code = ji_code;
        }

        public int getJi_Code() {
            return ji_code;
        }
    }
}
