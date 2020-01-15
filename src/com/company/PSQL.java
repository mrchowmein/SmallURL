package com.company;

import java.sql.*;

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

        String SQL = "SELECT full_url FROM url_log WHERE Id = " + rowId+";";

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


    //function inserts full url into db and returns the Row ID
    public long insertURL(String long_url) {
        String SQL = "INSERT INTO url_log(full_url) "
                + "VALUES(?)";

        long id = 0;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, long_url);

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

}
