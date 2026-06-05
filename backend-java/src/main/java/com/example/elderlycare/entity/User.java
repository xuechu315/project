package com.example.elderlycare.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Entity
@Table(name = "user", indexes = {
    @Index(name = "uk_username", columnList = "username", unique = true)
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    private UserType userType;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "blood_type", length = 10)
    private String bloodType;

    @Column(name = "height")
    private Float height;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public User() {}

    public User(Integer id, String username, String password, UserType userType, String name,
                Integer age, String gender, String bloodType, Float height, String phone, Float weight, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.bloodType = bloodType;
        this.height = height;
        this.phone = phone;
        this.weight = weight;
        this.createdAt = createdAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public Float getHeight() { return height; }
    public void setHeight(Float height) { this.height = height; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Float getWeight() { return weight; }
    public void setWeight(Float weight) { this.weight = weight; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * 用户类型枚举
     */
    public enum UserType {
        elder, family, doctor, admin
    }
}