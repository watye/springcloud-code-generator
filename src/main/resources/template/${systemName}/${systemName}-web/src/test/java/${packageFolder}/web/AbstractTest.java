package ${package}.web;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ${package}.web.${moduleName}WebApplication;
/**
 * 抽象测试用例
 * date: 2019-03-21 10:58:30
 * 
 * @author Liuweiyao
 * @version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=${moduleName}WebApplication.class)
public abstract class AbstractTest{

}