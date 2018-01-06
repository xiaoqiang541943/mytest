package com.itheima.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.itheima.jopo.Book;

public class LuceneDaoImpl implements LuceneDao {

	@Override
	public List<Book> queryBookList() {
		//数据库连接
		Connection con =null;
		//预编译的sql执行对象
		PreparedStatement  preparedStatement = null;
		//结果集对象
		ResultSet resultSet = null;
		//图书列表
		List<Book> bList = new ArrayList<Book>();
		try {
			//加载数据库驱动
			Class.forName("com.mysql.jdbc.Driver");
			//创建数据库连接
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/lucene","root","root");
			//编写预编译sql语句
			String sql ="select*from book";
			//获取预编译执行语句对象
			preparedStatement = con.prepareStatement(sql);
			//获取结果集
			resultSet = preparedStatement.executeQuery();
			//解析遍历结果集获取数据
			while(resultSet.next()){
				Book book = new Book();
				book.setId(resultSet.getInt("id"));
				book.setName(resultSet.getString("name"));
				book.setPrice(resultSet.getFloat("price"));
				book.setPic(resultSet.getString("pic"));
				book.setDesc(resultSet.getString("description"));
				bList.add(book);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bList;
	}

}
