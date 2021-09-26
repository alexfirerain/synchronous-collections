public abstract class Tester {

    final protected int[] dataSizes;      // размеры данных для серии тестов
    final protected int arrValMin;        // нижнее значение для данных
    final protected int arrValMax;        // верхнее значение для данных
    final protected int repetitions;      // количество повторов каждого теста
    final protected StringBuilder report; // построитель отчёта

    protected Tester(int[] dataSizes, int arrValMin, int arrValMax, int repetitions) {
        this.dataSizes = dataSizes;
        this.arrValMin = arrValMin;
        this.arrValMax = arrValMax;
        this.repetitions = repetitions;
        report = new StringBuilder("");
    }

    abstract protected void executeTesting() throws InterruptedException;

    static protected String nanoTimeFormatter(long duration) {
        if (duration < 10_000)
            return duration + " нс";
        if (duration > 1_000_000_000)
            return "%.3f с".formatted((double) duration / 1_000_000_000f);
        return "%.3f мс".formatted((double) duration / 1_000_000f);
    }

}
