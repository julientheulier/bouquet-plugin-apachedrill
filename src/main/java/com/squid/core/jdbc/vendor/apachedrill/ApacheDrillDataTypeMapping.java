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

import java.sql.Types;

public class ApacheDrillDataTypeMapping {


	
	public static  String getJavaDatatype(int colType){

		switch(colType){
			case Types.CHAR :  
			case Types.VARCHAR : 
			case Types.LONGVARCHAR : 
				return "java.lang.String";  
	
			case Types.NUMERIC:  
			case Types.DECIMAL :  
				return "java.math.BigDecimal";
	
			case Types.BIT : 
				return "boolean";  
	
			case Types.TINYINT: 	 	
				return "byte"; 
	
			case Types.SMALLINT : 
			return "short";  
		
			case Types.INTEGER :  
				return "int"; 
	
			case Types.BIGINT :  
				return "long" ;  
		
			case Types.REAL : 
				return "float" ;
	
			case Types.FLOAT:  
			case Types.DOUBLE:  
				return "double";  
	
			case Types.BINARY: 	 
			case Types.VARBINARY :  
			case Types.LONGVARBINARY :  
				return "byte[]";  
	
			case Types.DATE :  
				return "java.sql.Date"; 
	
			case Types.TIME :  
				return "java.sql.Time";  
	
			case Types.TIMESTAMP :  
				return "java.sql.Timestamp";  
	
			default :
				return null;
		}
	}
	
	
	public static boolean isPrimitiveType(int colType) {

		switch(colType){

			case Types.BIT : 
			case Types.TINYINT: 	 	
			case Types.SMALLINT : 
			case Types.INTEGER :  
			case Types.BIGINT :  
			case Types.REAL : 
			case Types.FLOAT:  
			case Types.DOUBLE:  
			case Types.BINARY: 	 
			case Types.VARBINARY :  
			case Types.LONGVARBINARY :  
				return true;  
			
			case Types.CHAR :  
			case Types.VARCHAR : 
			case Types.LONGVARCHAR : 
			case Types.NUMERIC:  
			case Types.DECIMAL :  
			case Types.DATE :  
			case Types.TIME :  
			case Types.TIMESTAMP :  
				return false;  
	
			default :
				return false;
		}
	}
	
}
