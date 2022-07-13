package com.itecknologigroupofcompanies.itecklite;
import java.lang.String;
public class ResponseLoginCheck {
    String  contact, email,success;

    public ResponseLoginCheck(String contact, String email, String success) {
        this.contact = contact;
        this.email = email;
        this.success = success;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
