package mitw.meetup.util;

import lombok.Getter;
import me.skymc.taboolib.mysql.builder.SQLColumn;
import me.skymc.taboolib.mysql.builder.SQLColumnType;
import me.skymc.taboolib.mysql.builder.SQLTable;
import net.development.mitw.Mitw;
import net.development.mitw.database.Database;

@Getter
public class UHCMeetupDatabase {

    private Database database;
    private SQLTable table;

    public UHCMeetupDatabase() {
        database = new Database(Mitw.getInstance(), "uhcmeetup");
        this.createTables();
    }

    private void createTables() {
        table = new SQLTable("uhcmeetup_player")
                .addColumn(new SQLColumn(SQLColumnType.TEXT, "uuid"))
                .addColumn(new SQLColumn(SQLColumnType.TEXT, "name"))
                .addColumn(new SQLColumn(SQLColumnType.INT, "global_kills"))
                .addColumn(new SQLColumn(SQLColumnType.INT, "wins"))
                .addColumn(new SQLColumn(SQLColumnType.INT, "deaths"))
                .addColumn(new SQLColumn(SQLColumnType.INT, "games_played"))
                .addColumn(new SQLColumn(SQLColumnType.INT, "elo"));
        table.executeUpdate(table.createQuery()).dataSource(database.getDataSource()).run();
    }

}