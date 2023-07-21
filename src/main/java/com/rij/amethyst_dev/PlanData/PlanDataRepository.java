package com.rij.amethyst_dev.PlanData;

import com.rij.amethyst_dev.DTO.AllPlaytime;
import com.rij.amethyst_dev.Dev.UserDTOS.PlayTimeDateDTO;
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
public class PlanDataRepository {
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public PlanDataRepository(@Qualifier("datasource2") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }



    public Integer getPlanPlayerId(String playerName){
        String sql = "SELECT id FROM plan_users WHERE name = ?";
        List<String> qres = jdbcTemplate.queryForList(sql, String.class, playerName);

        Integer res = Integer.valueOf(qres.get(0));

        return res;
    }

    public List<PlayTimeDateDTO> getplaytimeSecByDate(int planPlayerId, LocalDate startdate) {

        String sql = "SELECT FLOOR(SUM((session_end - session_start) / 1000)) AS playtime, DATE(FROM_UNIXTIME(session_start / 1000)) AS date\n" +
                "FROM plan_sessions\n" +
                "WHERE user_id = ?\n" +
                "AND DATE(FROM_UNIXTIME(session_start / 1000)) BETWEEN ? AND ?\n" +
                "GROUP BY DATE(FROM_UNIXTIME(session_start / 1000))";


        try {
            return jdbcTemplate.query(sql, new PlayerRowMapper1(), planPlayerId, startdate, LocalDate.now());
        }catch (Exception any){
            return null;
        }

    }

    public class PlayerRowMapper1 implements RowMapper<PlayTimeDateDTO> {
        @Override
        public PlayTimeDateDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PlayTimeDateDTO(
                    rs.getString("playtime"),
                    rs.getString("date")
            );
        }
    }

    public Date getLastOnline(int planPlayerid){

        String sql = "SELECT FROM_UNIXTIME(session_end/1000) AS converted_session_end\n" +
                "FROM plan_sessions\n" +
                "WHERE user_id = ?\n" +
                "ORDER BY id DESC\n" +
                "LIMIT 1;";

        try {
            return jdbcTemplate.queryForObject(sql, Date.class, planPlayerid);
        }catch (Exception e){
            return null;
        }
    }

    public AllPlaytime getAllPlaytime(int planPlayerid){
        String sql = "SELECT\n" +
                "  ROUND(SUM(CASE WHEN session_end >= UNIX_TIMESTAMP(CURDATE() - INTERVAL 1 DAY) * 1000 THEN (session_end - session_start) / 1000 ELSE 0 END)) AS last_day_seconds,\n" +
                "  ROUND(SUM(CASE WHEN session_end >= UNIX_TIMESTAMP(CURDATE() - INTERVAL 1 WEEK) * 1000 THEN (`session_end` - session_start) / 1000 ELSE 0 END)) AS last_week_seconds,\n" +
                "  ROUND(SUM(CASE WHEN session_end >= UNIX_TIMESTAMP(CURDATE() - INTERVAL 1 MONTH) * 1000 THEN (session_end - session_start) / 1000 ELSE 0 END)) AS last_month_seconds,\n" +
                "  ROUND(SUM(CASE WHEN session_end >= 0 THEN (session_end - session_start) / 1000 ELSE 0 END)) AS all_time_seconds\n" +
                "FROM plan_sessions\n" +
                "WHERE user_id = ?;";

        try {
            return jdbcTemplate.queryForObject(sql, new PlayerRowMapper2(), planPlayerid);
        }catch (Exception e){
            return new AllPlaytime("0", "0", "0", "0");
        }
    }
    public class PlayerRowMapper2 implements RowMapper<AllPlaytime> {
        @Override
        public AllPlaytime mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new AllPlaytime(
                    rs.getString("last_day_seconds"),
                    rs.getString("last_week_seconds"),
                    rs.getString("last_month_seconds"),
                    rs.getString("all_time_seconds")
            );
        }
    }
}
