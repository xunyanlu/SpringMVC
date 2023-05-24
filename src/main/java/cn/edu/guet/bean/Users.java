package cn.edu.guet.bean;

/**
 * @Author liwei
 * @Date 2023/5/16 19:45
 * @Version 1.0
 */

public class Users {
    private Long id;
    private String username;
    private String address;

    public Users(Long id, String username, String address) {
        this.id = id;
        this.username = username;
        this.address = address;
    }

    public Users(String username, String address) {
        this.username = username;
        this.address = address;
    }

    public Users() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
