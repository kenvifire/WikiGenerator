import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: WikiGenerator
 * 
 * File Created at 2012-5-25下午2:39:17
 * $Id$
 * 
 * Copyright 1999-2012 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */

/**
 * 类 WikiGenerator 的实现描述：TODO 类实现描述
 * 
 * @author kenvizhu 2012-5-25下午2:39:17
 */
public class WikiGenerator {

    /**
     * @author kenvizhu 2012-5-25下午2:39:17
     * @param args
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void main(String[] args) throws Exception {
        WikiGenerator wikiGenerator = new WikiGenerator();
        Connection conn = wikiGenerator.getConnection("lpscm", "lpscm2011");
        String sql = "select * from SUPPLIER ";
        ResultSet rs = wikiGenerator.executeSql(conn, sql);
        File file = new File("/home/kenvizhu/wiki/selection_supplier.when.wiki");
        wikiGenerator.generateWiki(rs, file);
        System.out.println("wiki file generated");

    }

    private Connection getConnection(String user, String password) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        Connection con = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            con = DriverManager.getConnection("dbc:oracle:thin:@127.0.0.1:test", user, password);
        } catch (SQLException e) {
            return null;
        }
        return con;
    }

    private ResultSet executeSql(Connection conn, String sql) throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute(sql);
        return statement.getResultSet();

    }

    private void generateWiki(ResultSet rs, File file) throws Exception {
        ResultSetMetaData metaData = rs.getMetaData();
        List<String> typeList = new ArrayList<String>();
        int count = metaData.getColumnCount();
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        for (int i = 1; i <= count; i++) {
            sb.append(metaData.getColumnName(i)).append("|");
            typeList.add(metaData.getColumnClassName(i));
            System.out.println(metaData.getColumnClassName(i));

        }
        sb.append("\n");
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(sb.toString());

        sb = new StringBuilder("|");
        while (rs.next()) {
            for (int i = 1; i <= count; i++) {
                String str = correct(typeList.get(i - 1), rs.getString(i));
                sb.append(str).append("|");
            }
            sb.append("\n|");

        }
        rs.close();
        bw.write(sb.toString());
        bw.flush();
        bw.close();
    }

    private String correct(String type, String value) {
        if (type.equals("java.math.BigDecimal")) {
            try {
                BigDecimal num = new BigDecimal(value);
                return num.toString();
            } catch (Exception e) {
                return "0";
            }
        }
        if (type.equals("java.sql.Timestamp")) {
            if (value == null)
                return "@{datetime}";
        }
        return value;
    }
}
