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
        if (name.equals("update") || name.equals("delete")) {
            LOGGER.debug("sql is update or delete enhance");
            script.getNode().getChildNodes();
//            String body = StringUtils.trimWhitespace(script.getStringBody());
//            if (null != body && !body.isEmpty()) {
//
//                String[] fragment = body.split(" ");
//                StringBuilder sb = new StringBuilder();
//                int fromPos = 0;
//                for (int i = 0; i < fragment.length; i++) {
//                    if (fragment[i].equals("from")) {
//                        for (int j = 0; j < i; j++) {
//                            sb.append(fragment[j]);
//                        }
//                        sb.append(",version=version+1");
//                        fromPos = i;
//                        break;
//                    }
//                }
//                for (int i = fromPos; i < fragment.length; i++) {
//                    sb.append(fragment[i]);
//                }
//                sb.append(" and version=#{version}");
//
//                AttrImpl node = new AttrImpl();
//                node.setNodeValue(sb.toString());
////                node.setValue();
//                script = script.newXNode(node);
//            }
        }
        return super.createSqlSource(configuration, script, parameterType);
    }

}
