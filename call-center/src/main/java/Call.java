import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Call {
    private final TelephoneExchangeSimulator source;
    private final Date callDate;

    public Call(TelephoneExchangeSimulator source) {
        this.source = source;
        callDate = new Date();
    }

    @Override
    public String toString() {
        return "Звонок АТС-%d (%s)".formatted(hashCode(),
                new SimpleDateFormat("dd MMM HH:mm:ss").format(callDate));
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, callDate);
    }
}
