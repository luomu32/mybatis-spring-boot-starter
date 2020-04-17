package xyz.luomu32.mybatis.plugin;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.util.List;

public class ProxySqlSource implements SqlSource {
    private SqlSource target;
    private Configuration configuration;

    public ProxySqlSource(SqlSource target, Configuration configuration) {
        this.target = target;
        this.configuration = configuration;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {

        //TODO，这样不行，除了在where添加version条件之外，update 还需要更新version字段，

        BoundSql boundSql = target.getBoundSql(parameterObject);

        int pos = boundSql.getSql().indexOf("where");
        if (pos == -1) {
            return boundSql;
        } else {
            String[] sqls = boundSql.getSql().split("where");
            String sql = sqls[1] + ",version=version+1" + " where " + sqls[2] + " and version=?";

            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            parameterMappings.add(new ParameterMapping.Builder(configuration, "version", int.class).build());

            BoundSql newBoundSql = new BoundSql(configuration, sql, parameterMappings, boundSql.getParameterObject());
            return newBoundSql;
        }
    }
}
