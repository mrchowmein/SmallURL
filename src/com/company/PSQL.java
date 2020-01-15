package com.company;

import java.sql.*;
import java.time.LocalDate;

public class PSQL {

    private String url = "jdbc:postgresql://";
    private String user;
    private String password;


    public PSQL(String userID, String pwd, String dbName, String dburl){
        this.user = userID;
        this.password = pwd;
        this.url = this.url+ dburl +"/"+ dbName;

    }

    //function creates connection to db
    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            //System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    //function prints entire table.
    public void printTable(String table) {

        String SQL = "SELECT * FROM " + table;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            printTable(rs, SQL);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }


    }

    //function prints whatever the retreive query returns.
    public void retriveWithQuery(String sqlStatement) {
        String SQL = sqlStatement;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            printTable(rs, SQL);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    //function uses the rowID to return the full url
    public String retriveFullUrl(Long rowId) {

        String SQL = "SELECT full_url FROM url_records WHERE Id = " + rowId+";";

        String full_url = "";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            if(rs.next()){
                full_url = rs.getString(1);
            } else {
                full_url = "-1";
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return full_url;
    }

    //function returns the last row id
    public long retreiveMaxRowID(String tableName){
        String SQL = "SELECT MAX(Id) FROM " + tableName +";";
        long rowId = -1;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            rs.next();
            rowId = rs.getInt(1);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return rowId;
    }

    //function to print table with resultset as input.
    public void printTable(ResultSet rs, String sqlQuery) throws SQLException {

        ResultSetMetaData rsmd = rs.getMetaData();
        System.out.println("SQL QUERY: "+ sqlQuery);
        int columnsNumber = rsmd.getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = rs.getString(i);
                // System.out.print(columnValue + " " + rsmd.getColumnName(i));
                System.out.print(columnValue);
            }
            System.out.println("");
        }

    }

    //function updates the full url given the correct row and curuserid
    public long updateURL(long rowid, String fullurl, int currUserId) {
        String SQL = "UPDATE url_records "
                + "SET full_url = ? "
                + "WHERE id = ? AND user_id = ? ";

        int affectedrows = 0;
        long affectedRowId = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, fullurl);
            pstmt.setLong(2, rowid);
            pstmt.setInt(3, currUserId);

            affectedrows = pstmt.executeUpdate();
            //System.out.println("affected rows: "+ affectedrows);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }


        System.out.println(affectedRowId);

        if(affectedrows == 0){
            return -1;
        } else {
            return rowid;
        }

    }


    //function inserts full url into db and returns the Row ID
    public long insertURL(String long_url, int currUserId) {
        String SQL = "INSERT INTO url_records(full_url,date,user_id)"
                + "VALUES(?,?,?)";

        long id = 0;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, long_url);
            pstmt.setObject(2, LocalDate.now());
            pstmt.setInt(3, currUserId);

            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return id;
    }

    //function deletes records with the matching userid and rowid
    public boolean deleteFullURLRecord(long rowid, int currUserid) {
        String SQL = "DELETE FROM url_records WHERE user_id = ? and id = ? ";

        int affectedrows = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, currUserid);
            pstmt.setLong(2, rowid);

            affectedrows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        if(affectedrows >= 1){
            if(affectedrows > 1){
                System.out.println("Warning, records deleted: "+affectedrows);
                return true;
            }
            return true;
        } else {
            return false;
        }

    }


}
