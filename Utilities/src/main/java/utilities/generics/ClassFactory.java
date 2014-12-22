package utilities.generics;

public class ClassFactory {

    public static class SomeClass<T> {

        public T createContents(Class<T> clazz) throws IllegalAccessException, InstantiationException {
            return clazz.newInstance();
        }
    }

}
