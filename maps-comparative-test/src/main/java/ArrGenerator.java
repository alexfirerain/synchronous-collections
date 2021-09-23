public class ArrGenerator {

    public static Integer[] generate(int length, int MIN, int MAX) {
        int min = Math.min(MIN, MAX);
        int max = Math.max(MIN, MAX);
        Integer[] arr = new Integer[length];
        for (int i = 0; i < length; i++)
            arr[i] = (int) (Math.random() * (max - min)) + min;
        return arr;
    }

    public static Integer[] generate(int length, int MAX) {
        return generate(length, 0, MAX);
    }

    public static Integer[] generate(int length) {
        return generate(length, 255);
    }
}

