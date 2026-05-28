package com.elderly.care.controller;

import com.elderly.care.dto.DoctorDTO;
import com.elderly.care.dto.Result;
import com.elderly.care.entity.Doctor;
import com.elderly.care.service.DoctorService;
import com.elderly.care.utils.DtoConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "医生管理", description = "医生信息的增删改查接口")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    @Operation(summary = "获取所有医生列表")
    public Result<List<DoctorDTO>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        return Result.success(DtoConverter.convertToDoctorDTOList(doctors));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取医生信息")
    public Result<DoctorDTO> getDoctorById(@PathVariable Integer id) {
        return doctorService.getDoctorById(id)
                .map(doctor -> Result.success(DtoConverter.convertToDoctorDTO(doctor)))
                .orElse(Result.notFound("医生不存在"));
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "按科室查询医生")
    public Result<List<DoctorDTO>> getDoctorsByDepartment(@PathVariable String department) {
        List<Doctor> doctors = doctorService.getDoctorsByDepartment(department);
        return Result.success(DtoConverter.convertToDoctorDTOList(doctors));
    }

    @GetMapping("/search")
    @Operation(summary = "按姓名搜索医生")
    public Result<List<DoctorDTO>> searchDoctors(@RequestParam String name) {
        List<Doctor> doctors = doctorService.searchDoctorsByName(name);
        return Result.success(DtoConverter.convertToDoctorDTOList(doctors));
    }

    @PostMapping
    @Operation(summary = "创建医生")
    public Result<DoctorDTO> createDoctor(@RequestBody Doctor doctor) {
        Doctor saved = doctorService.createDoctor(doctor);
        return Result.success(DtoConverter.convertToDoctorDTO(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新医生信息")
    public Result<DoctorDTO> updateDoctor(@PathVariable Integer id, @RequestBody Doctor doctor) {
        Doctor updated = doctorService.updateDoctor(id, doctor);
        return Result.success(DtoConverter.convertToDoctorDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除医生")
    public Result<Void> deleteDoctor(@PathVariable Integer id) {
        doctorService.deleteDoctor(id);
        return Result.success();
    }
}
