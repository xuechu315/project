package com.example.elderlycare.service;

import com.example.elderlycare.dto.response.UserResponse;
import com.example.elderlycare.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserResponse login(String username, String password);
    UserResponse getUserById(Integer userId);
    /** 获取 User 实体（用于生成 Token） */
    User getUserEntityById(Integer userId);
    UserResponse verify();
    List<User> getAllUsers();
    User createUser(User user);
    User updateUser(Integer id, User user);
    void deleteUser(Integer id);

    /**
     * 统一创建用户+角色关联表（一个事务内完成）
     * roleData 支持的 key：
     *   - role: elder/family/doctor
     *   - name, phone, age, gender, bloodType
     *   - elderIds (List<Integer>) - 创建 family 时关联的老人
     *   - familyUserIds (List<Integer>) - 创建 elder 时绑定的家属
     *   - doctorId (Integer) - 创建 elder 时绑定的医生
     */
    User createUserWithRelations(User user, Map<String, Object> roleData);
}
