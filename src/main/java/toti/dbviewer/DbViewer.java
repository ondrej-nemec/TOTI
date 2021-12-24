package toti.dbviewer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ji.database.Database;
import ji.database.support.DatabaseRow;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.server.RequestParameters;
import toti.response.Response;

public class DbViewer {
	
	private final Database database;
	
	public DbViewer(Database database) {
		this.database = database;
	}

	public Response getResponse(HttpMethod method, String path, RequestParameters params) {
		Map<String, Object> values = new HashMap<>();
		List<String> errors = new LinkedList<>();
		values.put("errors", errors);
		/*
		try {
			Map<String, String> table = database.applyQuery((con)->{
				Map<String, String> result = new HashMap<>();
				ResultSet rs = con.createStatement().executeQuery(
					"SELECT"
					+ " table_name, ("
						+ " CASE"
						+ " WHEN table_type = 'BASE TABLE' THEN 'TABLE'"
						+ " ELSE table_type"
						+ " END"
					+ ") structure_type"
					+ " FROM INFORMATION_SCHEMA.TABLES"
				);
				while (rs.next()) {
					result.put(rs.getString(1), rs.getString(2));
				}
				return result;
			});
			values.put("tables", table);
		} catch (Exception e) {
			e.printStackTrace();
			errors.add("Error occurs: " + e.getMessage());
			values.put("tables", new HashMap<>());
		}*/
		
		if (path.equals("") || path.equals("/")) {
			values.put("query", "");
			values.put("queries", new LinkedList<>());
			if (method == HttpMethod.POST && params.toMap().containsKey("query")) {
				try {
					values.put("queries", executeQuery(params.getString("query")));
					values.put("query", params.getString("query"));
				} catch (Exception e) {
					e.printStackTrace();
					errors.add("Error occurs: " + e.getMessage());
				}
			}
			return  Response.getTemplate("/dbviewer/query.jsp", values);
		}
		System.err.println(path);
		// TODO path.substring(1).replace("-", "").replace(" ", "")
		return  Response.getTemplate("/dbviewer/table.jsp", values);
	}
	
	private List<QueryResult> executeQuery(String queries) throws Exception {
		return database.applyQuery((con)->{
			List<QueryResult> result = new LinkedList<>();
			for (String sql : queries.split(";")) {
				try (PreparedStatement stmt = con.prepareStatement(sql);) {
					if (stmt.execute()) {
						List<DatabaseRow> rows = parseRows(stmt);
						result.add(new QueryResult(sql, rows.size(), rows, null));
					} else {
						result.add(new QueryResult(sql, stmt.getUpdateCount(), null, null));
					}
				} catch (Exception e) {
					result.add(new QueryResult(sql, null, null, e.getMessage()));
				}
			}
			return result;
		});
	}

	private List<DatabaseRow> parseRows(PreparedStatement stmt) throws SQLException {
		List<DatabaseRow> rows = new LinkedList<>();
		try (ResultSet rs = stmt.getResultSet()) {
			while (rs.next()) {
				DatabaseRow row = new DatabaseRow();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					row.addValue(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
				}
				rows.add(row);
			}
		}
		return rows;
	}
	
}
