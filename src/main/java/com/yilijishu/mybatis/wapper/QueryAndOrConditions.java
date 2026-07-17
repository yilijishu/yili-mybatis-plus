package com.yilijishu.mybatis.wapper;

import com.yilijishu.mybatis.constant.Constant;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class QueryAndOrConditions<T> extends QueryConditions<T> {

    @Getter
    @Setter
    private List<QueryConditions> queryConditions;


    public QueryAndOrConditions() {
        super();
    }

    public QueryAndOrConditions(List<QueryConditions> queryConditions) {
        super();
        this.queryConditions = queryConditions;
    }

    public QueryAndOrConditions(SqlKey sqlKey, List<QueryConditions> queryConditions, String column, T... data) {
        super(sqlKey, column, data);
        this.queryConditions = queryConditions;
    }

    public QueryAndOrConditions(SqlKey sqlKey) {
        super();
        setSqlKey(sqlKey);
    }

    public QueryAndOrConditions(SqlKey sqlKey, List<QueryConditions> queryConditions) {
        super();
        setSqlKey(sqlKey);
        this.queryConditions = queryConditions;
    }

    public List<QueryConditions> getQueryConditions() {
        return queryConditions;
    }

    public QueryAndOrConditions<T> add(List<QueryConditions> queryConditions) {
        this.queryConditions = queryConditions;
        return this;
    }

    public String toSqlString(String param) {
        StringBuffer sbf = new StringBuffer();
        switch (getSqlKey()) {
            case AND:
            case HAVING:
            case OR: {
                forQuery(this, sbf, param);
                break;
            }
            default:
                break;

        }
        return sbf.toString();
    }


    private void forQuery(QueryConditions queryConditions, StringBuffer sbf, String param) {
        if (queryConditions != null) {
            if (queryConditions instanceof QueryAndOrConditions) {
                QueryAndOrConditions queryAndOrConditions = (QueryAndOrConditions) queryConditions;
                sbf.append(Constant.SPACE);
                sbf.append(queryAndOrConditions.getSqlKey().getSqlSegment());
                sbf.append(Constant.SPACE);
                List<QueryConditions> list = queryAndOrConditions.getQueryConditions();
                if (list != null && list.size() > 0) {
                    sbf.append("(");
                    int lastOr = -1;
                    for(int i=0;i<list.size();i++) {
                        QueryConditions e = list.get(i);
                        if(!(e instanceof QueryAndOrConditions)) {
                            if(i > 0 && (i-1 != lastOr)) {
                                sbf.append(" AND ");
                            }
                        } else {
                            lastOr = i;
                        }
                        forQuery(e, sbf, param.concat(".queryConditions[").concat(String.valueOf(i)).concat("]"));
                    }
                    sbf.append(")");
                }
            } else {
                sbf.append(queryConditions.toSqlString(param));
            }
        }
    }
}
