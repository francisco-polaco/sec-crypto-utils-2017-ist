package pt.ulisboa.tecnico.meic.sec.lib;

public abstract class SecureEntity {
    public static final int ALREADY_EXISTS_CODE = 409;
    public static final int NOT_FOUND_CODE = 404;

    protected String publicKey;
    protected String timestamp;
    protected String nonce;
    protected String reqSignature;

    public String getPublicKey() {
        return publicKey;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getNonce() {
        return nonce;
    }

    public String getReqSignature() {
        return reqSignature;
    }
}
