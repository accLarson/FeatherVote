package com.zerek.feathervote.managers;

import com.zerek.feathervote.FeatherVote;
import org.javalite.activejdbc.Base;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {

    private static Connection connection;

    private final FeatherVote plugin;

    public DatabaseManager(FeatherVote plugin) {

        this.plugin = plugin;

        this.initConnection();

        this.initTables();
    }

    private void initConnection() {
        File folder = this.plugin.getDataFolder();

        File file = new File(folder.getAbsolutePath() + File.separator + "FeatherVote.db");

        try {

            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());

            Base.attach(connection);
        }

        catch (SQLException e) {

            plugin.getLogger().severe("Unable to initialize DatabaseManager connection.");
        }
    }

    private boolean existsTable() {

        try {

            if(!connection.isClosed()) {

                ResultSet rs = connection.getMetaData().getTables(null, null, "VOTES", null);

                return rs.next();
            }

            else return false;
        }

        catch (SQLException e) {

            plugin.getLogger().severe("Unable to query table metadata.");

            return false;
        }
    }

    private void initTables() {

        if(!this.existsTable()) {

            plugin.getLogger().info("Creating VOTES table.");

            String query = "CREATE TABLE IF NOT EXISTS `VOTES` ("
                    + " `mojang_uuid`                   VARCHAR(255) PRIMARY KEY NOT NULL, "
                    + " `updated_at`                    DATETIME, "
                    + " `rewards_owed`                  INT, "
                    + " `special_rewards_owed`          INT, "
                    + " `vote_count_total`              INT, "
                    + " `vote_count_previous_month`     INT, "
                    + " `vote_count_current_month`      INT );";

            try {

                if(!connection.isClosed()) connection.createStatement().execute(query);
            }

            catch (SQLException e) {

                e.printStackTrace();

                plugin.getLogger().severe("Unable to create VOTES table.");
            }
        }
    }
}
