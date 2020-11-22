package com.ugex.savelar.Demos.mybatisDemo.dao;

import com.ugex.savelar.Demos.mybatisDemo.model.Student;

import java.util.List;

public interface StudentDao {
    List<Student> findAll();
    Student findById(int id);
    List<Student> findByName(String name);
    int insertOne(Student stu);
    int updateOne(Student stu);
    int deleteOne(Student stu);

    List<Student> findAllIfAgeName(Student stu);
    List<Student> findAllWhereAgeName(Student stu);
    List<Student> findAllChooseAgeName(Student stu);

    List<Student> findAllEachArrayIds(int[] ids);
    List<Student> findAllEachCollectionBases(List<Integer> ids);
    List<Student> findAllEachCollectionObjects(List<Student> ids);

    List<Student> sqlPartUseTest(int[] ids);
}
