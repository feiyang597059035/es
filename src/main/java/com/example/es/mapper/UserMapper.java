package com.example.es.mapper;


import com.example.es.model.User;
import org.apache.ibatis.annotations.Mapper;



@Mapper
public abstract class UserMapper {
    public abstract int deleteByPrimaryKey(Integer id);

    public abstract int insert(User record);

    public abstract int insertSelective(User record);

    public abstract User selectByPrimaryKey(Integer id);

    public abstract int updateByPrimaryKeySelective(User record);

    public abstract int updateByPrimaryKey(User record);

    /* User selectUserInfoById(Integer id,String name);
    
    User selectUserInfoById(Map<String, Object> map);*/

    /* String selectUserInfoById(Integer id,String name);*/

    /*  Map<String, Object> selectUserInfoById(Integer id,String name);*/
  /*  @MapKey("id")
    public abstract Map<Integer, User> selectUserInfoById(Integer id, String name);*/
}