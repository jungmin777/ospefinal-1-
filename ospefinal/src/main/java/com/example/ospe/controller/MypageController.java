package com.example.ospe.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ospe.user.dto.Doctor;
import com.example.ospe.user.dto.User;
import com.example.ospe.user.service.DoctorService;
import com.example.ospe.user.service.UserService;

@RestController
@RequestMapping("/api/user/profile")
public class MypageController {

    @Autowired
    private UserService userService;
    @Autowired
    private DoctorService doctorService;
    
    

 // URL 경로에서 username을 추출할거임
    @GetMapping("/{username}")
    public User getUserProfile(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    
    
    // 환자 정보 수정
    @PutMapping
    public User updateUserProfile(@RequestBody User updatedUser) {
        return userService.updateUserProfile(updatedUser);
    }

    // 환자 정보 삭제
    @DeleteMapping("/{username}")
    public void deleteUserProfile(@PathVariable String username) {
        userService.deleteUser(username);
    }
    
    
    
    
    // 의사 정보수정
    @PutMapping("/doctor")
    public Doctor updateDoctorProfile(@RequestBody Doctor updatedDoctor) {
        return doctorService.updateDoctorProfile(updatedDoctor);
    }
    
    // 의사 정보삭제
    @DeleteMapping("/doctor/{username}")
    public void deleteDoctorProfile(@PathVariable String username) {
        doctorService.deleteDoctor(username);
    }
    
    
    
    
}
