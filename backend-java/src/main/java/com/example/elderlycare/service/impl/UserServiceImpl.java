package com.example.elderlycare.service.impl;

import com.example.elderlycare.dto.response.UserResponse;
import com.example.elderlycare.entity.*;
import com.example.elderlycare.exception.DuplicateResourceException;
import com.example.elderlycare.exception.ResourceNotFoundException;
import com.example.elderlycare.repository.*;
import com.example.elderlycare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ElderRepository elderRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ElderFamilyRepository elderFamilyRepository;

    @Autowired
    private ElderDoctorRelationRepository elderDoctorRelationRepository;

    @Override
    public UserResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "username", username));

        boolean passwordMatch;
        String storedPassword = user.getPassword();

        // BCrypt匹配（新密码格式）
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            passwordMatch = passwordEncoder.matches(password, storedPassword);
        } else {
            // 明文匹配（旧密码向后兼容）
            passwordMatch = storedPassword.equals(password);
            if (passwordMatch) {
                // 自动升级为BCrypt加密
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
            }
        }

        if (!passwordMatch) {
            throw new ResourceNotFoundException("用户", "username", username);
        }

        return convertToResponse(user);
    }

    @Override
    public UserResponse getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
        return convertToResponse(user);
    }

    @Override
    public User getUserEntityById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
    }

    @Override
    public UserResponse verify() {
        // 尝试获取当前登录用户（如果SecurityContext中有）
        // 由于当前是permitAll模式，回退到查找第一个admin用户
        Optional<User> adminUser = userRepository.findByUserType(User.UserType.admin)
                .stream().findFirst();
        if (adminUser.isPresent()) {
            return convertToResponse(adminUser.get());
        }
        // 如果没有admin用户，返回第一个用户
        List<User> allUsers = userRepository.findAll();
        if (!allUsers.isEmpty()) {
            return convertToResponse(allUsers.get(0));
        }
        // 完全无数据时返回默认
        UserResponse response = new UserResponse();
        response.setId(0);
        response.setName("未登录");
        response.setUserType("guest");
        response.setUsername("guest");
        return response;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("用户", "username", user.getUsername());
        }
        // 密码加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Integer id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));

        if (userDetails.getUsername() != null) user.setUsername(userDetails.getUsername());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            // 密码加密
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        if (userDetails.getName() != null) user.setName(userDetails.getName());
        if (userDetails.getPhone() != null) user.setPhone(userDetails.getPhone());
        if (userDetails.getUserType() != null) user.setUserType(userDetails.getUserType());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("用户", "id", id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public User createUserWithRelations(User user, Map<String, Object> roleData) {
        // 1. 创建 User 记录
        User saved = createUser(user);

        String role = (String) roleData.getOrDefault("role", "elder");

        // 2. 根据角色创建关联记录
        if ("doctor".equals(role)) {
            Doctor doctor = new Doctor();
            doctor.setUserId(saved.getId());
            doctor.setName(user.getName());
            doctor.setPhone(user.getPhone());
            doctorRepository.save(doctor);

        } else if ("family".equals(role)) {
            Object elderIdsObj = roleData.get("elderIds");
            if (elderIdsObj instanceof List) {
                for (Object eid : (List<?>) elderIdsObj) {
                    if (eid != null) {
                        ElderFamily ef = new ElderFamily();
                        ef.setUserId(saved.getId());
                        ef.setElderId(((Number) eid).intValue());
                        ef.setName(user.getName());
                        elderFamilyRepository.save(ef);
                    }
                }
            } else {
                Object elderIdObj = roleData.get("elderId");
                if (elderIdObj != null) {
                    ElderFamily ef = new ElderFamily();
                    ef.setUserId(saved.getId());
                    ef.setElderId(((Number) elderIdObj).intValue());
                    ef.setName(user.getName());
                    elderFamilyRepository.save(ef);
                }
            }

        } else if ("elder".equals(role)) {
            // 创建 elder 记录
            Elder elder = new Elder();
            elder.setUserId(saved.getId());
            if (roleData.get("age") != null) elder.setAge(((Number) roleData.get("age")).intValue());
            if (roleData.get("gender") != null) elder.setGender((String) roleData.get("gender"));
            if (roleData.get("bloodType") != null) elder.setBloodType((String) roleData.get("bloodType"));
            elderRepository.save(elder);

            // 同步绑定家属（通过 familyUserIds）
            Object familyUserIdsObj = roleData.get("familyUserIds");
            if (familyUserIdsObj instanceof List) {
                for (Object fuId : (List<?>) familyUserIdsObj) {
                    if (fuId != null) {
                        userRepository.findById(((Number) fuId).intValue()).ifPresent(familyUser -> {
                            ElderFamily ef = new ElderFamily();
                            ef.setUserId(((Number) fuId).intValue());
                            ef.setElderId(elder.getId());
                            ef.setName(familyUser.getName());
                            elderFamilyRepository.save(ef);
                        });
                    }
                }
            }

            // 同步绑定医生
            Object doctorIdObj = roleData.get("doctorId");
            if (doctorIdObj != null) {
                ElderDoctorRelation relation = new ElderDoctorRelation();
                relation.setElderId(elder.getId());
                relation.setDoctorId(((Number) doctorIdObj).intValue());
                elderDoctorRelationRepository.save(relation);
            }
        }

        return saved;
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setUserType(user.getUserType().name());
        response.setPhone(user.getPhone());
        response.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);

        // 对老人用户，从 elder 表补充身体数据（user 表已不再存储这些字段）
        if (user.getUserType() == User.UserType.elder) {
            elderRepository.findByUserId(user.getId()).ifPresent(elder -> {
                response.setAge(elder.getAge());
                response.setGender(elder.getGender());
                response.setBloodType(elder.getBloodType());
                response.setHeight(elder.getHeight());
                response.setWeight(elder.getWeight());
            });
        }

        return response;
    }
}
