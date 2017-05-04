package pt.ulisboa.tecnico.meic.sec.lib;

public abstract class Password extends SecureEntity {
    protected String domain;
    protected String username;
    protected String password;
    protected String version;
    protected String device;
    protected String iv;
    protected String pwdSignature;

    public String getDomain() {
        return domain;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getVersion() {
        return version;
    }

    public String getDevice() {
        return device;
    }

    public String getIv() {
        return iv;
    }

    public String getPwdSignature() {
        return pwdSignature;
    }
}