package ua.kiev.vignatyev.vhome1.models;

/**
 * Created by vignatyev on 27.08.2015.
 */
public class Credentials {
    private String userName;
    private String userPass;
    private boolean savePassword;

    public Credentials(String userName, String userPass, Boolean savePassword) {
        this.userName = userName;
        this.userPass = userPass;
        this.savePassword = savePassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public boolean isSavePassword() {
        return savePassword;
    }
}
