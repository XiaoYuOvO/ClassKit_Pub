package net.xiaoyu233.classkit.api.pos;

public interface IClassPosition<T extends IClassPosition<?>> {
    int getGroupIndex();
    int getDeskIndex();
    Side getSide();
    T getDeskMate();

    default boolean isSame(IClassPosition<?> other) {
        return other.getDeskIndex() == getGroupIndex() && other.getGroupIndex() == getGroupIndex() && other.getSide() == getSide();
    }
}
