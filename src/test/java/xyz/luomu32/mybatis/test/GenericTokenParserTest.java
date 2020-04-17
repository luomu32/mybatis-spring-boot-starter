package xyz.luomu32.mybatis.test;

import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.parsing.TokenHandler;
import org.junit.Test;

public class GenericTokenParserTest {

//    @Test
    public void test() {
        String s = "\n insert into t_user(username)values(#{username})\n";

        GenericTokenParser parser = new GenericTokenParser("#{", "}", new TokenHandler() {
            @Override
            public String handleToken(String content) {
                System.out.println(content);
                return null;
            }
        });

        String result = parser.parse(s);
        System.out.println(result);
    }

//    @Test
    public void spTest() {
        String s = "update t_user set username=? where id=?";
        String[] ss = s.split("where");
        System.out.println(ss[2]);
    }

}
