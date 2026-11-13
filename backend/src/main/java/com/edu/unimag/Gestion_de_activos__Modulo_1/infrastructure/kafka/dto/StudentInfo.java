package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentInfo {

    @JsonProperty("student_code")
    private String studentCode;

    private String mail;

    public StudentInfo() {
    }

    public StudentInfo(String studentCode, String mail) {
        this.studentCode = studentCode;
        this.mail = mail;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
