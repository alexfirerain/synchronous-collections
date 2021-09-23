import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.Function;

public class ArraysSumTester extends Tester {
    private final int[] dataSizes;
    private final int arrValMin;
    private final int arrValMax;
    private final int repetitions;
    private final StringBuilder report;

    public ArraysSumTester(int[] dataSizes, int arrValMin, int arrValMax, int repetitions) {
        this.dataSizes = dataSizes;
        this.arrValMin = arrValMin;
        this.arrValMax = arrValMax;
        this.repetitions = repetitions;
        report = new StringBuilder();
        report.append("Будет проведено сравнительное тестирование двух реализаций суммирования массива: в один поток и рекурсивное.\n")
                .append("Будут сгенерированы массивы длиной: ");
        for (int i = 0; i < dataSizes.length; i++) {
            report.append( dataSizes[i]);

            if (i == dataSizes.length - 1)         report.append("; ");
            else if (i == dataSizes.length - 2)    report.append(" и ");
            else                                    report.append(", ");
        }
        report.append("заполнены они будут случайными целыми числами от %d до %d.\n"
                        .formatted(arrValMin, arrValMax))
                .append("Для каждого массива будет исполнено по %d прогонов сначала одним методом, потом другим.\n\n"
                        .formatted(repetitions));
    }

    public String executeSingleBatch(Function<Integer[], Long> operation, Integer[] data, int repetitions) {

        long overallTestsDuration = 0;
        long maxDuration = 0;
        long minDuration = Long.MAX_VALUE;
        long sum = 0;

        for (int i = 0; i < repetitions; i++) {

            long l = System.nanoTime();
            sum = operation.apply(data);
            long duration = System.nanoTime() - l;

            if (duration > maxDuration) maxDuration = duration;
            if (duration < minDuration) minDuration = duration;
            overallTestsDuration += duration;
        }
        long averageDuration = Math.round((double) overallTestsDuration / (double) repetitions);

        return "Сумма %d вычислена за среднее время %s (минимальное %s, максимальное %s)\n"
                .formatted(sum,
                        nanoTimeFormatter(averageDuration),
                        nanoTimeFormatter(minDuration),
                        nanoTimeFormatter(maxDuration));
    }

    public void executeTesting() {
        for (int arrLength : dataSizes) {
            Integer[] testArray = ArrGenerator.generate(arrLength, arrValMin, arrValMax);
            report.append("----------Массив из %d элементов-------------\n".formatted(arrLength))
                    .append("\tОднопоточное суммирование:\n")
                    .append(executeSingleBatch(singleThreadSum, testArray, repetitions))
                    .append("\tРекурсивное суммирование:\n")
                    .append(executeSingleBatch(recursiveSum, testArray, repetitions))
                    .append("\n");
        }
        System.out.println(report.toString());
    }

    static Function<Integer[], Long> singleThreadSum = (Integer[] arr) -> {
        long sum = 0;
        for (int i : arr)
            sum += i;
        return sum;
    };

    static Function<Integer[], Long> recursiveSum = (Integer[] arr) ->
            new ForkJoinPool()
                    .invoke(new ParallelSum(arr, 0, arr.length - 1));


    static class ParallelSum extends RecursiveTask<Long> {
        Integer[] arr;
        int beginning, ending, range;


        public ParallelSum(Integer[] arr, int beginning, int ending) {
            this.arr = arr;
            this.beginning = beginning;
            this.ending = ending;
            range = ending - beginning;
        }

        @Override
        protected Long compute() {
            if (range == 0)
                return (long) arr[beginning];
            else if (range == 1)
                return (long) (arr[beginning] + arr[beginning + 1]);
            return splitAndCompute();
        }

        private Long splitAndCompute() {
            int median = range / 2 + beginning;
            ParallelSum semisum1 = new ParallelSum(arr, beginning, median);
            ParallelSum semisum2 = new ParallelSum(arr, median + 1, ending);
            invokeAll(semisum1, semisum2);
            return semisum1.join() + semisum2.join();
        }

    }

}
