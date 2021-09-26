import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.Function;

public class ArraysSumTester extends Tester {
    private final int[] dataSizes;      // размеры данных для серии тестов
    private final int arrValMin;        // нижнее значение для данных
    private final int arrValMax;        // верхнее значение для данных
    private final int repetitions;      // количество повторов каждого теста
    private int fractality;      // степень фрактальности параллельных расчётов
    private final StringBuilder report; // построитель отчёта

    // конструктор: размеры данных, нижнее и верхнее значение данного, повторения
    public ArraysSumTester(int[] dataSizes, int arrValMin, int arrValMax, int repetitions, int fractality) {
        super(dataSizes, arrValMin, arrValMax, repetitions);
        this.dataSizes = dataSizes;
        this.arrValMin = arrValMin;
        this.arrValMax = arrValMax;
        this.repetitions = repetitions;
        this.fractality = fractality;
        report = new StringBuilder();

        // описание отчёта
        report.append(("Будет проведено сравнительное тестирование двух реализаций суммирования массива:" +
                        " в один поток и рекурсивное (до %d потоков).\n").formatted(actualThreads()))
                .append("Будут сгенерированы массивы длиной: ");
        for (int i = 0; i < dataSizes.length; i++) {
            report.append(dataSizes[i]);

            if (i == dataSizes.length - 1)         report.append("; ");
            else if (i == dataSizes.length - 2)    report.append(" и ");
            else                                   report.append(", ");
        }
        report.append("заполнены они будут случайными целыми числами от %d до %d.\n"
                        .formatted(arrValMin, arrValMax))
                .append("Для каждого массива будет исполнено по %d прогонов сначала одним методом, потом другим.\n\n"
                        .formatted(repetitions));
    }

    private int actualThreads() {
        int actualThreads = 1;
        while (fractality > actualThreads)
            actualThreads *= 2;
        return actualThreads;
    }

    // запуск теста: функция, данные, повторения
    public String executeSingleBatch(Function<Integer[], Long> operation, Integer[] data, int repetitions) {
        // подготовка секундомера
        long overallTestsDuration = 0;
        long maxDuration = 0;
        long minDuration = Long.MAX_VALUE;
        // держатель результата вычисления
        long sum = 0;

        // повторы теста
        for (int i = 0; i < repetitions; i++) {

            // запуск секундомера
            long l = System.nanoTime();
            // выполнение тестируемой операции
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
    // запуск серии тестов

    @Override
    public void executeTesting() {
        // для каждого из размеров данных
        for (int arrLength : dataSizes) {
            // подготовить данные
            Integer[] testArray = ArrGenerator.generate(arrLength, arrValMin, arrValMax);
            report.append("----------Массив из %d элементов-------------\n".formatted(arrLength))
                    // выполнение теста на функции А
                    .append("\tОднопоточное суммирование:\n")
                    .append(executeSingleBatch(singleThreadSum, testArray, repetitions))
                    // выполнение теста на функции Б
                    .append("\tРекурсивное суммирование (%d потоков):\n".formatted(actualThreads()))
                    .append(executeSingleBatch(recursiveSum, testArray, repetitions))
                    .append("\n");
        }
        System.out.println(report.toString());
    }
    // тестируемая функция А
    final Function<Integer[], Long> singleThreadSum = (Integer[] arr) -> {
        long sum = 0;
        for (int i : arr)
            sum += i;
        return sum;
    };

    // тестируемая функция Б
    final Function<Integer[], Long> recursiveSum = (Integer[] arr) ->
            new ForkJoinPool()
                    .invoke(new ParallelSum(arr, 0, arr.length - 1, fractality));


    static class ParallelSum extends RecursiveTask<Long> {
        Integer[] arr;
        final int fractality;
        int beginning, ending, range;


        public ParallelSum(Integer[] arr, int beginning, int ending, int fractality) {
            this.arr = arr;                 // обрабатываемый массив
            this.beginning = beginning;     
            this.ending = ending;
            this.fractality = fractality;   // на сколько можно разбивать
            range = ending - beginning;
        }

        @Override
        protected Long compute() {
            if (range > arr.length / fractality)
                return splitAndCompute();
            int s = 0;
            for (int i = beginning; i <= ending; i++)
                s += arr[i];
            return (long) s;

        }

        private Long splitAndCompute() {
            int median = range / 2 + beginning;
            ParallelSum semisum1 = new ParallelSum(arr, beginning, median, fractality );
            ParallelSum semisum2 = new ParallelSum(arr, median + 1, ending, fractality );
            invokeAll(semisum1, semisum2);
            return semisum1.join() + semisum2.join();
        }

    }

}
