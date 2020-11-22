package com.ugex.savelar.Utils.DbUtil;

/**
 * 分页数据存放类
 * count：符合分页的数据的总量
 * index:当前数据的页面索引
 * limit:一页数据的最大显示数据量
 * data:数据的存放，根据你的具体类型而定的一个泛型
 * @param <T>
 */
public class DBPageData<T> {
    int count;
    int index;
    int limit;
    T data;
    public DBPageData(){

    }

    public DBPageData(int index, int limit,int count,  T data) {
        this.index = index;
        this.limit = limit;
        this.count = count;
        this.data = data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
