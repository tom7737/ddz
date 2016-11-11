package com.ddz.common.mongo;
import java.net.UnknownHostException;  
import java.util.ArrayList;  
import java.util.List;  
import java.util.Set;  
import java.util.regex.Pattern;  
  
import org.junit.Test;  
  
import com.mongodb.AggregationOutput;  
import com.mongodb.BasicDBList;  
import com.mongodb.BasicDBObject;  
import com.mongodb.BasicDBObjectBuilder;  
import com.mongodb.DB;  
import com.mongodb.DBCollection;  
import com.mongodb.DBCursor;  
import com.mongodb.DBObject;  
import com.mongodb.MapReduceCommand;  
import com.mongodb.MapReduceOutput;  
import com.mongodb.Mongo;  
import com.mongodb.QueryBuilder;  
import com.mongodb.WriteConcern;  
  
public class TestCase {  
       //DBCursor cursor = coll.find(condition).addOption(Bytes.QUERYOPTION_NOTIMEOUT);//设置游标不要超时  
  
    @Test  
    /** 
     * 获取所有数据库实例 
     */  
    public void testGetDBS() {  
        List<String> dbnames = MongoUtil.getMong().getDatabaseNames();  
        for (String dbname : dbnames) {  
            System.out.println("dbname:" + dbname);  
        }  
    }  
  
    @Test  
    /** 
     * 删除数据库 
     */  
    public void dropDatabase() {  
        MongoUtil.getMong().dropDatabase("my_new_db");  
    }  
  
    @Test  
    /** 
     * 查询所有表名 
     */  
    public void getAllCollections() {  
        Set<String> colls = MongoUtil.getDB().getCollectionNames();  
        for (String s : colls) {  
            System.out.println(s);  
        }  
    }  
  
    @Test  
    public void dropCollection() {  
        MongoUtil.getColl("jellonwu").drop();  
    }  
  
    /** 
     * 添加一条记录 
     */  
    @Test  
    public void addData() {  
        DBCollection coll = MongoUtil.getColl("wujintao");  
        BasicDBObject doc = new BasicDBObject();  
        doc.put("name", "MongoDB");  
        doc.put("type", "database");  
        doc.put("count", 1);  
  
        BasicDBObject info = new BasicDBObject();  
        info.put("x", 203);  
        info.put("y", 102);  
        doc.put("info", info);  
        coll.insert(doc);  
        // 设定write concern，以便操作失败时得到提示  
        coll.setWriteConcern(WriteConcern.SAFE);  
    }  
  
    @Test  
    /** 
     * 创建索引 
     */  
    public void createIndex() {  
        MongoUtil.getColl("wujintao").createIndex(new BasicDBObject("i", 1));  
    }  
  
    @Test  
    /** 
     * 获取索引信息 
     */  
    public void getIndexInfo() {  
        List<DBObject> list = MongoUtil.getColl("hems_online").getIndexInfo();  
        for (DBObject o : list) {  
            System.out.println(o);  
        }  
    }  
  
    @Test  
    /** 
     * 添加多条记录 
     */  
    public void addMultiData() {  
        for (int i = 0; i < 100; i++) {  
            MongoUtil.getColl("wujintao").insert(  
                    new BasicDBObject().append("i", i));  
        }  
  
        List<DBObject> docs = new ArrayList<DBObject>();  
        for (int i = 0; i < 50; i++) {  
            docs.add(new BasicDBObject().append("i", i));  
        }  
        MongoUtil.getColl("wujintao").insert(docs);  
        // 设定write concern，以便操作失败时得到提示  
        MongoUtil.getColl("wujintao").setWriteConcern(WriteConcern.SAFE);  
    }  
  
    @Test  
    /** 
     * 查找第一条记录 
     */  
    public void findOne() {  
        DBObject myDoc = MongoUtil.getColl("wujintao").findOne();  
        System.out.println(myDoc);  
    }  
  
    @Test  
    /** 
     * 获取表中所有记录条数 
     */  
    public void count() {  
        System.out.println(MongoUtil.getColl("users").getCount());  
        System.out.println(MongoUtil.getColl("users").count());  
    }  
  
