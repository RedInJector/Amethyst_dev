package com.rij.amethyst_dev.LibertybansData;

import com.rij.amethyst_dev.DTO.AllPlaytime;
import com.rij.amethyst_dev.DTO.PlayTimeDateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public class LibertybansDataRepository {
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public LibertybansDataRepository(@Qualifier("datasource3") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public boolean isBanned(String name){
        String sql = "SELECT ln.name, lb.id\n" +
                "FROM libertybans_names ln\n" +
                "JOIN libertybans_victims lv ON ln.uuid = lv.uuid\n" +
                "JOIN libertybans_bans lb ON lv.id = lb.victim\n" +
                "WHERE ln.name = ?";

        try {
            jdbcTemplate.queryForObject(sql, String.class, name);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public void unBann(String name){
        String sql = "DELETE FROM libertybans_bans\n" +
                "WHERE id IN (\n" +
                "  SELECT lb.id\n" +
                "  FROM libertybans_names ln\n" +
                "  JOIN libertybans_victims lv ON ln.uuid = lv.uuid\n" +
                "  JOIN libertybans_bans lb ON lv.id = lb.victim\n" +
                "  WHERE ln.name = " + name +
                " );";


            jdbcTemplate.execute(sql);
    }

    public String getNameFromUUID(String uuid){
        String sql = "SELECT ln.name FROM libertybans_names ln\n" +
                "WHERE ln.uuid = UNHEX(REPLACE(?, '-',''));";


        return jdbcTemplate.queryForObject(sql, String.class, uuid);
    }
}
