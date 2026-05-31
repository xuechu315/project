package com.elderly.care.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elderly.care.dto.Result;
import com.elderly.care.entity.Doctor;
import com.elderly.care.entity.Elder;
import com.elderly.care.entity.FamilyMember;
import com.elderly.care.entity.User;
import com.elderly.care.enums.UserType;
import com.elderly.care.service.DoctorService;
import com.elderly.care.service.ElderService;
import com.elderly.care.service.FamilyMemberService;
import com.elderly.care.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final DoctorService doctorService;
    private final ElderService elderService;
    private final FamilyMemberService familyMemberService;

    @PostMapping
    public Result<Map<String, Object>> createUserWithRole(@RequestBody Map<String, Object> body) {
        try {
            String role = (String) body.getOrDefault("role", "elder");
            String username = (String) body.get("username");
            String password = (String) body.get("password");
            String name = (String) body.get("name");
            String phone = (String) body.get("phone");

            if (username == null || password == null) {
                return Result.error("username and password required");
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setName(name);
            user.setPhone(phone);
            user.setUserType(UserType.valueOf(role));

            User created = userService.createUser(user);

            // create role-specific entity when needed
            if ("doctor".equals(role)) {
                Doctor d = new Doctor();
                d.setUserId(created.getId());
                d.setName(name);
                d.setPhone(phone);
                doctorService.createDoctor(d);
            } else if ("family".equals(role)) {
                FamilyMember f = new FamilyMember();
                f.setUserId(created.getId());
                Object elderIdObj = body.get("elderId");
                if (elderIdObj != null) f.setElderId(((Number) elderIdObj).intValue());
                f.setName(name);
                f.setPhone(phone);
                familyMemberService.createFamilyMember(f);
            } else if ("elder".equals(role)) {
                Elder e = new Elder();
                e.setUserId(created.getId());
                e.setAge(body.get("age") == null ? null : ((Number) body.get("age")).intValue());
                e.setGender((String) body.get("gender"));
                e.setBloodType((String) body.get("bloodType"));
                elderService.createElder(e);
            }

            return Result.success(Map.of("userId", created.getId()));
        } catch (Exception ex) {
            return Result.error("创建用户失败: " + ex.getMessage());
        }
    }

    /**
     * 获取所有用户列表
     */
    @GetMapping
    public Result<List<Map<String, Object>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<Map<String, Object>> list = new ArrayList<>();
        for (User u : users) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", u.getId());
            item.put("username", u.getUsername());
            item.put("name", u.getName());
            item.put("phone", u.getPhone());
            item.put("userType", u.getUserType().name());
            item.put("userTypeDesc", u.getUserType().getDescription());
            item.put("createdAt", u.getCreatedAt() != null ? u.getCreatedAt().toString() : null);
            list.add(item);
        }
        return Result.success(list);
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getUserById(@PathVariable Integer id) {
        User u = userService.getUserById(id);
        if (u == null) {
            return Result.error("用户不存在");
        }
        Map<String, Object> item = new HashMap<>();
        item.put("id", u.getId());
        item.put("username", u.getUsername());
        item.put("name", u.getName());
        item.put("phone", u.getPhone());
        item.put("userType", u.getUserType().name());
        item.put("userTypeDesc", u.getUserType().getDescription());
        item.put("createdAt", u.getCreatedAt() != null ? u.getCreatedAt().toString() : null);
        return Result.success(item);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<Map<String, Object>> updateUser(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            User existing = userService.getUserById(id);
            if (existing == null) {
                return Result.error("用户不存在");
            }

            String username = (String) body.get("username");
            String password = (String) body.get("password");
            String name = (String) body.get("name");
            String phone = (String) body.get("phone");
            String role = (String) body.get("role");

            if (username != null && !username.isEmpty()) existing.setUsername(username);
            if (password != null && !password.isEmpty()) existing.setPassword(password);
            if (name != null) existing.setName(name);
            if (phone != null) existing.setPhone(phone);
            if (role != null && !role.isEmpty()) existing.setUserType(UserType.valueOf(role));

            userService.updateUser(existing);

            // 家属角色更新时同步关联老人（按 userId 或 name 匹配，兼容旧数据）
            if ("family".equals(role) && body.containsKey("elderId")) {
                java.util.Optional<FamilyMember> fmOpt = familyMemberService.getFamilyMemberByUserId(existing.getId());
                if (fmOpt.isEmpty() && name != null) {
                    fmOpt = familyMemberService.getAllFamilyMembers().stream()
                        .filter(fm -> name.equals(fm.getName()))
                        .findFirst();
                }
                fmOpt.ifPresent(fm -> {
                    Object elderIdObj = body.get("elderId");
                    fm.setElderId(elderIdObj != null ? ((Number) elderIdObj).intValue() : null);
                    familyMemberService.updateFamilyMember(fm.getId(), fm);
                });
            }

            return Result.success(Map.of("userId", existing.getId()));
        } catch (Exception ex) {
            return Result.error("更新用户失败: " + ex.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Integer id) {
        try {
            User existing = userService.getUserById(id);
            if (existing == null) {
                return Result.error("用户不存在");
            }
            userService.deleteUser(id);
            return Result.success(null);
        } catch (Exception ex) {
            return Result.error("删除用户失败: " + ex.getMessage());
        }
    }
}
