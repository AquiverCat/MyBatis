package com.genius.mybatis;

import com.genius.mybatis.dto.GoodsDTO;
import com.genius.mybatis.entity.Goods;
import com.genius.mybatis.utils.MyBatisUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//JUNIT单元测试类
public class MyBatisTestor {
    @Test
    public void testSqlSessionFactory() throws IOException {
        //利用Reader加载classpath下的mybatis-config.xml核心配置文件
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        //初始化SqlSessionFactory对象，同时解析mybatis-config.xml文件
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        System.out.println("SessionFactory加载成功");
        SqlSession sqlSession = null;
        try {
            //创建SqlSession对象，SqlSession是JDBC的扩展类，用于与数据库交互
            sqlSession = sqlSessionFactory.openSession();
            //创建数据库连接（测试用）
            Connection connection = sqlSession.getConnection();
            System.out.println(connection);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(sqlSession != null){
                //如果type="POOLED"，代表使用连接池，close则是将连接回收到连接池中
                //如果type="UNPOOLED"，代表直连，close则会调用Connection.close()方法关闭
                sqlSession.close();
            }
        }
    }
    @Test
    public void testMyBatisUtils() throws Exception{
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            Connection connection = sqlSession.getConnection();
            System.out.println(connection);
        }catch (Exception e){
            throw e;
        }finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testSelectAll() throws Exception {
        SqlSession session =null;
        try{
            session = MyBatisUtils.openSession();
            List<Goods> list = session.selectList("goods.selectAll");
            for (Goods goods:list){
                System.out.println(goods.getTitle());
            }
        }catch (Exception e){
            throw e;
        }finally {
            MyBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testSelectById() throws Exception {
        SqlSession session =null;
        try{
            session = MyBatisUtils.openSession();
            Goods goods = session.selectOne("goods.selectById",1602);
            System.out.println(goods.getTitle());
        }catch (Exception e){
            throw e;
        }finally {
            MyBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testSelectByPriceRange() throws Exception{
        SqlSession session =null;
        try{
            session = MyBatisUtils.openSession();
            Map param = new HashMap();
            param.put("min",100);
            param.put("max",500);
            param.put("limit",10);
            List<Goods> list = session.selectList("selectByPriceRange",param);
            for (Goods goods:list){
                System.out.println(goods.getTitle()+":"+goods.getCurrentPrice());
            }
        }catch (Exception e){
            throw e;
        }finally {
            MyBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testSelectGoodsMap() throws Exception {
        SqlSession session =null;
        try{
            session = MyBatisUtils.openSession();
            List<Map> list= session.selectList("goods.selectGoodsMap");
            for(Map map:list){
                System.out.println(map);
            }
        }catch (Exception e){
            throw e;
        }finally {
            MyBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testSelectGoodsDTO() throws Exception {
        SqlSession session =null;
        try{
            session = MyBatisUtils.openSession();
            List<GoodsDTO> list = session.selectList("goods.selectGoodsDTO");
            for(GoodsDTO goodsDTO:list){
                System.out.println(goodsDTO.getGoods().getTitle());
            }
        }catch (Exception e){
            throw e;
        }finally {
            MyBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testInsert() throws Exception {
        SqlSession session =null;
        try{
            session = MyBatisUtils.openSession();
            Goods goods = new Goods();
            goods.setTitle("测试商品");
            goods.setSubTitle("测试子标题");
            goods.setOriginalCost(200f);
            goods.setCurrentPrice(100f);
            goods.setDiscount(0.5f);
            goods.setIsFreeDelivery(1);
            goods.setCategoryId(43);
            //insert返回值代表本次成功插入的记录总数
            int num = session.insert("goods.insert",goods);
            //提交事务数据
            session.commit();
            System.out.println(goods.getGoodsId());
        }catch (Exception e){
            if(session != null){
                session.rollback();
            }
            throw e;
        }finally {
            MyBatisUtils.closeSession(session);
        }
    }
}
