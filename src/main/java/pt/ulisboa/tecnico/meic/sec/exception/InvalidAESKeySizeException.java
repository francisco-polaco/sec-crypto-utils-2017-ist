package pt.ulisboa.tecnico.meic.sec.exception;

/**
 * Created by francisco on 04/03/2017.
 */
public class InvalidAESKeySizeException extends CryptoException {
    private int bits = -1;

    public InvalidAESKeySizeException(int bits) {
        this.bits = bits;
    }

    @Override
    public String getMessage() {
        String message = "Size must be 128, 192 or 256.";
        if(bits != -1) message += " You asked for " + bits;
        return message;
    }
}
