package com.company;

import java.sql.*;
import java.time.LocalDate;
/**
 * PSQL class provides function that perform SQL writing, reading, editing to the db used by SmallURL
 */

public class PSQL {

    private String url = "jdbc:postgresql://";
    private String user;
    private String password;

    /**
     * Constructs and Initialize PSQL with userID, pwd, dbName and dburl
     * @param userID database user id
     * @param pwd database password
     * @param dbName database name
     * @param dburl url or address od database
     */
    public PSQL(String userID, String pwd, String dbName, String dburl){
        this.user = userID;
        this.password = pwd;
        this.url = this.url+ dburl +"/"+ dbName;

    }

    /**
     * Function creates a connection to the database
     * @return a connection object
     */
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

    /**
     * Prints specified table in database.
     * @param table name of table to be printed
     */
    public void printDBTable(String table) {

        String SQL = "SELECT * FROM " + table;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            printTable(rs, SQL);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }


    }


    /**
     * function prints the retrieved query results.
     * @param sqlStatement sql query as a string.
     */
    public void retriveAndPrintWithQuery(String sqlStatement) {
        String SQL = sqlStatement;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            printTable(rs, SQL);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Returns the full URL given the row id in the table.
     * @param rowId id number of small url record
     * @return string of the full url
     */
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

    /**
     * Returns the last row id# of a given table.
     * @param tableName name of the table in the database
     * @return return a long number that is the last record id.
     */
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

    /**
     *  Print table query with with a resultset and string sql query as input.
     * @param rs resultset object
     * @param sqlQuery a sql query in string format
     * @throws SQLException
     */
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

    /**
     * Updates the full url record given the rowid and current userid. If successful, function return the row id.
     * @param rowid row id number of record
     * @param fullurl full url in string format
     * @param currUserId current userid trying to make the update as an int
     * @return row id as int
     */
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

    /**
     * Inserts full url into db and returns the Row ID
     * @param long_url full url as string
     * @param currUserId current user id as int
     * @return
     */
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

    /**
     * delete record with rowid and currUser
     * @param rowid
     * @param currUserid
     * @return
     */
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
