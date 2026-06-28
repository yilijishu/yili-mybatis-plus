package com.yilijishu.mybatis.wapper;

import com.yilijishu.mybatis.iter.BaseBeanInterface;

import java.util.List;

public interface QueryInterface<Entity extends BaseBeanInterface> {

    String getSql();

    List<String> getSelect();

    default String toSql(Entity entity) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" SELECT ");
        if (getSelect() != null && getSelect().size() > 0) {
            for (int i = 0; i < getSelect().size(); i++) {
                if (i > 0) {
                    stringBuffer.append(", ");
                }
                stringBuffer.append(getSelect().get(i));
            }
        } else {
            stringBuffer.append(entity.baseGenColumnNames());
        }
        stringBuffer.append(" FROM ");
        stringBuffer.append(entity.baseGenTable());
        stringBuffer.append(getSql());
        return stringBuffer.toString();
    }
}
