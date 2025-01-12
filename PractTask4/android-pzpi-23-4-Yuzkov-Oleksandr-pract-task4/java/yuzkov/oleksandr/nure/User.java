package yuzkov.oleksandr.nure;

public class User {
    Integer userId;
    String userName;
    Integer userAge;

    public User(Integer userId, String userName, Integer userAge) {
        this.userId = userId;
        this.userName = userName;
        this.userAge = userAge;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Integer getUserAge() {
        return userAge;
    }
}
