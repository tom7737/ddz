package demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.Controller;
import com.jfinal.ext.plugin.monogodb.MongoKit;
import com.jfinal.plugin.activerecord.Record;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

public class HelloController extends Controller {

	public void index() {
		// DBCursor find = MongoKit.getDB().getCollection("users").find();
		Record record = new Record().set("userid", "userid")
				.set("click_type", "click_type").set("id", "id")
				.set("type", "type")
				.set("createtime", new Timestamp(System.currentTimeMillis()));
		MongoKit.save("wgh", record);
		// Page<Record> findFirst = MongoKit.paginate("wgh", 0, 1);
		// BasicDBObject query = new BasicDBObject();
		// query.put("name", "MongoDB");
//		DBObject[] rooms = new DBObject[]{new Room()};
		DBCollection collection = MongoKit.getCollection("wgh");
		BasicDBObject bd = new BasicDBObject();
		BasicDBList list = new BasicDBList();
		bd.put("rooms", list);
		WriteResult insert = collection.save(bd);
		renderJson(insert.toString());
	}

	public void haha() {
		renderText("Hello JFinal haha");
	}

	public void test() throws IOException {
		HttpServletResponse response = this.getResponse();

		// content type must be set to text/event-stream

		response.setContentType("text/event-stream");

		// encoding must be set to UTF-8
		response.setCharacterEncoding("UTF-8");

		PrintWriter writer = response.getWriter();

		Random random = new Random();

		for (int i = 0; i < 10; i++) {
			int nextInt = random.nextInt(55);
			System.out.println(nextInt);
			writer.print("data: " + nextInt + "\n\n");
			writer.flush();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		writer.close();
		System.out.println("return 之前");
		renderNull();
		return;
	}
}