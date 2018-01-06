package com.itheima.lucene;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

public class SearchIndexTest {

	@Test
	public void testSearchIndex() throws Exception {
		//1，创建搜索对象
		//创建分词器
		Analyzer analyzer = new SimpleAnalyzer();
		//创建搜索解析器 '第一个参数：field域对象，第二个参数：分词器对象
		QueryParser queryParser = new QueryParser("desc",analyzer);
		//创建搜索对象
		Query query = queryParser.parse("desc:java AND lucene");
		//2,创建directory流对象，声明索引库的位置
		Directory directroy = FSDirectory.open(new File("e:\\term"));
		//3,创建索引读取对象
		IndexReader indexReader = DirectoryReader.open(directroy);
		//4,创建索引搜获对象
		IndexSearcher searcher = new IndexSearcher(indexReader);
		//5,使用索引搜索对象执行搜索，返回结果集TopDocs
		TopDocs topDocs = searcher.search(query, 5);
		System.out.println("查询到的数据总条数是："+topDocs.totalHits);
		//6,获取搜索结果集
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		//解析结果集
		for (ScoreDoc scoreDoc : scoreDocs) {
			//获取文档
			int docID= scoreDoc.doc;
			Document doc = searcher.doc(docID);
			System.out.println("===========================");
			System.out.println("docID:"+docID);
			System.out.println("id:"+doc.get("id"));
			System.out.println("name:"+doc.get("name"));
			System.out.println("price:"+doc.get("price"));
			System.out.println("pic:"+doc.get("pic"));
			System.out.println("desc:" + doc.get("desc"));
		}
		//7，释放资源
		indexReader.close();
	}
	@Test
	public void testSearchIndex1() throws Exception {
		//创建索引
		Query query = new TermQuery(new Term("name","java"));
		
		//索引库 位置对象
		Directory directory = FSDirectory.open(new File("e:\\term"));
		//执行查询
		IndexReader indexReader = DirectoryReader.open(directory);
		//搜索
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		//执行  5条的文档ID
		TopDocs topDocs = indexSearcher.search(query, 5);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docID = scoreDoc.doc;
			Document doc = indexSearcher.doc(docID);
			System.out.println("ID:" + doc.get("id"));
			System.out.println("名称:" + doc.get("name"));
			System.out.println("价格:" + doc.get("price"));
			System.out.println("图片:" + doc.get("pic"));
			System.out.println("描述:" + doc.get("desc"));
		}
		indexReader.close();
	}

}
