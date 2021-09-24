public abstract class Tester {

    protected abstract void prepareData(int dataSize);

    static protected String nanoTimeFormatter(long duration) {
        if (duration < 10_000)
            return duration + " нс";
        if (duration > 1_000_000_000)
            return "%.3f с".formatted((double) duration / 1_000_000_000f);
        return "%.3f мс".formatted((double) duration / 1_000_000f);
    }

}
