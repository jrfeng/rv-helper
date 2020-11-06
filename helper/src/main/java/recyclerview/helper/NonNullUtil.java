package recyclerview.helper;

/**
 * 用于代替 Objects.requireNonNull(T obj) 方法。
 * <p>
 * 因为 Objects.requireNonNull(T obj) 方法需要 API Level 19，而本项目最低需要支持到 API Level 14。
 */
class NonNullUtil {
    static <T> T requireNonNull(T obj) {
        if (obj == null)
            throw new NullPointerException();
        return obj;
    }
}
