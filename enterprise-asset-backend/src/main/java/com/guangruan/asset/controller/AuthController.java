package com.guangruan.asset.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.guangruan.asset.dto.LoginRequest;
import com.guangruan.asset.dto.UpdateProfileRequest;
import com.guangruan.asset.dto.ChangePasswordRequest;
import com.guangruan.asset.mapper.EnterpriseMapper;
import com.guangruan.asset.mapper.UserMapper;
import com.guangruan.asset.model.Enterprise;
import com.guangruan.asset.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserMapper userMapper;
    private final EnterpriseMapper enterpriseMapper;

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request) {
        User user = userMapper.findByPhone(request.getPhone());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "账号或密码错误");
        }

        java.util.List<Enterprise> enterprises = enterpriseMapper.findByUserId(user.getId());
        if (enterprises == null || enterprises.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "该账号未配置可访问的企业");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getName());
        userInfo.put("phone", user.getPhone());

        Map<String, Object> result = new HashMap<>();
        result.put("token", UUID.randomUUID().toString());
        result.put("user", userInfo);
        result.put("enterprises", enterprises);
        result.put("currentEnterpriseId", enterprises.get(0).getId());
        return result;
    }

    @GetMapping("/profile")
    public User profile(@NotNull @RequestParam Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @PutMapping("/profile")
    public User updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        User existing = userMapper.findById(request.getUserId());
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        existing.setName(request.getName());
        existing.setPhone(request.getPhone());
        int changed = userMapper.updateProfile(existing);
        if (changed == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "修改失败");
        }
        existing.setPassword(null);
        return existing;
    }

    @PostMapping("/change-password")
    public Map<String, Object> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        User existing = userMapper.findById(request.getUserId());
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        if (!existing.getPassword().equals(request.getOldPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "原密码不正确");
        }
        existing.setPassword(request.getNewPassword());
        int changed = userMapper.updatePassword(existing);
        if (changed == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "修改失败");
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "密码修改成功");
        return resp;
    }
}
