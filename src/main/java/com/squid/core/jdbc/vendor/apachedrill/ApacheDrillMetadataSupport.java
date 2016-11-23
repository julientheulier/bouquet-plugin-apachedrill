/*******************************************************************************
 * Copyright © Squid Solutions, 2016
 *
 * This file is part of Open Bouquet software.
 *  
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation (version 3 of the License).
 *
 * There is a special FOSS exception to the terms and conditions of the 
 * licenses as they are applied to this program. See LICENSE.txt in
 * the directory of this program distribution.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Squid Solutions also offers commercial licenses with additional warranties,
 * professional functionalities or services. If you purchase a commercial
 * license, then it supersedes and replaces any other agreement between
 * you and Squid Solutions (above licenses and LICENSE.txt included).
 * See http://www.squidsolutions.com/EnterpriseBouquet/
 *******************************************************************************/
package com.squid.core.jdbc.vendor.apachedrill;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.core.database.metadata.ColumnData;
import com.squid.core.database.metadata.GenericMetadataSupport;
import com.squid.core.database.metadata.VendorMetadataSupportExt;
import com.squid.core.database.model.DatabaseFactory;
import com.squid.core.database.model.Table;
import com.squid.core.database.model.TableType;

/**
 * Custom metadata support for DRILL in order to:
 * - handle FILEs
 * 
 * 
 * @author sergefantino
 *
 */
public class ApacheDrillMetadataSupport extends GenericMetadataSupport implements VendorMetadataSupportExt {
	
    static final Logger logger = LoggerFactory.getLogger(ApacheDrillMetadataSupport.class);

	public enum ApacheDrillVendorType {
		TABLE, FILE
	}
	
	// SHOW FILES columns
	private static final String NAME = "name";
	private static final String IS_DIRECTORY = "isDirectory";
	private static final String IS_FILE = "isFile";
	private static final String PERMISSIONS = "permissions";

	/**
	 * add support for listing FILES
	 */
	@Override
	public List<Table> getTables(DatabaseFactory df, Connection conn, String catalog, String schema, String tableName)
			throws ExecutionException {
		// list the default
		List<Table> tables = super.getTables(df, conn, catalog, schema, tableName);
		if (schema!=null && (schema.startsWith("dfs."))) {// only for filesystems
			// now try to list the files
			Statement stat = null;
			try {
				stat = conn.createStatement();
				// we can ignore the ctalog name for drill
				String sql = "show files in `"+schema+"`";
				ResultSet rs = stat.executeQuery(sql);
				while (rs.next ()) {
					String tname = rs.getString(NAME);// table name
					boolean isDirectory = rs.getBoolean(IS_DIRECTORY);
					boolean isFile = rs.getBoolean(IS_FILE);
					String permissions = rs.getString(PERMISSIONS);
					if (isFile && !isDirectory && permissions!=null && permissions.startsWith("r")) {
						Table table = df.createTable();
						table.setType(TableType.Table);
						table.setVendorType(ApacheDrillVendorType.FILE);
						table.setName(tname);
						table.setDescription("this is a file");
						tables.add(table);
					}
				} 
			} catch (SQLException e) {
				// boom
			} finally {
				if (stat!=null) {
					try {
						stat.close();
					} catch (SQLException e) {
						// double boom
					}
				}
			}
		}
		//
		return tables;
	}

	/* (non-Javadoc)
	 * @see com.squid.core.database.metadata.VendorMetadataSupportExt#getColumns(java.sql.Connection, com.squid.core.database.model.Table)
	 */
	@Override
	public List<ColumnData> getColumns(Connection conn, Table table) throws SQLException {
		if (table.getVendorType()!=null && table.getVendorType().equals(ApacheDrillVendorType.FILE)) {
			// use alternate method
			return getFileColumns(conn, table);
		} else {
			return getColumns(conn, table.getCatalog(), table.getSchema().getName(), table.getName(), null);
		}
	}

	/**
	 * @param conn
	 * @param table
	 * @return
	 * @throws SQLException 
	 */
	private List<ColumnData> getFileColumns(Connection conn, Table table) throws SQLException {
		Statement stat = conn.createStatement();
		ResultSet res = null;
		try {
			/* for testing
			res = stat.executeQuery("describe `"+table.getSchema().getName()+"`.`"+table.getName()+"`");
			while (res.next()) {
				String col = res.getString(1);
				String type = res.getString(2);
				logger.info(col);
			}
			res.close();
			*/
			//
			res = stat.executeQuery("select * from `"+table.getSchema().getName()+"`.`"+table.getName()+"` limit 0");
			ResultSetMetaData metadata = res.getMetaData();
			List<ColumnData> columns = new ArrayList<>();
			for (int i=1; i<=metadata.getColumnCount(); i++) {
				ColumnData data = new ColumnData();
				data.column_name = metadata.getColumnName(i);
				data.table_name = table.getName();
				data.data_type = metadata.getColumnType(i);
				data.column_size = metadata.getPrecision(i);
				data.decimal_digits = metadata.getScale(i);
				int is_nullable = metadata.isNullable(i);
				data.is_nullable = (is_nullable==ResultSetMetaData.columnNullable || is_nullable==ResultSetMetaData.columnNullableUnknown)?"YES":"NO";
				columns.add(data);
			}
			return columns;
		} finally {
			if (res!=null) {
				try {
					res.close();
					stat.close();
				} catch (SQLException e) {
					// ignore
				}
			}
		}
	}

}
