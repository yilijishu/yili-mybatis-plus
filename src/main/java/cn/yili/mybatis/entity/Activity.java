package cn.yili.mybatis.entity;

import cn.yili.mybatis.ann.Table;
import lombok.Data;

@Table
@Data
public class Activity extends BaseEntity{

    private String name;

    private String type;

    public static void main(String[] args) {

    }
}
