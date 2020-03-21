package recyclerview.helper;

class NotNullHelper {
    static <T> T requireNonNull(T obj) {
        if (obj == null)
            throw new NullPointerException();
        return obj;
    }
}
