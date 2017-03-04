package pt.ulisboa.tecnico.meic.sec;

/**
 * Created by francisco on 04/03/2017.
 */
public class InvalidTimestampException extends RuntimeException {
    private String message;

    InvalidTimestampException(String s) {
        message = s;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
