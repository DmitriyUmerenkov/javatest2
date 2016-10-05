package ru.umerenkov.adm.javatest2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class Dao {

	private NamedParameterJdbcTemplate jdbc;

	@Autowired
	public void setDataSource(DataSource jdbc) {
		this.jdbc = new NamedParameterJdbcTemplate(jdbc);

		System.out.println("Datasource set");
		try {
			jdbc.getConnection();
		} catch (Exception e) {
			System.out.println(e);
		}
		;
	}

	public List<Long> getClientIDs() {
		System.out.println("Getting client ID");
		return jdbc.query("select customer_id from admtestdb.traffic", new RowMapper<Long>() {

			@Override
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				// TODO Auto-generated method stub
				System.out.println("Row procecced");
				return rs.getLong("customer_id");

			}

		});
	}

	public long getTraffic(long customerId, Date startDate, Date endDate, DataDirection dataDirection) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("startDate", startDate);
		params.addValue("customerId", customerId);
		params.addValue("endDate", endDate);

		System.out.println(params.getValue("startDate"));

		if (dataDirection == DataDirection.UPLINK)

			return jdbc.queryForObject(
					"select ifnull(sum(uplink),0) from admtestdb.traffic where customer_id=:customerId and date>=:startDate and date<:endDate",
					params, Long.class);

		else if (dataDirection == DataDirection.DOWNLINK)
			return jdbc.queryForObject(
					"select ifnull(sum(downlink),0) from admtestdb.traffic where customer_id=:customerId and date>=:startDate and date<:endDate",
					params, Long.class);

		else
			return -1;

	}
	
	public long getMaxSpeed(long customerId, Date startDate, Date endDate, DataDirection dataDirection) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("startDate", startDate);
		params.addValue("customerId", customerId);
		params.addValue("endDate", endDate);

		System.out.println(params.getValue("startDate"));

		if (dataDirection == DataDirection.UPLINK)

			return jdbc.queryForObject(
					"select ifnull(max(uplink),0) from admtestdb.traffic where customer_id=:customerId and date>=:startDate and date<:endDate",
					params, Long.class);

		else if (dataDirection == DataDirection.DOWNLINK)
			return jdbc.queryForObject(
					"select ifnull(max(downlink),0) from admtestdb.traffic where customer_id=:customerId and date>=:startDate and date<:endDate",
					params, Long.class);

		else
			return -1;

	}


}
