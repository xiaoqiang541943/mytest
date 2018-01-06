package com.itheima.dao;

import java.util.List;

import com.itheima.jopo.Book;

public interface LuceneDao {
	
	List<Book> queryBookList(); 
}
