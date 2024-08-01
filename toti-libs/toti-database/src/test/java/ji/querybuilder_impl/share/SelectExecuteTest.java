package ji.querybuilder_impl.share;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.querybuilder.builder_impl.share.SelectExecute;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class SelectExecuteTest {

	@Test
	@Parameters(method="dataParseValueWorksWithDateTime")
	public void testParseValueWorksWithDateTime(Date datetime, String datetimeString, TemporalAccessor expected) throws SQLException {
		SelectExecute exec = new SelectExecute() {};
		ResultSet rs = mock(ResultSet.class);
		when(rs.getObject(anyInt())).thenReturn(datetime);
		when(rs.getString(anyInt())).thenReturn(datetimeString);
		assertEquals(expected, exec._parseValue(rs, 0));
	}
	
	public Object[] dataParseValueWorksWithDateTime() {
		return new Object[] {
			new Object[] {
				Time.valueOf(LocalTime.of(10, 12)), "", LocalTime.of(10, 12)
			},
			new Object[] {
				Time.valueOf(LocalTime.of(10, 12, 45)), "", LocalTime.of(10, 12, 45)
			},
			new Object[] {
				Time.valueOf(LocalTime.of(10, 12, 45, 123)), "", LocalTime.of(10, 12, 45)
			},
			new Object[] {
				java.sql.Date.valueOf(LocalDate.of(2021, 8, 20)), "", LocalDate.of(2021, 8, 20)
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17", LocalDateTime.of(2021, 8, 12, 4, 17)
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:22", LocalDateTime.of(2021, 8, 12, 4, 17, 22)
			},
			new Object[] {
				new Timestamp(0), "2021-08-12T04:17:22", LocalDateTime.of(2021, 8, 12, 4, 17, 22)
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:22.1", LocalDateTime.of(2021, 8, 12, 4, 17, 22, 100000000)
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:22.821364", LocalDateTime.of(2021, 8, 12, 4, 17, 22, 821364000)
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:22.821364+01:00",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 22, 821364000, ZoneId.of("+1"))
			},
			new Object[] {
				new Timestamp(0), "2021-08-12T04:17:22.821364+01:00",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 22, 821364000, ZoneId.of("+1"))
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:22.821364-01:00",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 22, 821364000, ZoneId.of("-1"))
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:22.821364+01",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 22, 821364000, ZoneId.of("+1"))
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:22.821364 +01:00",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 22, 821364000, ZoneId.of("+1"))
			}
		};
	}
	
}