    @Test  
    /** 
     * 获取查询结果集的记录数 
     */  
    public void getCount() {  
        DBObject query = new BasicDBObject("name", "a");  
        long count = MongoUtil.getColl("wujintao").count(query);  
        System.out.println(count);  
    }  
  
    @Test  
    /** 
     * 查询所有结果 
     */  
    public void getAllDocuments() {  
        DBCursor cursor = MongoUtil.getColl("wujintao").find();  
        try {  
            while (cursor.hasNext()) {  
                System.out.println(cursor.next());  
            }  
        } finally {  
            cursor.close();  
        }  
    }  
  
    @Test  
    /** 
     * 按照一个条件查询 
     */  
    public void queryByConditionOne() {  
        BasicDBObject query = new BasicDBObject();  
        query.put("name", "MongoDB");  
        DBCursor cursor = MongoUtil.getColl("wujintao").find(query);  
  
        try {  
            while (cursor.hasNext()) {  
                System.out.println(cursor.next());  
            }  
        } finally {  
            cursor.close();  
        }  
    }  
  
    @Test  
    /** 
     * AND多条件查询,区间查询 
     */  
    public void queryMulti() {  
        BasicDBObject query = new BasicDBObject();  
        // 查询j不等于3,k大于10的结果集  
        query.put("j", new BasicDBObject("$ne", 3));  
        query.put("k", new BasicDBObject("$gt", 10));  
        DBCursor cursor = MongoUtil.getColl("wujintao").find(query);  
        try {  
            while (cursor.hasNext()) {  
                System.out.println(cursor.next());  
            }  
        } finally {  
            cursor.close();  
        }  
    }  
  
    @Test  
    /** 
     * 区间查询 
     * select * from table where i >50 
     */  
    public void queryMulti2() {  
        BasicDBObject query = new BasicDBObject();  
        query = new BasicDBObject();  
        query.put("i", new BasicDBObject("$gt", 50)); // e.g. find all where i >  
        DBCursor cursor = MongoUtil.getColl("wujintao").find(query);  
        try {  
            while (cursor.hasNext()) {  
                System.out.println(cursor.next());  
            }  
        } finally {  
            cursor.close();  
        }  
    }  
  
    @Test  
    /** 
     * 区间查询 
     * select * from table where 20 < i <= 30 
        //比较符    
        //"$gt"： 大于    
        //"$gte"：大于等于    
        //"$lt"： 小于    
        //"$lte"：小于等于    
        //"$in"： 包含    
     */  
    public void queryMulti3() {  
        BasicDBObject query = new BasicDBObject();  
        query = new BasicDBObject();  
  
        query.put("i", new BasicDBObject("$gt", 20).append("$lte", 30));  
        DBCursor cursor = MongoUtil.getColl("wujintao").find(query);  
        try {  
            while (cursor.hasNext()) {  
                System.out.println(cursor.next());  
            }  
        } finally {  
            cursor.close();  
        }  
    }  
  
    /** 
     * 组合in和and select * from test_Table where (a=5 or b=6) and (c=5 or d = 6) 
     */  
    public void queryMulti4() {  
        BasicDBObject query11 = new BasicDBObject();  
        query11.put("a", 1);  
        BasicDBObject query12 = new BasicDBObject();  
        query12.put("b", 2);  
        List<BasicDBObject> orQueryList1 = new ArrayList<BasicDBObject>();  
        orQueryList1.add(query11);  
        orQueryList1.add(query12);  
        BasicDBObject orQuery1 = new BasicDBObject("$or", orQueryList1);  
  
        BasicDBObject query21 = new BasicDBObject();  
        query21.put("c", 5);  
        BasicDBObject query22 = new BasicDBObject();  
        query22.put("d", 6);  
        List<BasicDBObject> orQueryList2 = new ArrayList<BasicDBObject>();  
        orQueryList2.add(query21);  
        orQueryList2.add(query22);  
        BasicDBObject orQuery2 = new BasicDBObject("$or", orQueryList2);  
  
        List<BasicDBObject> orQueryCombinationList = new ArrayList<BasicDBObject>();  
        orQueryCombinationList.add(orQuery1);  
        orQueryCombinationList.add(orQuery2);  
  
        BasicDBObject finalQuery = new BasicDBObject("$and",  
                orQueryCombinationList);  
        DBCursor cursor = MongoUtil.getColl("wujintao").find(finalQuery);  
    }  
  
