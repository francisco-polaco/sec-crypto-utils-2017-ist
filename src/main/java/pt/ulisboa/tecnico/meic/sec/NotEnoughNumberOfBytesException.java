package pt.ulisboa.tecnico.meic.sec;

/**
 * Created by francisco on 04/03/2017.
 */
public class NotEnoughNumberOfBytesException extends RuntimeException {
    @Override
    public String getMessage() {
        return "The minimum bytes to get a secure byte[] is 16.";
    }
}
