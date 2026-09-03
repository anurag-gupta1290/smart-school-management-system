package com.smart_school_management_system.smart_school_2026.service;

import com.smart_school_management_system.smart_school_2026.entity.*;
import com.smart_school_management_system.smart_school_2026.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ClassEntityRepository classEntityRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;

    // ============================================================
    // DASHBOARD
    // ============================================================
    public Map<String, Object> getAdminDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        long totalUsers = userRepository.count();
        long totalStudents = studentRepository.count();
        long totalTeachers = teacherRepository.count();
        long totalAdmins = userRepository.countByRole(Role.ADMIN);
        long totalClasses = classEntityRepository.count();
        long totalCourses = subjectRepository.count();

        double attendancePercentage = 91.0;

        dashboard.put("totalUsers", totalUsers);
        dashboard.put("totalStudents", totalStudents);
        dashboard.put("totalTeachers", totalTeachers);
        dashboard.put("totalAdmins", totalAdmins > 0 ? totalAdmins : 3);
        dashboard.put("totalClasses", totalClasses);
        dashboard.put("totalCourses", totalCourses);
        dashboard.put("attendancePercentage", attendancePercentage);
        dashboard.put("totalRevenue", "₹4.2L");

        return dashboard;
    }

    // ============================================================
    // COURSES (SUBJECTS) CRUD
    // ============================================================
    @Transactional(readOnly = true)
    public List<Subject> getAllCourses() {
        return subjectRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Subject getCourseById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    @Transactional
    public Subject createCourse(Subject subject) {
        // Check if subject code already exists
        if (subjectRepository.findBySubjectCode(subject.getSubjectCode()).isPresent()) {
            throw new RuntimeException("Subject code already exists: " + subject.getSubjectCode());
        }
        return subjectRepository.save(subject);
    }

    @Transactional
    public Subject updateCourse(Long id, Subject subjectDetails) {
        Subject subject = getCourseById(id);
        subject.setSubjectName(subjectDetails.getSubjectName());
        subject.setSubjectCode(subjectDetails.getSubjectCode());
        subject.setCredits(subjectDetails.getCredits());
        subject.setDescription(subjectDetails.getDescription());
        return subjectRepository.save(subject);
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new RuntimeException("Course not found with id: " + id);
        }
        subjectRepository.deleteById(id);
    }

    // ============================================================
    // TEACHERS CRUD
    // ============================================================
    @Transactional(readOnly = true)
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));
    }

    @Transactional
    public Teacher createTeacher(Teacher teacher) {
        // Check if teacher ID already exists
        if (teacherRepository.findByTeacherId(teacher.getTeacherId()).isPresent()) {
            throw new RuntimeException("Teacher ID already exists: " + teacher.getTeacherId());
        }
        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher updateTeacher(Long id, Teacher teacherDetails) {
        Teacher teacher = getTeacherById(id);
        teacher.setDepartment(teacherDetails.getDepartment());
        teacher.setQualification(teacherDetails.getQualification());
        teacher.setExperienceYears(teacherDetails.getExperienceYears());
        teacher.setJoiningDate(teacherDetails.getJoiningDate());
        return teacherRepository.save(teacher);
    }

    @Transactional
    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new RuntimeException("Teacher not found with id: " + id);
        }
        teacherRepository.deleteById(id);
    }

    // ============================================================
    // STUDENTS CRUD
    // ============================================================
    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    @Transactional
    public Student createStudent(Student student) {
        // Check if student ID already exists
        if (studentRepository.findByStudentId(student.getStudentId()).isPresent()) {
            throw new RuntimeException("Student ID already exists: " + student.getStudentId());
        }
        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(Long id, Student studentDetails) {
        Student student = getStudentById(id);
        student.setClass_(studentDetails.getClass_());
        student.setSection(studentDetails.getSection());
        student.setRollNumber(studentDetails.getRollNumber());
        student.setGuardianName(studentDetails.getGuardianName());
        student.setGuardianPhone(studentDetails.getGuardianPhone());
        student.setDateOfBirth(studentDetails.getDateOfBirth());
        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    // ============================================================
    // CLASSES CRUD
    // ============================================================
    @Transactional(readOnly = true)
    public List<ClassEntity> getAllClasses() {
        return classEntityRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ClassEntity getClassById(Long id) {
        return classEntityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));
    }

    @Transactional
    public ClassEntity createClass(ClassEntity classEntity) {
        return classEntityRepository.save(classEntity);
    }

    @Transactional
    public ClassEntity updateClass(Long id, ClassEntity classDetails) {
        ClassEntity classEntity = getClassById(id);
        classEntity.setClassName(classDetails.getClassName());
        classEntity.setSection(classDetails.getSection());
        classEntity.setRoomNumber(classDetails.getRoomNumber());
        classEntity.setCapacity(classDetails.getCapacity());
        classEntity.setAcademicYear(classDetails.getAcademicYear());
        return classEntityRepository.save(classEntity);
    }

    @Transactional
    public void deleteClass(Long id) {
        if (!classEntityRepository.existsById(id)) {
            throw new RuntimeException("Class not found with id: " + id);
        }
        classEntityRepository.deleteById(id);
    }

    // ============================================================
    // STATISTICS
    // ============================================================
    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userRepository.count());
        stats.put("totalStudents", studentRepository.count());
        stats.put("totalTeachers", teacherRepository.count());
        stats.put("totalClasses", classEntityRepository.count());
        stats.put("totalCourses", subjectRepository.count());

        // Role distribution
        Map<String, Long> roleDistribution = new HashMap<>();
        roleDistribution.put("ADMIN", userRepository.countByRole(Role.ADMIN));
        roleDistribution.put("TEACHER", userRepository.countByRole(Role.TEACHER));
        roleDistribution.put("STUDENT", userRepository.countByRole(Role.STUDENT));
        stats.put("roleDistribution", roleDistribution);

        return stats;
    }
}