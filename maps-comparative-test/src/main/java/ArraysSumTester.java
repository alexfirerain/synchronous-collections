import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.Function;

public class ArraysSumTester extends Tester {
    private final int[] dataSizes;      // размеры данных для серии тэстов
    private final int arrValMin;        // нижнее значение для данных
    private final int arrValMax;        // верхнее значение для данных
    private final int repetitions;      // количество повторов каждого тэста
    private final StringBuilder report; // построитель отчёта

    @Override
    protected void prepareData(int dataSize) {
        Integer[] testDatum = ArrGenerator.generate(dataSize);
    }

    // конструктор: размеры данных, нижнее и верхнее значение данного, повторения
    public ArraysSumTester(int[] dataSizes, int arrValMin, int arrValMax, int repetitions) {
        this.dataSizes = dataSizes;
        this.arrValMin = arrValMin;
        this.arrValMax = arrValMax;
        this.repetitions = repetitions;
        report = new StringBuilder();

        // описание отчёта
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

    // запуск тэста: функция, данные, повторения
    public String executeSingleBatch(Function<Integer[], Long> operation, Integer[] data, int repetitions) {
        // подготовка секундомера
        long overallTestsDuration = 0;
        long maxDuration = 0;
        long minDuration = Long.MAX_VALUE;
        // держатель результата вычисления
        long sum = 0;

        // повторы тэста
        for (int i = 0; i < repetitions; i++) {

            // запуск секундомера
            long l = System.nanoTime();
            // выполнение тэстируемой операции
            sum = operation.apply(data);
            // остановка секундомера
            long duration = System.nanoTime() - l;

            // вычисление статистики результатов
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

    // запуск серии тэстов
    public void executeTesting() {
        // для каждого из размеров данных
        for (int arrLength : dataSizes) {
            // подготовить данные
            Integer[] testArray = ArrGenerator.generate(arrLength, arrValMin, arrValMax);
            report.append("----------Массив из %d элементов-------------\n".formatted(arrLength))
                    // выполнение тэста на функции А
                    .append("\tОднопоточное суммирование:\n")
                    .append(executeSingleBatch(singleThreadSum, testArray, repetitions))
                    // выполнение тэста на функции Б
                    .append("\tРекурсивное суммирование:\n")
                    .append(executeSingleBatch(recursiveSum, testArray, repetitions))
                    .append("\n");
        }
        System.out.println(report.toString());
    }

    // тэстируемая функция А
    static Function<Integer[], Long> singleThreadSum = (Integer[] arr) -> {
        long sum = 0;
        for (int i : arr)
            sum += i;
        return sum;
    };

    // тэстируемая функция Б
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
