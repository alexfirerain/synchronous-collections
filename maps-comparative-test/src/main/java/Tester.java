public abstract class Tester {

    protected Tester(int[] dataSizes, int arrValMin, int arrValMax, int repetitions) {
        this.dataSizes = dataSizes;
        this.arrValMin = arrValMin;
        this.arrValMax = arrValMax;
        this.repetitions = repetitions;
        report = new StringBuilder();
    }

    abstract protected void executeTesting();
    protected final int[] dataSizes;      // размеры данных для серии тэстов
    protected final int arrValMin;        // нижнее значение для данных
    protected final int arrValMax;        // верхнее значение для данных
    protected final int repetitions;      // количество повторов каждого тэста
    protected final StringBuilder report; // построитель отчёта

    static protected String nanoTimeFormatter(long duration) {
        if (duration < 10_000)
            return duration + " нс";
        if (duration > 1_000_000_000)
            return "%.3f с".formatted((double) duration / 1_000_000_000f);
        return "%.3f мс".formatted((double) duration / 1_000_000f);
    }

}
