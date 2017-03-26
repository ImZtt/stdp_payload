package com.dinglicom.stdppayload.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Class.forName("org.apache.hive.jdbc.HiveDriver");
			Connection conn = DriverManager.getConnection("jdbc:hive2://172.16.34.164:10000/default","","");
			Statement stmt = conn.createStatement();
			String tablename = "test.lteu1_text";
			String quary_sql = "select u8interface from " + tablename;
			ResultSet rs = stmt.executeQuery(quary_sql);
			while(rs.next()){
			    System.out.println("u8interface: "+rs.getInt(1));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
