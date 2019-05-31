package com.zhaohg.annotation;

@Table("user")
public class Filter {

    @Column("id")
    private int    stuId;
    @Column("name")
    private String stuName;
    @Column("email")
    private String email;

    public int getStuId() {
        return stuId;
    }

    public void setStuId(int stuId) {
        this.stuId = stuId;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
