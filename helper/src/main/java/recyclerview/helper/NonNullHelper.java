package recyclerview.helper;

class NonNullHelper {
    static <T> T requireNonNull(T obj) {
        if (obj == null)
            throw new NullPointerException();
        return obj;
    }
}
