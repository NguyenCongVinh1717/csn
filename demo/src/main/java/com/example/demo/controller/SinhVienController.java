package com.example.demo.controller;

import com.example.demo.repository.SinhVienRepository;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.SinhVien;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/sinhvien")
public class SinhVienController
{
    @Autowired
    private SinhVienRepository sinhVienRepository;

    @GetMapping
    public List<SinhVien> getAll() {
        return sinhVienRepository.findAll();
    }
}

