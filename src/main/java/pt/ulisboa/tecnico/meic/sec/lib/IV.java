package pt.ulisboa.tecnico.meic.sec.lib;

public abstract class IV extends SecureEntity {
    protected String hash;
    protected String value;

    public String getHash() {
        return hash;
    }

    public String getValue() {
        return value;
    }
}
