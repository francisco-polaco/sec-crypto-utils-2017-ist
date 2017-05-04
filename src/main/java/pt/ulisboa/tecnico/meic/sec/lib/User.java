package pt.ulisboa.tecnico.meic.sec.lib;

public abstract class User extends SecureEntity {
    protected String fingerprint;

    public String getFingerprint() {
        return fingerprint;
    }
}
