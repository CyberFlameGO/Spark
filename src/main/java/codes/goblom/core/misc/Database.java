/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.misc;

import codes.goblom.core.GoPlugin;
import codes.goblom.core.configuration.Config;
import codes.goblom.core.internals.Callback;
import codes.goblom.core.internals.ExecutorNoArgs;
import codes.goblom.core.internals.Validater;
import codes.goblom.core.internals.task.AsyncTask;
import codes.goblom.core.misc.utils.Utils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Goblom
 */
public class Database {

    static {
        Utils.addNullCheckValidater((Validater<Connection>) (Connection obj) -> {
            boolean b = false;

            try {
                b = obj.isClosed();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            return b;
        });
        
        Utils.addNullCheckValidater((Validater<ResultSet>) (ResultSet obj) -> {
            boolean b = false;

            try {
                b = obj.isClosed();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            return b;
        });
        
    }

    @AllArgsConstructor
    public static enum Type {

        /**
         * {host} -- The URL of the MySQL Server
         * {port} -- The port of the MySQL server
         * {database} -- The Database name you are connecting to
         */
        MySQL("com.mysql.jdbc.Drive", "jdbc:mysql://{host}:{port}/{database}?autoReconnect=true"),
        
        /**
         * {file} -- The file of the SQLite database
         */
        SQLite("org.sqlite.JDBC", "jdbc:sqlite:{file}"),
        
        /**
         * Update this with your custom JDBC Driver & Loading arguments
         *
         * @deprecated loadArgs / callerClass Not Supported Directly
         */
        OTHER("", "");

        public String callerClass;
        public String loadArgs;
    }

    private final Type type;

    @Getter
    private Connection connection;

    private Database(Type type) {
        if (Utils.isNull(type.callerClass) || Utils.isNull(type.loadArgs)) {
            throw new RuntimeException("Caller class cannot be null for DatabaseType");
        }

        try {
            Class.forName(type.callerClass);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.type = type;
    }

    public Database(String path) throws SQLException {
        this(new File(path));
    }

    public Database(File file) throws SQLException {
        this(Type.SQLite);

        if (file.getParentFile() != null) {
            file.getParentFile().mkdir();
        }

        this.connection = DriverManager.getConnection(type.loadArgs.replace("{file}", file.getAbsolutePath()));
    }

    public Database(String host, int port, String database, String username, String password) throws SQLException {
        this(Type.MySQL);

        String loadArgs = type.loadArgs;
        loadArgs = loadArgs.replace("{host}", host);
        loadArgs = loadArgs.replace("{port}", String.valueOf(port));
        loadArgs = loadArgs.replace("{database}", database);

        this.connection = DriverManager.getConnection(loadArgs, username, password);
    }

    @Deprecated
    /**
     * Loads the Type.OTHER DatabaseType
     */
    public Database(ExecutorNoArgs<Connection, SQLException> exe) throws SQLException {
//        this(Type.OTHER);
        this.type = Type.OTHER;
        
        this.connection = exe.execute();

        if (Utils.isNull(this.connection)) {
            this.connection = DriverManager.getConnection(type.loadArgs);
        }
    }

    /**
     * For the sole purpose of having the connection be called within an async
     * task so you can perform your own queries on it without disturbing
     * anything
     */
    public void getConnectionAsync(Callback<Connection> async) {
        new AsyncTask<Connection>(async) {

            @Override
            public Connection execute() throws Throwable {
                return getConnection();
            }

        }.run();
    }

    public int executeUpdate(String sql) {
        int i = -1;
        try {
            Statement statement = connection.createStatement();
            i = statement.executeUpdate(sql);
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return i;
    }

    public void executeUpdateAsync(String sql, Callback<Integer> callback) {
        new AsyncTask<Integer>(callback) {

            @Override
            public Integer execute() throws Throwable {
                return executeUpdate(sql);
            }

        }.run();
    }

    public ResultSet executeQuery(String sql, Object... values) {
        ResultSet rs = null;
        try {
            if (values != null && values.length >= 1) {
                rs = connection.createStatement().executeQuery(String.format(sql, values));
            } else {
                rs = connection.createStatement().executeQuery(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rs;
    }

    public void executeQueryAsync(String sql, Callback<ResultSet> callback, Object... vals) {
        new AsyncTask<ResultSet>(callback) {

            @Override
            public ResultSet execute() throws Throwable {
                return executeQuery(sql, vals);
            }

        }.run();
    }

    public void close() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isValid(int timeout) {
        if (timeout < 0) {
            return true;
        }
        
        try {
            return connection.isValid(timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return true;
    }
    
    public int getRowCount(String table) {
        int size = 0;

        try {
            ResultSet rs = executeQuery("SELECT COUNT(*) AS count FROM %s", table);
            if (rs.next()) {
                size = rs.getInt("count");
            }

            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return size;
    }

    public void getRowCountAsync(String table, Callback<Integer> callback) {
        new AsyncTask<Integer>(callback) {

            @Override
            public Integer execute() throws Throwable {
                return getRowCount(table);
            }

        }.run();
    }

    public Statement createStatement() {
        Statement st = null;
        try {
            st = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return st;
    }

    public void createStatementAsync(Callback<Statement> callback) {
        new AsyncTask<Statement>(callback) {

            @Override
            public Statement execute() throws Throwable {
                return createStatement();
            }
        }.run();
    }

    public PreparedStatement prepareStatement(String sql, Object... values) {
        PreparedStatement ps = null;

        try {
            if (values != null && values.length >= 1) {
                ps = connection.prepareStatement(String.format(sql, values));
            } else {
                ps = connection.prepareStatement(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ps;
    }

    public void prepareStatementAsync(String sql, Callback<PreparedStatement> callback, Object... vals) {
        new AsyncTask<PreparedStatement>(callback) {

            @Override
            public PreparedStatement execute() throws Throwable {
                return prepareStatement(sql, vals);
            }

        }.run();
    }
    
    public Set<String> getColumns(String table) {
        Set<String> set = Sets.newHashSet();
        
        try {
            try (ResultSet rs = executeQuery("SELECT %s FROM %s", "*", table)) {
                ResultSetMetaData md = rs.getMetaData();
                
                for (int i = 1; i <= md.getColumnCount(); ++i) {
                    set.add(md.getColumnName(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return set;
    }
    
    public void getColumnsAsync(String table, Callback<Set<String>> callback) {
        new AsyncTask<Set<String>>(callback) {

            @Override
            public Set<String> execute() throws Throwable {
                return getColumns(table);
            }
            
        }.run();
    }
    
    public Set<String> getTables() {
        Set<String> set = Sets.newLinkedHashSet();
        
        try {
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            
            while (rs.next()) {
                set.add(rs.getString(3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return set;
    }
    
    public void getTablesAsync(Callback<Set<String>> callback) {
        new AsyncTask<Set<String>>(callback) {

            @Override
            public Set<String> execute() throws Throwable {
                return getTables();
            }
            
        }.run();
    }
    
    public boolean tableContains(String table, String column, Object value) {
        final String sql = "SELECT COUNT(%s) AS %s Count FROM %s WHERE %s='%s'";
        final ResultSet rs = executeQuery(sql, column, column, table, column, value);
        
        try {
            if (rs == null || rs.isAfterLast()) {
                return false;
            }
            
            if (rs.isBeforeFirst()) {
                rs.next();
            }
            
            return rs.getInt(1) != 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return false;
    }
    
    public void tableContainsAsync(String table, String column, Object value, Callback<Boolean> callback) {
        new AsyncTask<Boolean>(callback) {

            @Override
            public Boolean execute() throws Throwable {
                return tableContains(table, column, value);
            }        
            
        }.run();
    }
    
    public boolean isClosed() {
        boolean b = false;
        
        try {
            b = connection.isClosed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return b;
    }
    
    public SimpleResultSetIterator createSimpleIterator(ResultSet rs) {
        return new SimpleResultSetIterator(rs);
    }
    
    
    public static class SimpleResultSetIterator implements Iterable<ResultSetIteratorEntry> {

        private final List<ResultSetIteratorEntry> data = Lists.newLinkedList();
        
        private SimpleResultSetIterator(ResultSet rs) {
            if (Utils.isNull(rs)) {
                throw new RuntimeException("ResultSet cannot be null or closed");
            }
            
            try {
                Set<String> columns = Sets.newLinkedHashSet();
                ResultSetMetaData meta = rs.getMetaData();
                
                for (int columnCount = meta.getColumnCount(), i = 1; i <= columnCount; ++i) {
                    columns.add(meta.getColumnName(i));
                }
                
                while (rs.next()) {
                    for (String column : columns) {
                        ResultSetIteratorEntry entry = new ResultSetIteratorEntry(column, rs.getObject(column));
                        
                        data.add(entry);
                    }
                }
                
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        public Stream<ResultSetIteratorEntry> stream() {
            return data.stream();
        }
        
        public boolean isEmpty() {
            return data.isEmpty();
        }
        
        @Override
        public Iterator<ResultSetIteratorEntry> iterator() {
            return data.iterator();
        }
        
    }
    
    @RequiredArgsConstructor ( access = AccessLevel.PRIVATE )
    public static class ResultSetIteratorEntry {
        private final String column;
        private final Object data;
        
        public String column() {
            return this.column;
        }
        
        public Object data() {
            return this.data;
        }
        
        public <T> T dataAs(Class<T> type) {
            return (T) data();
        }
    }
    
    /**
     * Connect to the Database using information provided by the user
     * 
     * @throws java.sql.SQLException if there was an error connecting to the database
     */
    public static Database GenericConnect() throws SQLException {
        Config cfg = GoPlugin.getInstance().getConfig("database_settings");
        
        Type type = Type.valueOf(cfg.get("type", Type.MySQL.name()));
        Database db = null;
        
        switch (type) {
            case MySQL:
                String host = cfg.get("host", "localhost");
                int port = cfg.get("port", 3306);
                String username = cfg.get("username", "root");
                String password = cfg.get("password", "");
                String dbName = cfg.get("dbName", "minecraft");
                
                db = new Database(host, port, dbName, username, password);
                break;
            case SQLite:
                String file = cfg.get("file", "database.db");
                
                db = new Database(file);
                break;
            case OTHER:
                throw new IllegalArgumentException("Database.GenericConnect() does not support Database.Type.OTHER");
        }
        
        
        return db;
    }
}
