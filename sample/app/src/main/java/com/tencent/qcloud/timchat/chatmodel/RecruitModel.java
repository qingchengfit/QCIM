package com.tencent.qcloud.timchat.chatmodel;

/**
 * Created by fb on 2017/6/14.
 */

public class RecruitModel {

  public String id;
  public String name;
  public String photo;
  public float max_salary; //薪水
  public float min_salary;
  public float max_height;
  public float min_height;

  public int max_work_year; //工作年限
  public int min_work_year;
  public int gender;

  public int min_age;  //年龄
  public int max_age;

  public String address;
  public String gym_name;

  public int deliveried;   // 1 表示已投递

}