    @Test  
    /** 
     * IN查询 
     * if i need to query name in (a,b); just use { name : { $in : ['a', 'b'] } } 
     * select * from things where name='a' or name='b' 
     * @param coll 
     */  
    public void queryIn() {  
        BasicDBList values = new BasicDBList();  
        values.add("a");  
        values.add("b");  
        BasicDBObject in = new BasicDBObject("$in", values);  
        DBCursor cursor = MongoUtil.getColl("wujintao").find(  
                new BasicDBObject("name", in));  
        try {  
            while (cursor.hasNext()) {  
                System.out.println(cursor.next());  
            }  
        } finally {  
            cursor.close();  
        }  
    }  
  
    @Test  
    /** 
     * 或查询 
     * select * from table where name  = '12' or title = 'p' 
     * @param coll 
     */  
    public void queryOr() {  
        QueryBuilder query = new QueryBuilder();  
        query.or(new BasicDBObject("name", 12), new BasicDBObject("title", "p"));  
        DBCursor cursor = MongoUtil.getColl("wujintao").find(query.get()).addSpecial("$returnKey", "");  
        try {  
            while (cursor.hasNext()) {  
                System.out.println(cursor.next());  
            }  
        } finally {  
            cursor.close();  
        }  
    }  
      
    @Test  
    public void customQueryField() throws UnknownHostException{  
        Mongo mongo = new Mongo("localhost", 27017);  
        DB db = mongo.getDB("zhongsou_ad");  
        BasicDBObjectBuilder bulder = new BasicDBObjectBuilder();  
        bulder.add("times",1);  
        bulder.add("aid",1);  
        DBCursor cusor =  db.getCollection("ad_union_ad_c_1").find(new BasicDBObject(),bulder.get());  
        for (DBObject dbObject : cusor) {  
            System.out.println(dbObject);  
        }  
    }  
      
    @Test  
    public void mapReduce() throws UnknownHostException{  
        Mongo mongo = new Mongo("localhost", 27017);  
        DB db = mongo.getDB("zhongsou_ad");  
        /*** 
         *  book1 = {name : "Understanding JAVA", pages : 100} 
         *  book2 = {name : "Understanding JSON", pages : 200} 
         *  db.books.save(book1) 
         *  db.books.save(book2) 
         *  book = {name : "Understanding XML", pages : 300} 
         *  db.books.save(book) 
         *  book = {name : "Understanding Web Services", pages : 400} 
         *  db.books.save(book) 
         *  book = {name : "Understanding Axis2", pages : 150} 
         *  db.books.save(book)   
         *   
        var map = function() { 
            var category; 
            if ( this.pages >= 250 ) 
                category = 'Big Books'; 
            else 
                category = "Small Books"; 
            emit(category, {name: this.name}); 
        }; 
        var reduce = function(key, values) { 
            var sum = 0; 
            values.forEach(function(doc) { 
                sum += 1; 
            }); 
            return {books: sum}; 
        };        
        var count  = db.books.mapReduce(map, reduce, {out: "book_results"}); 
         */  
        try {  
  
            DBCollection books = db.getCollection("books");  
  
            BasicDBObject book = new BasicDBObject();  
            book.put("name", "Understanding JAVA");  
            book.put("pages", 100);  
            books.insert(book);  
              
            book = new BasicDBObject();    
            book.put("name", "Understanding JSON");  
            book.put("pages", 200);  
            books.insert(book);  
              
            book = new BasicDBObject();  
            book.put("name", "Understanding XML");  
            book.put("pages", 300);  
            books.insert(book);  
              
            book = new BasicDBObject();  
            book.put("name", "Understanding Web Services");  
            book.put("pages", 400);  
            books.insert(book);  
            
            book = new BasicDBObject();  
            book.put("name", "Understanding Axis2");  
            book.put("pages", 150);  
            books.insert(book);  
              
            String map = "function() { "+   
                      "var category; " +    
                      "if ( this.pages >= 250 ) "+    
                      "category = 'Big Books'; " +  
                      "else " +  
                      "category = 'Small Books'; "+    
                      "emit(category, {name: this.name});}";  
              
            String reduce = "function(key, values) { " +  
                                     "var sum = 0; " +  
                                     "values.forEach(function(doc) { " +  
                                     "sum += 1; "+  
                                     "}); " +  
                                     "return {books: sum};} ";  
              
            MapReduceCommand cmd = new MapReduceCommand(books, map, reduce,  
              null, MapReduceCommand.OutputType.INLINE, null);  
  
            MapReduceOutput out = books.mapReduce(cmd);  
  
            for (DBObject o : out.results()) {  
             System.out.println(o.toString());  
            }  
           } catch (Exception e) {  
             e.printStackTrace();  
           }  
    }  
      
