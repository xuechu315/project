package com.example.elderlycare.config;

import com.example.elderlycare.entity.*;
import com.example.elderlycare.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 数据库初始化器 - 在数据库为空时自动插入测试数据
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ElderRepository elderRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private ElderDoctorRelationRepository elderDoctorRelationRepository;

    @Autowired
    private HealthDataRepository healthDataRepository;

    @Autowired
    private AbnormalEventRepository abnormalEventRepository;

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private MedicationRecordRepository medicationRecordRepository;

    @Autowired
    private ElderFamilyMemberRepository elderFamilyMemberRepository;

    @Autowired
    private SOSRecordRepository sosRecordRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private ContactRecordRepository contactRecordRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // 已有数据，不重复初始化
        }

        String encodedPassword = passwordEncoder.encode("123456");
        LocalDateTime now = LocalDateTime.now();

        // ==================== 用户数据 ====================
        User admin = createUser("admin", encodedPassword, User.UserType.admin, "系统管理员", 35, "男", null, null, null, "13800138000");
        User doctor1 = createUser("doctor1", encodedPassword, User.UserType.doctor, "王建国", 45, "男", null, null, null, "13800138001");
        User doctor2 = createUser("doctor2", encodedPassword, User.UserType.doctor, "李医生", 38, "女", null, null, null, "13800138002");

        User elderly1 = createUser("elderly1", encodedPassword, User.UserType.elder, "张大爷", 75, "男", "O+", 172f, 68f, "13900000001");
        User elderly2 = createUser("elderly2", encodedPassword, User.UserType.elder, "李芳", 68, "女", "A+", 158f, 55f, "13900000002");
        User elderly3 = createUser("elderly3", encodedPassword, User.UserType.elder, "王林", 72, "男", "B+", 175f, 72f, "13900000003");
        User elderly4 = createUser("elderly4", encodedPassword, User.UserType.elder, "陈秀莲", 65, "女", "AB+", 155f, 52f, "13900000004");
        User elderly5 = createUser("elderly5", encodedPassword, User.UserType.elder, "赵德明", 78, "男", "O-", 168f, 65f, "13900000005");
        User elderly6 = createUser("elderly6", encodedPassword, User.UserType.elder, "孙桂英", 71, "女", "A-", 160f, 58f, "13900000006");

        User family1 = createUser("family1", encodedPassword, User.UserType.family, "张小明", 42, "男", null, null, null, "13700000001");
        User family2 = createUser("family2", encodedPassword, User.UserType.family, "李小红", 38, "女", null, null, null, "13700000002");
        User family3 = createUser("family3", encodedPassword, User.UserType.family, "王小强", 35, "男", null, null, null, "13700000003");

        // ==================== 医生辅助表 ====================
        createDoctor(doctor1.getId(), "王建国", "13800138001", "全科");
        createDoctor(doctor2.getId(), "李医生", "13800138002", "内科");

        // ==================== 老人辅助表 ====================
        createElder(elderly1.getId(), 75, "男", "O+", 172f, 68f);
        createElder(elderly2.getId(), 68, "女", "A+", 158f, 55f);
        createElder(elderly3.getId(), 72, "男", "B+", 175f, 72f);
        createElder(elderly4.getId(), 65, "女", "AB+", 155f, 52f);
        createElder(elderly5.getId(), 78, "男", "O-", 168f, 65f);
        createElder(elderly6.getId(), 71, "女", "A-", 160f, 58f);

        // ==================== 家属辅助表（管理端） ====================
        createFamilyMember(family1.getId(), 1, "张小明", "儿子", "13700000001");
        createFamilyMember(family2.getId(), 2, "李小红", "女儿", "13700000002");
        createFamilyMember(family3.getId(), 3, "王小强", "孙子", "13700000003");

        // ==================== 老人-医生关联 ====================
        createElderDoctorRelation(1, 1);
        createElderDoctorRelation(2, 1);
        createElderDoctorRelation(3, 1);
        createElderDoctorRelation(4, 1);
        createElderDoctorRelation(5, 1);
        createElderDoctorRelation(6, 1);

        // ==================== 健康数据 ====================
        createHealthData(elderly1.getId(), 118, 185, 110, 1250, null);
        createHealthData(elderly2.getId(), 92, 142, 88, 3102, null);
        createHealthData(elderly3.getId(), 78, 125, 82, 4521, null);
        createHealthData(elderly4.getId(), 72, 118, 78, 5890, null);
        createHealthData(elderly5.getId(), 68, 130, 85, 2340, null);
        createHealthData(elderly6.getId(), 85, 135, 88, 4156, null);

        // ==================== 异常事件 ====================
        createAbnormalEvent(elderly1.getId(), AbnormalEvent.EventType.跌倒, AbnormalEvent.Severity.紧急, 0.968f, "行为分析Agent");
        createAbnormalEvent(elderly1.getId(), AbnormalEvent.EventType.血压异常, AbnormalEvent.Severity.紧急, 0.985f, "健康监测Agent");
        createAbnormalEvent(elderly2.getId(), AbnormalEvent.EventType.血压异常, AbnormalEvent.Severity.警告, 0.892f, "健康监测Agent");
        createAbnormalEvent(elderly6.getId(), AbnormalEvent.EventType.心率异常, AbnormalEvent.Severity.注意, 0.856f, "健康监测Agent");
        createAbnormalEvent(elderly3.getId(), AbnormalEvent.EventType.心率异常, AbnormalEvent.Severity.正常, 0.723f, "健康监测Agent");

        // ==================== 药品表 ====================
        Medication med1 = createMedication(elderly1.getId(), "硝苯地平缓释片", "降压药", "2片", "每日1次", "08:00");
        Medication med2 = createMedication(elderly1.getId(), "阿司匹林肠溶片", "抗血小板聚集", "1片", "每日1次", "09:00");
        createMedication(elderly2.getId(), "硝苯地平缓释片", "降压药", "1片", "每日1次", "08:00");
        Medication med4 = createMedication(elderly6.getId(), "二甲双胍", "降糖药", "1片", "每日2次", "08:00,20:00");
        Medication med5 = createMedication(elderly1.getId(), "丹参滴丸", "活血化瘀", "10丸", "每日3次", "08:00,12:00,18:00");

        // ==================== 用药记录 ====================
        createMedicationRecord(med1.getId(), LocalDateTime.of(2026, 4, 28, 8, 0), MedicationRecord.MedicationStatus.已服用);
        createMedicationRecord(med2.getId(), LocalDateTime.of(2026, 4, 28, 9, 0), MedicationRecord.MedicationStatus.已服用);
        createMedicationRecord(med4.getId(), LocalDateTime.of(2026, 4, 28, 8, 0), MedicationRecord.MedicationStatus.已服用);
        createMedicationRecord(med4.getId(), LocalDateTime.of(2026, 4, 28, 20, 0), MedicationRecord.MedicationStatus.漏服);
        createMedicationRecord(med5.getId(), LocalDateTime.of(2026, 4, 28, 8, 0), MedicationRecord.MedicationStatus.已服用);

        // ==================== 家属绑定（移动端） ====================
        createFamilyBind(elderly1.getId(), family1.getId(), "儿子", "张小明", "13700000001");
        createFamilyBind(elderly2.getId(), family2.getId(), "女儿", "李小红", "13700000002");
        createFamilyBind(elderly3.getId(), family3.getId(), "孙子", "王小强", "13700000003");

        // ==================== SOS记录 ====================
        createSosRecord(elderly1.getId(), "康乐园小区 A3-102室", SOSRecord.SOSStatus.PENDING);

        // ==================== 咨询记录 ====================
        createConsultation(elderly2.getId(), "请问我妈妈血压偏高需要注意什么？",
                "建议减少盐分摄入，定期监测血压，如果持续偏高请及时就医。", Consultation.ConsultationType.文本);
        createConsultation(elderly6.getId(), "最近血糖有点高，饮食需要怎么调整？", null, Consultation.ConsultationType.文本);

        // ==================== 联系记录 ====================
        createContactRecord(elderly1.getId(), family1.getId(), "emergency", "delivered");
        createContactRecord(elderly2.getId(), family2.getId(), "normal", "read");

        System.out.println("========================================");
        System.out.println("【DataInitializer】测试数据初始化完成");
        System.out.println("账号 elderly1~elderly6 (老人端) 密码: 123456");
        System.out.println("账号 family1~family3   (家属端) 密码: 123456");
        System.out.println("========================================");
    }

    // ==================== 私有辅助方法 ====================

    private User createUser(String username, String password, User.UserType userType, String name,
                            Integer age, String gender, String bloodType, Float height, Float weight, String phone) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setUserType(userType);
        user.setName(name);
        user.setAge(age);
        user.setGender(gender);
        user.setBloodType(bloodType);
        user.setHeight(height);
        user.setWeight(weight);
        user.setPhone(phone);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private void createDoctor(Integer userId, String name, String phone, String department) {
        Doctor d = new Doctor();
        d.setUserId(userId);
        d.setName(name);
        d.setPhone(phone);
        d.setDepartment(department);
        doctorRepository.save(d);
    }

    private void createElder(Integer userId, Integer age, String gender, String bloodType, Float height, Float weight) {
        Elder e = new Elder();
        e.setUserId(userId);
        e.setAge(age);
        e.setGender(gender);
        e.setBloodType(bloodType);
        e.setHeight(height);
        e.setWeight(weight);
        elderRepository.save(e);
    }

    private void createFamilyMember(Integer userId, Integer elderId, String name, String relationship, String phone) {
        ElderFamilyMember fm = new ElderFamilyMember();
        fm.setUserId(userId);
        fm.setElderId(elderId);
        fm.setName(name);
        fm.setRelationship(relationship);
        fm.setPhone(phone);
        elderFamilyMemberRepository.save(fm);
    }

    private void createElderDoctorRelation(Integer elderId, Integer doctorId) {
        ElderDoctorRelation r = new ElderDoctorRelation();
        r.setElderId(elderId);
        r.setDoctorId(doctorId);
        elderDoctorRelationRepository.save(r);
    }

    private void createHealthData(Integer userId, Integer heartRate, Integer systolic, Integer diastolic, Integer steps, LocalDateTime recordedAt) {
        HealthData hd = new HealthData();
        hd.setUserId(userId);
        hd.setHeartRate(heartRate);
        hd.setSystolicPressure(systolic);
        hd.setDiastolicPressure(diastolic);
        hd.setSteps(steps);
        if (recordedAt != null) hd.setRecordedAt(recordedAt);
        healthDataRepository.save(hd);
    }

    private void createAbnormalEvent(Integer userId, AbnormalEvent.EventType type, AbnormalEvent.Severity severity, Float confidence, String detectedBy) {
        AbnormalEvent ae = new AbnormalEvent();
        ae.setUserId(userId);
        ae.setType(type);
        ae.setSeverity(severity);
        ae.setConfidence(confidence);
        ae.setDetectedBy(detectedBy);
        abnormalEventRepository.save(ae);
    }

    private Medication createMedication(Integer userId, String name, String description, String dosage, String frequency, String time) {
        Medication m = new Medication();
        m.setUserId(userId);
        m.setName(name);
        m.setDescription(description);
        m.setDosage(dosage);
        m.setFrequency(frequency);
        m.setTime(time);
        return medicationRepository.save(m);
    }

    private void createMedicationRecord(Integer medicationId, LocalDateTime takenAt, MedicationRecord.MedicationStatus status) {
        MedicationRecord mr = new MedicationRecord();
        mr.setMedicationId(medicationId);
        mr.setTakenAt(takenAt);
        mr.setStatus(status);
        medicationRecordRepository.save(mr);
    }

    private void createFamilyBind(Integer userId, Integer familyId, String relationship, String name, String phone) {
        FamilyMember fb = new FamilyMember();
        fb.setUserId(userId);
        fb.setFamilyId(familyId);
        fb.setRelationship(relationship);
        fb.setName(name);
        fb.setPhone(phone);
        familyMemberRepository.save(fb);
    }

    private void createSosRecord(Integer userId, String location, SOSRecord.SOSStatus status) {
        SOSRecord sr = new SOSRecord();
        sr.setUserId(userId);
        sr.setLocation(location);
        sr.setStatus(status);
        sosRecordRepository.save(sr);
    }

    private void createConsultation(Integer userId, String message, String response, Consultation.ConsultationType type) {
        Consultation c = new Consultation();
        c.setUserId(userId);
        c.setMessage(message);
        c.setResponse(response);
        c.setType(type);
        consultationRepository.save(c);
    }

    private void createContactRecord(Integer userId, Integer familyMemberId, String type, String status) {
        ContactRecord cr = new ContactRecord();
        cr.setUserId(userId);
        cr.setFamilyMemberId(familyMemberId);
        cr.setType(type);
        cr.setStatus(status);
        contactRecordRepository.save(cr);
    }
}
