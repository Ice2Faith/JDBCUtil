package com.ugex.savelar.Utils.MyBatis;
public interface IMyBatisDao<T,E>{
    E toDo(T dao,Object ... params);
}