    @Test  
    public void GroupByManyField() throws UnknownHostException{  
        //此方法没有运行成功  
        Mongo mongo = new Mongo("localhost", 27017);  
        DB db = mongo.getDB("libary");  
        DBCollection books = db.getCollection("books");  
        BasicDBObject groupKeys = new BasicDBObject();  
        groupKeys.put("total", new BasicDBObject("$sum","pages"));  
          
        BasicDBObject condition = new BasicDBObject();  
        condition.append("pages", new BasicDBObject().put("$gt", 0));  
     
          
        String reduce = "function(key, values) { " +  
                "var sum = 0; " +  
                "values.forEach(function(doc) { " +  
                "sum += 1; "+  
                "}); " +  
                "return {books: sum};} ";  
        /** 
         BasicDBList basicDBList = (BasicDBList)db.getCollection("mongodb中集合编码或者编码") 
                   .group(DBObject key,   --分组字段，即group by的字段 
                DBObject cond,        --查询中where条件 
                DBObject initial,     --初始化各字段的值 
                String reduce,        --每个分组都需要执行的Function 
                String finial         --终结Funciton对结果进行最终的处理 
         */  
        DBObject obj = books.group(groupKeys, condition,  new BasicDBObject(), reduce);  
        System.out.println(obj);  
          
        AggregationOutput ouput = books.aggregate(new BasicDBObject("$group",groupKeys));  
        System.out.println(ouput.getCommandResult());  
        System.out.println(books.find(new BasicDBObject("$group",groupKeys)));  
    }     
      
  
    @Test  
    /** 
     * 分页查询    
     */  
    public void pageQuery() {  
        DBCursor cursor = MongoUtil.getColl("wujintao").find().skip(0)  
                .limit(10);  
        while (cursor.hasNext()) {  
            System.out.println(cursor.next());  
        }  
    }  
  
    /** 
     * 模糊查询 
     */  
    public void likeQuery() {  
        Pattern john = Pattern.compile("joh?n");  
        BasicDBObject query = new BasicDBObject("name", john);  
  
        // finds all people with "name" matching /joh?n/i  
        DBCursor cursor = MongoUtil.getColl("wujintao").find(query);  
    }  
  
    @Test  
    /** 
     * 条件删除    
     */  
    public void delete() {  
        BasicDBObject query = new BasicDBObject();  
        query.put("name", "xxx");  
        // 找到并且删除，并返回删除的对象  
        DBObject removeObj = MongoUtil.getColl("wujintao").findAndRemove(query);  
        System.out.println(removeObj);  
    }  
  
    @Test  
    /** 
     * 更新 
     */  
    public void update() {  
        BasicDBObject query = new BasicDBObject();  
        query.put("name", "liu");  
        DBObject stuFound = MongoUtil.getColl("wujintao").findOne(query);  
        stuFound.put("name", stuFound.get("name") + "update_1");  
        MongoUtil.getColl("wujintao").update(query, stuFound);  
    }  
  
  
} 