import java.util.Random;

/**
 *  Генератор целочисленного массива
 */
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

//    public static Integer[] generateUniqueSlow(int length) {
//        List<Integer> list = new ArrayList<>(length);
//        Random rnd = new Random();
//        while (list.size() < length) {
//            int newRnd = rnd.nextInt(length);
//            if (list.contains(newRnd)) continue;        // это будет адски долго для больших массивов
//            list.add(newRnd);
//        }
//        return list.toArray(new Integer[0]);
//    }

    public static Integer[] generateUnique(int length) {
        Integer[] unique = new Integer[length];
        Random randomPlace = new Random();
        for (int i = 0; i < length; i++) {
            int next = randomPlace.nextInt(length);
            while (unique[next] != null) {
                next++;
                if (next == length) next = 0;
            }
            unique[next] = i;
        }
        return unique;
    }

}

