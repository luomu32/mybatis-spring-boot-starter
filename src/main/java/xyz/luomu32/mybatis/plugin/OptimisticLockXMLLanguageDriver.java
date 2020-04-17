package xyz.luomu32.mybatis.plugin;

import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OptimisticLockXMLLanguageDriver extends XMLLanguageDriver {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptimisticLockXMLLanguageDriver.class);

    @Override
    public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {

        String name = script.getName();

        SqlSource sqlSource = super.createSqlSource(configuration, script, parameterType);

        //TODO 如何确定需要修改sql，加上版本号？需要找到Mapper对应的实体类
        if (shouldEnhance(parameterType) && (name.equals("update") || name.equals("delete"))) {
            LOGGER.debug("sql is update or delete enhance");
//            script.getNode().getChildNodes();

            //TODO 修改，如何生成一个SqlSource对象
            //一种方式修改XNode对象，简单一点。
//            System.out.println(script.getPath());
//            System.out.println(script.getStringBody());
//            System.out.println(script.getNode().getNodeName());


            sqlSource = new ProxySqlSource(sqlSource, configuration);
        }
        return sqlSource;
    }

    private boolean shouldEnhance(Class<?> parameterType) {


        return true;
    }
}
