package com.itheima.lucene;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.itheima.dao.LuceneDao;
import com.itheima.dao.LuceneDaoImpl;
import com.itheima.jopo.Book;

public class CreateIndexText {

	@Test
	public void createIndex() throws Exception{
		//获取数据
		LuceneDao luceneDao = new LuceneDaoImpl();
		List<Book> bList = luceneDao.queryBookList();
		//创建解析器（分词器）
		Analyzer analyzer = new IKAnalyzer();
		//配置indexWriterConfig索引|写入的配置信息
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3,analyzer);
		//创建索引库对象，声明索引库的位置
		Directory directory = FSDirectory.open(new File("e:\\term"));
		//创建indexWriter索引写入对象
		IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
		//把数据写入到索引库，通过indexWriter添加文档对象
		for (Book book : bList) {
			//创建文档对象
			TextField namefield = new TextField("name",book.getName(),Store.YES);
			Document doc = new Document();
			if(book.getId()==4){
				namefield.setBoost(10.f);
			}
			//不要使用id进行查询但是页面需要Id这个数据 所以需要写去到文档对象中 N N Y
			doc.add(new StoredField("id", ""+book.getId()));
			//需要根据客户的名字搜索索引库，Y Y Y
			doc.add(new TextField("name",book.getName(),Store.YES));
			//需要队价格进行分词和搜索，并且需要存储在文档对象中 Y Y Y 
			doc.add(namefield);
			//不使用图片的名称进行查询，所以不用分词和索引，但需要存储  N N Y
			doc.add(new StoredField("pic",book.getPic()));
			//要分词，要索引，但是描述信息量过大，不存储是不再索引域中记录，N N Y
			doc.add(new TextField("desc",book.getDesc(),Store.NO));
			indexWriter.addDocument(doc);
		}
		//释放资源
		indexWriter.close();
	}
	
}
