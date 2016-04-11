package nl.willemsenmedia.utwente.anonymization.data.database;

import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.DataType;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Martijn on 9-4-2016.
 */
public class DBHandler {
	private final Connection conn;

	public DBHandler() throws SQLException {
		conn = DriverManager.getConnection("jdbc:derby:derby_database.db;create=true");
		createEmptyTable();

	}

	/**
	 * Creates the table if it does not yet exist
	 *
	 * @return true if the <strong>empty</strong> table exists after execution, false if anything went wrong (@see Connection#prepareStatement())
	 * @throws SQLException (@see Connection#prepareStatement())
	 */
	public void createEmptyTable() throws SQLException {
		DatabaseMetaData dbmd = conn.getMetaData();
		ResultSet rs = dbmd.getTables(null, null, "DATA".toUpperCase(), null);
		if (!rs.next()) {
			String SQL = "CREATE TABLE DATA (" +
					"  ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
					"  ENTRY_ID INTEGER NOT NULL," +
					"  NAME VARCHAR(100)," +
					"  RAW_DATA LONG VARCHAR NOT NULL," +
					"  ANONIMOUS_DATA LONG VARCHAR DEFAULT NULL," +
					"  INDEX INTEGER NOT NULL," +
					"  DATATYPE VARCHAR(50)," +
					"  DO_ANONIMIZE BOOLEAN DEFAULT TRUE," +
					" CONSTRAINT primary_key PRIMARY KEY (id)" +
					")";
			conn.prepareStatement(SQL).execute();
		} else {
			conn.prepareStatement("TRUNCATE TABLE DATA").execute();
		}
	}

	/**
	 * Inserts the raw data into the database.
	 *
	 * @param entry_id  unique entry_id
	 * @param dataEntry the data entry
	 * @return true if successful for all attributes
	 * @throws SQLException
	 */
	public void saveRawData(int entry_id, DataEntry dataEntry) throws SQLException {
		for (int i = 0; i < dataEntry.getDataAttributes().size(); i++) {
			saveRawData(entry_id, dataEntry.getHeaders().get(i).getData(), dataEntry.getDataAttributes().get(i).getData(), i, dataEntry.getHeaders().get(i).getDataType().name(), dataEntry.getHeaders().get(i).doAnonimize());
		}
	}

	//	private boolean saveHeaders(int entry_id, String name, String data, int index) throws SQLException {
//		PreparedStatement stmt = conn.prepareStatement("INSERT INTO DATA (ENTRY_ID, NAME, DATA, INDEX, IS_HEADER) VALUES (?,?,?,?,?)");
//		stmt.setInt(1, entry_id);
//		stmt.setString(2, name);
//		stmt.setString(3, data);
//		stmt.setInt(4, index);
//		stmt.setBoolean(5, true);
//		return stmt.execute();
//	}
	private boolean saveRawData(int entry_id, String name, String data, int index, String datatype, boolean do_anonimize) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO DATA (ENTRY_ID, NAME, RAW_DATA, INDEX, DATATYPE, DO_ANONIMIZE) VALUES (?,?,?,?,?,?)");
		stmt.setInt(1, entry_id);
		stmt.setString(2, name);
		stmt.setString(3, data);
		stmt.setInt(4, index);
		stmt.setString(5, datatype);
		stmt.setBoolean(6, do_anonimize);
		return stmt.execute();
	}

	public List<DataEntry> getRawData() throws SQLException {
//		List<DataEntry> dataEntries = new LinkedList<>();
//		//Get all unique entry_ids
//		ResultSet entry_id_resultset = conn.prepareStatement("SELECT DISTINCT ENTRY_ID FROM DATA").executeQuery();
//		while (entry_id_resultset.next()) {
//			int entry_id = entry_id_resultset.getInt("ENTRY_ID");
//			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM DATA WHERE ANONIMOUS_ID IS NULL AND ENTRY_ID=? ORDER BY INDEX");
//			stmt.setInt(1, entry_id);
//			ResultSet resultSet = stmt.executeQuery();
//			LinkedList<DataAttribute> headers = new LinkedList<>();
//			LinkedList<DataAttribute> data = new LinkedList<>();
//			while (resultSet.next()) {
//				String header_name = resultSet.getString("NAME");
//				if (headers.stream().parallel().filter((entry) -> entry.getData().equals(header_name)).count() == 0)
//					headers.add(new DataAttribute(DataType.valueOf(resultSet.getString("DATATYPE")), resultSet.getString("NAME")));
//				data.add(new DataAttribute(DataType.valueOf(resultSet.getString("DATATYPE")), resultSet.getString("NAME"), resultSet.getString("DATA"), resultSet.getBoolean("DO_ANONIMIZE")));
//			}
//			dataEntries.add(new DataEntry(headers, data.toArray(new DataAttribute[data.size()])));
//		}

		return getData(false);
	}

	public List<DataEntry> getData(boolean anonimous) throws SQLException {
		List<DataEntry> dataEntries = new LinkedList<>();
		//Get all unique entry_ids
		ResultSet entry_id_resultset = conn.prepareStatement("SELECT DISTINCT ENTRY_ID FROM DATA").executeQuery();
		while (entry_id_resultset.next()) {
			int entry_id = entry_id_resultset.getInt("ENTRY_ID");
			String SQL = null;
			if (anonimous)
				SQL = "SELECT * FROM DATA WHERE ENTRY_ID=? AND ANONIMOUS_DATA IS NOT NULL ORDER BY INDEX";
			else
				SQL = "SELECT * FROM DATA WHERE ENTRY_ID=? AND ANONIMOUS_DATA IS NULL ORDER BY INDEX";
			PreparedStatement stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, entry_id);
			ResultSet resultSet = stmt.executeQuery();
			LinkedList<DataAttribute> headers = new LinkedList<>();
			LinkedList<DataAttribute> data = new LinkedList<>();
			while (resultSet.next()) {
				String header_name = resultSet.getString("NAME");
				if (headers.stream().parallel().filter((entry) -> entry.getData().equals(header_name)).count() == 0)
					headers.add(new DataAttribute(DataType.valueOf(resultSet.getString("DATATYPE")), resultSet.getString("NAME")));
				DataAttribute dataAttribute = new DataAttribute(DataType.valueOf(resultSet.getString("DATATYPE")), resultSet.getString("NAME"), anonimous ? resultSet.getString("ANONIMOUS_DATA") : resultSet.getString("RAW_DATA"), resultSet.getBoolean("DO_ANONIMIZE"));
				dataAttribute.setID(resultSet.getInt("ID"));
				data.add(dataAttribute);
			}
			dataEntries.add(new DataEntry(headers, data.toArray(new DataAttribute[data.size()])));
		}

		return dataEntries;
	}

	public void saveAnonimousData(DataEntry raw_data, DataEntry anonimous_data) throws SQLException {
		for (int i = 0; i < raw_data.getDataAttributes().size(); i++) {
			saveAnonimousData(raw_data.getDataAttributes().get(i).getId(), anonimous_data.getDataAttributes().get(i).getData());
		}
	}

	public void saveAnonimousData(int id, String anonimous_data) throws SQLException {
		PreparedStatement stmt_anonimous = conn.prepareStatement("UPDATE DATA SET ANONIMOUS_DATA=? WHERE ID=?");
		stmt_anonimous.setString(1, anonimous_data);
		stmt_anonimous.setInt(2, id);
		stmt_anonimous.executeUpdate();
	}
}
