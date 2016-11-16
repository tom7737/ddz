package com.ddz.ms.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.google.gson.Gson;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class ObjectUtil {
	/**
	 * 对象转byte[]
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static byte[] objectToBytes(Object obj) throws Exception {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(bo);
		oo.writeObject(obj);
		byte[] bytes = bo.toByteArray();
		bo.close();
		oo.close();
		return bytes;
	}

	/**
	 * byte[]转对象
	 * 
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	public static Object bytesToObject(byte[] bytes) throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ObjectInputStream sIn = new ObjectInputStream(in);
		return sIn.readObject();
	}

	/**
	 * 将普通Object对象转换成mongodb的DBObject对象
	 * 
	 * @param obj
	 * @return
	 */
	public static DBObject toDBObject(Object obj) {
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		return (DBObject) JSON.parse(json);
	}

	/**
	 * 将对象转化成json字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String objectToJson(Object obj) {
		Gson gson = new Gson();
		return gson.toJson(obj);
	}

	/**
	 * 将json字符串转化成对象
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> T jsonToObject(String json, Class<T> classOfT) {
		Gson gson = new Gson();
		return gson.fromJson(json, classOfT);
	}
}