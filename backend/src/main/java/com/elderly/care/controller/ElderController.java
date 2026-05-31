package com.elderly.care.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elderly.care.dto.ElderDTO;
import com.elderly.care.dto.Result;
import com.elderly.care.entity.Elder;
import com.elderly.care.service.ElderService;
import com.elderly.care.service.UserService;
import com.elderly.care.utils.DtoConverter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/elders")
@RequiredArgsConstructor
@Tag(name = "老人管理", description = "老人信息的查询与创建接口")
public class ElderController {

    private final ElderService elderService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "获取所有老人")
    public Result<List<ElderDTO>> getAll() {
        return Result.success(elderService.getAllElders().stream()
                .map(e -> DtoConverter.convertToElderDTO(e, userService.getUserById(e.getUserId())))
                .collect(Collectors.toList()));
    }

    @PostMapping
    @Operation(summary = "创建老人（仅基础信息）")
    public Result<ElderDTO> create(@RequestBody Elder elder) {
        Elder saved = elderService.createElder(elder);
        return Result.success(DtoConverter.convertToElderDTO(saved, userService.getUserById(saved.getUserId())));
    }
}
