package pt.ulisboa.tecnico.meic.sec.exception;

/**
 * Created by francisco on 04/03/2017.
 */
public class NotEnoughNumberOfBytesException extends CryptoException {
    @Override
    public String getMessage() {
        return "The minimum bytes to get a secure byte[] is 16.";
    }
}
