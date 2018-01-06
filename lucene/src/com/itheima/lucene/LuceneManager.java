package com.itheima.lucene;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneManager {
	public IndexWriter getIndexWriter() throws Exception{
		//创建分词器
		Analyzer analyzer = new IKAnalyzer();
		//创建Directory对象制定索引库的位置
		Directory directory = FSDirectory.open(new File("e:\\term"));
		//创建IndexWriterConfig配置对象，配置Lucene相关信息
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST, analyzer);
		//创建索引写入对象
		return new IndexWriter(directory, indexWriterConfig);
	}
	//全部删除请慎用，close关闭资源就会提交commit
	@Test
	public void testIndexDelete() throws Exception {
		//创建分词器
		Analyzer analyzer = new IKAnalyzer();
		//创建Directory对象制定索引库的位置
		Directory directory = FSDirectory.open(new File("e:\\term"));
		//创建IndexWriterConfig配置对象，配置Lucene相关信息
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST, analyzer);
		//创建索引写入对象
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		//全部删除
		indexWriter.deleteAll();
		indexWriter.close();
	}
	//删除制定索引（根据term项，满足条件的索引全部删除）
	@Test
	public void testIndexDeleteByTerm() throws Exception{
		IndexWriter indexWriter = this.getIndexWriter();
		//根据term删除数据库name:java
		indexWriter.deleteDocuments(new Term("name","java"));
		//释放资源
		indexWriter.close();
	}
	//修改索引：更新索引是先删除后添加 跟修改一概一样
	@Test
	public void testIndexUpdate() throws Exception {
		//创建写入对象
		IndexWriter indexWriter = this.getIndexWriter();
		//创建document对象
		Document doc = new Document();
		//添加索引信息
		doc.add(new StoredField("ID", "1002"));
		doc.add(new TextField("NAME", "测试",Store.YES));
		doc.add(new TextField("Context","测试内容",Store.YES));
		//执行更新
		indexWriter.updateDocument(new Term("name","mybatis"), doc);
		indexWriter.close();
	}
	//查询的两种方式：通过精确匹配进行查询
	@Test
	public void testQueryByTerm() throws Exception {
		//创建查询对象
		Query query = new TermQuery(new Term("name","lucene"));
		//索引库，索引库位置
		Directory directory = FSDirectory.open(new File("e:\\term"));
		//创建读入搜索对象
		IndexReader indexReader = DirectoryReader.open(directory);
		//创建搜索对象
		IndexSearcher searcher = new IndexSearcher(indexReader);
		//执行搜索
		TopDocs topDocs = searcher.search(query, 8);
		System.out.println(topDocs.totalHits);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docID = scoreDoc.doc;
			Document doc = searcher.doc(docID);
			System.out.println("文档的ID："+docID);
			System.out.println("ID:"+doc.get("id"));
			System.out.println("名称:"+doc.get("name"));
			System.out.println("价格:"+doc.get("price"));
			System.out.println("图片:"+doc.get("pic"));
			System.out.println("描述:"+doc.get("desc"));
		}
	}
	//获取IndexSearcher对象
	public IndexSearcher getIndexSearcher() throws Exception{
		Directory directory =FSDirectory.open(new File("e:\\term"));
		IndexReader indexReader = DirectoryReader.open(directory);
		return new IndexSearcher(indexReader);
	}
	//解析查询  queryParser
	@Test
	public void testQueryParser() throws Exception {
		//获取indexSearcher对象
		IndexSearcher indexSearcher = this.getIndexSearcher();
		//创建query对象，通过解析查询获取查询对象
		QueryParser queryParser = new QueryParser("name",new IKAnalyzer());
//		Query query = queryParser.parse("+desc:lucene +name:apache");
		Query query = queryParser.parse("desc:java AND lucene");
		System.out.println(query);
		//执行查询
		printResult(indexSearcher,query);
	}
	//打印结果
	private void printResult(IndexSearcher indexSearcher, Query query) throws Exception {
		TopDocs topDocs = indexSearcher.search(query, 8);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docId = scoreDoc.doc;
			Document doc = indexSearcher.doc(docId);
			System.out.println("文档的Id:"+docId);
			System.out.println("ID:"+doc.get("id"));
			System.out.println("name:"+doc.get("name"));
			System.out.println("price:"+doc.get("price"));
			System.out.println("pic:"+doc.get("pic"));
			System.out.println("desc:"+doc.get("desc"));
		}
		indexSearcher.getIndexReader().close();
	}
	//指定数字范围查询NumericRangeQuery
	@Test
	public void testNumericRangeQuery() throws Exception {
		IndexSearcher indexSearcher = this.getIndexSearcher();
		Query query = NumericRangeQuery.newFloatRange("price", 0f, 70f, false, true);
		System.out.println(query);
		printResult(indexSearcher, query);
	}
	//booleanQuery,布尔类型的查询，实现组合查询
	@Test
	public void testBooleanQuery() throws Exception {
		IndexSearcher indexSearcher = this.getIndexSearcher();
		//创建精确查找对象
		Query query1 = new TermQuery(new Term("name","lucene"));
		//创建数字区域查询对象
		Query query2 = NumericRangeQuery.newFloatRange("price", 70f, 90f,false, true);
		//创建组合条件查询对象
		BooleanQuery booleanQuery = new BooleanQuery();
		booleanQuery.add(query1,Occur.SHOULD);
		booleanQuery.add(query2,Occur.SHOULD);
		System.out.println(booleanQuery);
		printResult(indexSearcher, booleanQuery);
	}
	//对多个域进行查询
	@Test
	public void testMultiFieldQueryParser() throws Exception {
		IndexSearcher indexSearcher = this.getIndexSearcher();
		//创建域的数组
		String [] fields = {"name","desc"};
		MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(fields,new IKAnalyzer());
		Query query = multiFieldQueryParser.parse("lucene");
		printResult(indexSearcher, query);
	}
}



