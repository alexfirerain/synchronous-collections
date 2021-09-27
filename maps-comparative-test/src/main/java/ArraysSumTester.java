import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.Function;

public class ArraysSumTester extends Tester {
    // тестируемая функция А
    final private Function<Integer[], Long> singleThreadSum;
    // тестируемая функция Б
    final private Function<Integer[], Long> recursiveSum;
    final int fractality;       // степень фрактальности параллельных расчётов, фактически поле не нужно

    // конструктор: размеры данных, нижнее и верхнее значение, повторения, фрактальность
    public ArraysSumTester(int[] dataSizes, int arrValMin, int arrValMax, int repetitions, int fractality) {
        super(dataSizes,
                arrValMin,
                arrValMax,
                repetitions);
        this.fractality = fractality;

        singleThreadSum = (Integer[] arr) -> {
            long sum = 0;
            for (int i : arr)
                sum += i;
            return sum;
        };

        recursiveSum = (Integer[] arr) ->
                new ForkJoinPool()
                        .invoke(new ParallelSum(arr, 0, arr.length - 1, fractality));

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

    // на сколько фактически частей дихотомически фрактализуется массив
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
                    .append("\tРекурсивное суммирование (до %d потоков):\n".formatted(actualThreads()))
                    .append(executeSingleBatch(recursiveSum, testArray, repetitions))
                    .append("\n");
        }
        System.out.println(report.toString());
    }


    static class ParallelSum extends RecursiveTask<Long> {
        Integer[] arr;
        final int fractality;
        int beginning, ending, range;

        //конструктор рекурсивной задачи
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
