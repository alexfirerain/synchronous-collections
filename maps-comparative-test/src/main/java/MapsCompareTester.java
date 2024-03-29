import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MapsCompareTester extends Tester {
    final int threadsCount;

    public MapsCompareTester(int[] dataSizes, int arrValMin, int arrValMax, int repetitions, int threadsCount) {
        super(dataSizes,
                arrValMin,
                arrValMax,
                repetitions);
        this.threadsCount = threadsCount;
    }

    @Override
    protected void executeTesting() throws InterruptedException {
        // для каждого из размеров данных
        for (int mapSize : dataSizes) {
            // подготовить данные
            HashMap<Integer, Integer> originalMap = createRandomHashMap(mapSize);
            Map<Integer, Integer> synchronizedMap = Collections.synchronizedMap(originalMap);
            Map<Integer, Integer> concurrentMap = new ConcurrentHashMap<>(originalMap);

            report.append("----------Карта из %d пар-------------\n".formatted(mapSize))
                    // выполнение теста на карте А
                    .append("\tСинхронизированная карта:\n")
                    .append(executeSingleBatch(synchronizedMap, repetitions))
                    // выполнение теста на карте Б
                    .append("\tМногопоточная карта:\n")
                    .append(executeSingleBatch(concurrentMap, repetitions))
                    .append("\n");
        }
        System.out.println(report);
    }

    private String executeSingleBatch(Map<Integer, Integer> map, int repetitions) throws InterruptedException {
        // вспомогательные данные
        String result;
        Integer[] accessOrder = ArrGenerator.generateUnique(map.size());
        Integer[] newValues = ArrGenerator.generate(map.size(), map.size());
        // подготовка секундомера
        long overallTestsDuration = 0;
        long maxDuration = 0;
        long minDuration = Long.MAX_VALUE;

        // повторы теста
        for (int i = 0; i < repetitions; i++) {

                // создание потоков
            List<Thread> threads = new ArrayList<>(threadsCount);
            for (int t = 0; t < threadsCount; t++) threads.add(new Thread(new RandomAccess(map, accessOrder)));
            // запуск секундомера
            long l = System.nanoTime();

            // выполнение тестируемой операции
                // запуск потоков
            for (Thread t : threads) t.start();
                // ожидание выполнения
            for (Thread t : threads) t.join();

            // остановка секундомера
            long duration = System.nanoTime() - l;
            // вычисление статистики результатов
            if (duration > maxDuration) maxDuration = duration;
            if (duration < minDuration) minDuration = duration;
            overallTestsDuration += duration;
        }
        long averageDuration = Math.round((double) overallTestsDuration / (double) repetitions);

        result = "Доступ ко всем элементам осуществлён %d потоками за среднее время %s (минимальное %s, максимальное %s)\n"
                .formatted(threadsCount,
                        nanoTimeFormatter(averageDuration),
                        nanoTimeFormatter(minDuration),
                        nanoTimeFormatter(maxDuration));

        // сброс секундомера
        overallTestsDuration = 0;
        maxDuration = 0;
        minDuration = Long.MAX_VALUE;

        // повторы теста
        for (int i = 0; i < repetitions; i++) {
            // запуск секундомера
            long l = System.nanoTime();
            // выполнение тестируемой операции
                // создание потоков
            List<Thread> threads = new ArrayList<>(threadsCount);
            for (int t = 0; t < threadsCount; t++) threads.add(new Thread(new RandomRecord(map, accessOrder, newValues)));
                // запуск потоков
            for (Thread t : threads) t.start();
                // ожидание выполнения
            for (Thread t : threads) t.join();
            // остановка секундомера
            long duration = System.nanoTime() - l;
            // вычисление статистики результатов
            if (duration > maxDuration) maxDuration = duration;
            if (duration < minDuration) minDuration = duration;
            overallTestsDuration += duration;
        }
        averageDuration = Math.round((double) overallTestsDuration / (double) repetitions);

        System.gc();
        return result + "Запись во все элементы осуществлена %d потоками за среднее время %s (минимальное %s, максимальное %s)\n"
                .formatted(threadsCount,
                        nanoTimeFormatter(averageDuration),
                        nanoTimeFormatter(minDuration),
                        nanoTimeFormatter(maxDuration));
    }


    private HashMap<Integer, Integer> createRandomHashMap(int size) {
        Integer[] keys = ArrGenerator.generateUnique(size);
        Integer[] values = ArrGenerator.generate(size, size);
        HashMap<Integer, Integer> hashMap = new HashMap<>(size);
        for (int i = 0; i < keys.length; i++) hashMap.put(keys[i], values[i]);
        return hashMap;
    }

    static class RandomAccess implements Runnable {
        private final Map<Integer, Integer> map;
        private final Integer[] accessOrder;
        public RandomAccess(Map<Integer, Integer> map, Integer[] accessOrder) {
            this.map = map;
            this.accessOrder = accessOrder;
        }
        @Override
        public void run() {
            Integer temp = 0;
            for (int i : accessOrder)
                temp = map.get(i);
        }
    }

    static class RandomRecord implements Runnable {
        private final Map<Integer, Integer> map;
        private final Integer[] accessOrder;
        private final Integer[] newValues;
        public RandomRecord(Map<Integer, Integer> map, Integer[] accessOrder, Integer[] newValues) {
            this.map = map;
            this.accessOrder = accessOrder;
            this.newValues = newValues;
        }
        @Override
        public void run() {
            for (int i = 0; i < accessOrder.length; i++)
                map.put(accessOrder[i], newValues[i]);
        }
    }

}
