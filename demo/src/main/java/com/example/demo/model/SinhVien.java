package com.example.demo.model;

import jakarta.persistence.Entity;
@Entity
public class SinhVien {
    private String maSV;
    private String tenSV;
    private double diemTK;

    public SinhVien() {
    }

    public SinhVien(String maSV, String tenSV, double diemTK) {
        this.maSV = maSV;
        this.tenSV = tenSV;
        this.diemTK = diemTK;
    }

    public String getMaSV() {
        return maSV;
    }

    public void setMaSV(String maSV) {
        this.maSV = maSV;
    }

    public String getTenSV() {
        return tenSV;
    }

    public void setTenSV(String tenSV) {
        this.tenSV = tenSV;
    }

    public double getDiemTK() {
        return diemTK;
    }

    public void setDiemTK(double diemTK) {
        this.diemTK = diemTK;
    }
}
