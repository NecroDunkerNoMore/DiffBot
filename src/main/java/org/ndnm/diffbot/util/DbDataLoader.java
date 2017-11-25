package org.ndnm.diffbot.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndnm.diffbot.spring.SpringContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class DbDataLoader {
    private static final Logger LOG = LogManager.getLogger(DbDataLoader.class);
    private static final int BATCH_SIZE = 1000;

    private final DataSource dataSource;
    private final DataSource rootDataSource;


    @Autowired
    public DbDataLoader(DataSource dataSource, DataSource rootDataSource) {
        this.dataSource = dataSource;
        this.rootDataSource = rootDataSource;
    }


    public void fireAllScripts() {
        teardownDb();
        bootstrapDb();
        loadDummyDiffUrls();
    }


    private void bootstrapDb() {
        executeSqlStatements(getBootstrapSqlStatements(), getRootDataSource());
    }


    private void teardownDb() {
        try {
            executeSqlStatements(getTeardownSqlStatements(), getRootDataSource());
        } catch (Exception e) {
            // No-op in the case the user/schema didn't exist before we started
            LOG.info("It appears the diffbot schema was not present: %s", e.getMessage());
        }
    }


    private void loadDummyDiffUrls() {
        executeSqlStatements(getDiffUrlStatements(), getDataSource());
    }


    private List<String> getDiffUrlStatements() {
        return getSqlStatements("src/main/resources/sql/diff_url_t.sql");
    }


    private List<String> getTeardownSqlStatements() {
        return getSqlStatements("src/main/resources/ddl/teardown.sql");
    }


    private List<String> getBootstrapSqlStatements() {
        return getSqlStatements("src/main/resources/ddl/bootstrap.sql");
    }


    private void executeSqlStatements(List<String> stringStatements, DataSource aDataSource) {
        Connection connection = null;
        try {
            connection = aDataSource.getConnection();
            Statement statement = connection.createStatement();

            int totalDone = 0;
            int totalToDo = stringStatements.size();
            int batchCountRightNow = 0;
            for (String stringStatement : stringStatements) {
                batchCountRightNow++;
                statement.addBatch(stringStatement);
                if (batchCountRightNow >= BATCH_SIZE) {
                    totalDone += batchCountRightNow;
                    statement.executeBatch();
                    statement.clearBatch();

                    LOG.info("Executed: %d statements (%d/%d)", batchCountRightNow, totalDone, totalToDo);
                    batchCountRightNow = 0;
                }
            }

            if (batchCountRightNow > 0) {
                totalDone += batchCountRightNow;
                statement.executeBatch();
                LOG.info("Executed: %d statements (%d/%d)", batchCountRightNow, totalDone, totalToDo);
            }

            statement.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOG.error("Could not close connection: %s", e.getMessage());
                }
            }
        }
    }


    private List<String> getSqlStatements(String filepath) {
        FileReader fileReader;
        try {
            fileReader = new FileReader(filepath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return getSqlStatements(fileReader);
    }



    private List<String> getSqlStatements(Reader reader) {
        Scanner scanner = new Scanner(reader);
        StringBuilder stringBuilder = new StringBuilder();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (StringUtils.isNotBlank(line) && !line.startsWith("--")) {
                line = line.trim().replaceAll(" +", " "); // replace mutliple spaces with one
                line = line + " ";
                stringBuilder.append(line);
            }
        }

        scanner.close();

        String[] statements = stringBuilder.toString().split(";");
        List<String> goodStatements = new ArrayList<>();
        for (String statement : statements) {
            if (StringUtils.isNotBlank(statement)) {
                // Fencepost for split(";"), which will produce empty token after last ';'
                goodStatements.add(statement);
            }
        }


        return goodStatements;
    }


    private DataSource getDataSource() {
        return dataSource;
    }


    private DataSource getRootDataSource() {
        return rootDataSource;
    }


    public static void main(String... args) {
        DbDataLoader dataLoader = SpringContext.getBean(DbDataLoader.class);
        dataLoader.fireAllScripts();
    }


}
