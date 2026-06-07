package com.example.elderlycare.controller;

import com.example.elderlycare.dto.request.LoginRequest;
import com.example.elderlycare.dto.response.ApiResponse;
import com.example.elderlycare.dto.response.LoginResponse;
import com.example.elderlycare.dto.response.UserResponse;
import com.example.elderlycare.entity.ElderFamily;
import com.example.elderlycare.entity.User;
import com.example.elderlycare.security.TokenManager;
import com.example.elderlycare.service.ElderFamilyService;
import com.example.elderlycare.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 用户控制器 - 登录 + CRUD
 */
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ElderFamilyService elderFamilyService;

    @Autowired
    private TokenManager tokenManager;

    /**
     * 用户登录
     * POST /api/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            UserResponse userResponse = userService.login(request.getUsername(), request.getPassword());

            // 从数据库获取完整 User 实体以生成 Token
            User user = userService.getUserEntityById(userResponse.getId());

            // 生成 Token
            String token = tokenManager.createToken(user);

            LoginResponse response = LoginResponse.of(userResponse);
            response.setToken(token);

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "用户名或密码错误"));
        }
    }

    /**
     * 验证用户（从 Token 中获取当前用户）
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<LoginResponse>> verify(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            UserResponse u = userService.getUserById(user.getId());
            return ResponseEntity.ok(ApiResponse.success(LoginResponse.of(u)));
        }
        // 没有Token时回退
        UserResponse user = userService.verify();
        LoginResponse resp = LoginResponse.of(user);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    /**
     * 获取用户信息
     * GET /api/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Integer userId) {
        try {
            UserResponse user = userService.getUserById(userId);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(404, e.getMessage()));
        }
    }

    // ==================== 管理员CRUD接口 ====================

    /**
     * 获取所有用户列表
     * GET /api/users
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<Map<String, Object>> list = new ArrayList<>();
        for (User u : users) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", u.getId());
            item.put("username", u.getUsername());
            item.put("name", u.getName());
            item.put("phone", u.getPhone());
            item.put("userType", u.getUserType().name());
            item.put("userTypeDesc", getUserTypeDesc(u.getUserType().name()));
            item.put("createdAt", u.getCreatedAt() != null ? u.getCreatedAt().toString() : null);
            list.add(item);
        }
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    /**
     * 创建用户（同时自动创建角色对应的关联记录）
     * POST /api/users
     */
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createUser(@RequestBody Map<String, Object> body) {
        try {
            String role = (String) body.getOrDefault("role", "elder");
            String username = (String) body.get("username");
            String password = (String) body.get("password");
            String name = (String) body.get("name");
            String phone = (String) body.get("phone");

            if (username == null || password == null) {
                return ResponseEntity.ok(ApiResponse.error(400, "username and password required"));
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setName(name);
            user.setPhone(phone);
            user.setUserType(User.UserType.valueOf(role));

            // 使用统一 Service 方法在事务内创建用户+关联表
            User created = userService.createUserWithRelations(user, body);

            return ResponseEntity.ok(ApiResponse.success(Map.of("userId", created.getId())));
        } catch (Exception ex) {
            return ResponseEntity.ok(ApiResponse.error(500, "创建用户失败: " + ex.getMessage()));
        }
    }

    /**
     * 获取单个用户
     * GET /api/users/{id}
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserById(@PathVariable Integer id) {
        try {
            UserResponse u = userService.getUserById(id);
            Map<String, Object> item = new HashMap<>();
            item.put("id", u.getId());
            item.put("username", u.getUsername());
            item.put("name", u.getName());
            item.put("phone", u.getPhone());
            item.put("userType", u.getUserType());
            item.put("userTypeDesc", getUserTypeDesc(u.getUserType()));
            item.put("createdAt", u.getCreatedAt());
            return ResponseEntity.ok(ApiResponse.success(item));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(404, e.getMessage()));
        }
    }

    /**
     * 更新用户
     * PUT /api/users/{id}
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateUser(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            User existing = new User();
            if (body.containsKey("username")) existing.setUsername((String) body.get("username"));
            if (body.containsKey("password")) existing.setPassword((String) body.get("password"));
            if (body.containsKey("name")) existing.setName((String) body.get("name"));
            if (body.containsKey("phone")) existing.setPhone((String) body.get("phone"));
            if (body.containsKey("role")) existing.setUserType(User.UserType.valueOf((String) body.get("role")));

            userService.updateUser(id, existing);

            // 家属角色更新时同步关联老人（支持多选）
            String role = (String) body.get("role");
            if ("family".equals(role) && (body.containsKey("elderIds") || body.containsKey("elderId"))) {
                // 获取该家属当前所有绑定行
                List<ElderFamily> existingBinds = elderFamilyService.getFamilyMemberByUserId(id);
                // 确定新的 elderId 列表
                List<Integer> newElderIds = new ArrayList<>();
                if (body.containsKey("elderIds")) {
                    for (Object o : (List<?>) body.get("elderIds")) {
                        if (o != null) newElderIds.add(((Number) o).intValue());
                    }
                } else {
                    Object elderIdObj = body.get("elderId");
                    if (elderIdObj != null) newElderIds.add(((Number) elderIdObj).intValue());
                }
                // 删除不再绑定的行
                for (ElderFamily ef : existingBinds) {
                    if (!newElderIds.contains(ef.getElderId())) {
                        elderFamilyService.deleteFamilyMember(ef.getId());
                    }
                }
                // 新增绑定行
                for (Integer elderId : newElderIds) {
                    boolean alreadyBound = existingBinds.stream().anyMatch(ef -> elderId.equals(ef.getElderId()));
                    if (!alreadyBound) {
                        ElderFamily newEf = new ElderFamily();
                        newEf.setUserId(id);
                        newEf.setElderId(elderId);
                        newEf.setName((String) body.get("name"));
                        elderFamilyService.createFamilyMember(newEf);
                    }
                }
            }

            return ResponseEntity.ok(ApiResponse.success(Map.of("userId", id)));
        } catch (Exception ex) {
            return ResponseEntity.ok(ApiResponse.error(500, "更新用户失败: " + ex.getMessage()));
        }
    }

    /**
     * 删除用户
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception ex) {
            return ResponseEntity.ok(ApiResponse.error(500, "删除用户失败: " + ex.getMessage()));
        }
    }

    private String getUserTypeDesc(String type) {
        Map<String, String> map = Map.of("elder", "老人", "family", "家属", "doctor", "医生", "admin", "管理员");
        return map.getOrDefault(type, type);
    }
}
