import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Call {
//    private final TelephoneExchangeSimulator source;
    private final Date callDate;

    public Call() {
//        this.source = source;
        callDate = new Date();
    }

    @Override
    public String toString() {
        return "звонок АТС-%d от %s".formatted(hashCode(),
                new SimpleDateFormat("mm:ss").format(callDate));
    }

    @Override
    public int hashCode() {
        return Objects.hash(callDate);
    }
}
