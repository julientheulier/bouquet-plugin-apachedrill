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
package com.squid.core.jdbc.vendor.apachedrill.render;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.core.database.impl.DataSourceReliable;
import com.squid.core.database.metadata.IMetadataEngine;
import com.squid.core.database.model.DatabaseProduct;
import com.squid.core.domain.extensions.date.AddMonthsOperatorDefinition;
import com.squid.core.domain.extensions.cast.CastOperatorDefinition;
import com.squid.core.domain.extensions.date.operator.DateOperatorDefinition;
import com.squid.core.domain.extensions.date.DateTruncateOperatorDefinition;
import com.squid.core.domain.extensions.date.extract.ExtractOperatorDefinition;
import com.squid.core.domain.extensions.string.PosStringOperatorDefinition;
import com.squid.core.domain.extensions.string.SubstringOperatorDefinition;
import com.squid.core.domain.maths.RandOperatorDefinition;
import com.squid.core.domain.maths.SinhCoshTanhOperatorDefintion;
import com.squid.core.domain.maths.TruncateOperatorDefintion;
import com.squid.core.domain.operators.IntrinsicOperators;
import com.squid.core.domain.operators.OperatorDefinition;
import com.squid.core.sql.db.features.IGroupingSetSupport;
import com.squid.core.sql.db.features.IRollupStrategySupport;
import com.squid.core.sql.db.render.AddMonthsAsIntervalOperatorRenderer;
import com.squid.core.sql.db.render.DateAddSubOperatorRenderer;
import com.squid.core.sql.db.render.DateEpochOperatorRenderer;
import com.squid.core.sql.db.render.ExtractAsFunctionOperatorRenderer;
import com.squid.core.sql.db.render.MetatdataSearchFeatureSupport;
import com.squid.core.sql.db.templates.DefaultJDBCSkin;
import com.squid.core.sql.db.templates.DefaultSkinProvider;
import com.squid.core.sql.db.templates.ISkinProvider;
import com.squid.core.sql.db.templates.SkinRegistry;
import com.squid.core.sql.render.ISkinFeatureSupport;
import com.squid.core.sql.render.SQLSkin;
import com.squid.core.sql.render.ZeroIfNullFeatureSupport;
import com.squid.core.sql.statements.SelectStatement;



public class ApacheDrillSkinProvider extends DefaultSkinProvider
{
	static final Logger logger = LoggerFactory.getLogger(ApacheDrillSkinProvider.class);

	private static final ZeroIfNullFeatureSupport zeroIfNull = new ANSIZeroIfNullFeatureSupport();

	public ApacheDrillSkinProvider() {
				
		//
		registerOperatorRender(OperatorDefinition.getExtendedId(IntrinsicOperators.DIVIDE), new ApacheDrillDivideOperatorRenderer());
		//
		registerOperatorRender("com.sodad.domain.operator.density.EQWBUCKET", new EquiWidthBucketRenderer ());
		registerOperatorRender(PosStringOperatorDefinition.STRING_POSITION, new PosStringRenderer());
		registerOperatorRender(SubstringOperatorDefinition.STRING_SUBSTRING, new SubStringRenderer());
		registerOperatorRender(DateOperatorDefinition.DATE_MONTHS_BETWEEN, new MonthsBetweenRenderer()); //TODO refactor

		registerOperatorRender(CastOperatorDefinition.TO_CHAR, new ApacheDrillCastOperatorRenderer());
		registerOperatorRender(CastOperatorDefinition.TO_DATE, new ApacheDrillCastOperatorRenderer()); //TODO test
		//registerOperatorRender(CastOperatorDefinition.TO_NUMBER, new ApacheDrillCastOperatorRenderer()); //DOES NOT EXIST DECIMAL
		registerOperatorRender(CastOperatorDefinition.TO_INTEGER, new ApacheDrillCastOperatorRenderer());
		registerOperatorRender(CastOperatorDefinition.TO_TIMESTAMP, new ApacheDrillCastOperatorRenderer()); //TODO test 
		//registerOperatorRender(DateOperatorDefinition.DATE_INTERVAL, new ApacheDrillDateIntervalOperatorRenderer()); //TODO test
		registerOperatorRender(DateOperatorDefinition.DATE_ADD, new ApacheDrillDateAddSubOperatorRenderer(DateAddSubOperatorRenderer.OperatorType.ADD)); //TODO test
		registerOperatorRender(DateOperatorDefinition.DATE_SUB, new ApacheDrillDateAddSubOperatorRenderer(DateAddSubOperatorRenderer.OperatorType.ADD)); //TODO test
		registerOperatorRender(ExtractOperatorDefinition.EXTRACT_DAY_OF_WEEK, new ApacheDrillDayOfWeekOperatorRenderer("DAYOFWEEK")); //TODO test
		registerOperatorRender(ExtractOperatorDefinition.EXTRACT_DAY_OF_YEAR, new ExtractAsFunctionOperatorRenderer("DAYOFYEAR")); //TODO test
		registerOperatorRender(DateOperatorDefinition.FROM_UNIXTIME, new ApacheDrillDateEpochOperatorRenderer(DateEpochOperatorRenderer.FROM)); //TODO test // Seems OK
		registerOperatorRender(DateOperatorDefinition.TO_UNIXTIME, new ApacheDrillDateEpochOperatorRenderer(DateEpochOperatorRenderer.TO)); //TODO test //Seems OK
		//
		registerOperatorRender(TruncateOperatorDefintion.TRUNCATE, new ApacheDrillTruncateOperatorRenderer()); //TODO test
		registerOperatorRender(RandOperatorDefinition.RAND, new ApacheDrillRandOperatorRenderer()); //TODO test
		registerOperatorRender(SinhCoshTanhOperatorDefintion.SINH, new ApacheDrillSinhOperatorRenderer()); //TODO test
		registerOperatorRender(SinhCoshTanhOperatorDefintion.COSH, new ApacheDrillCoshOperatorRenderer()); //TODO test
		registerOperatorRender(SinhCoshTanhOperatorDefintion.TANH, new ApacheDrillTanhOperatorRenderer()); //TODO test
		//
		registerOperatorRender(AddMonthsOperatorDefinition.ADD_MONTHS, new AddMonthsAsIntervalOperatorRenderer()); //TODO test
		//
		registerOperatorRender(OperatorDefinition.getExtendedId(IntrinsicOperators.AVG),new ApacheDrillAvgRenderer()); //TODO test
		registerOperatorRender(OperatorDefinition.getExtendedId(IntrinsicOperators.VAR_POP),new ApacheDrillVarStdevRenderer()); //TODO test
		registerOperatorRender(OperatorDefinition.getExtendedId(IntrinsicOperators.VAR_SAMP),new ApacheDrillVarStdevRenderer()); //TODO test
		registerOperatorRender(OperatorDefinition.getExtendedId(IntrinsicOperators.STDDEV_POP),new ApacheDrillVarStdevRenderer()); //TODO test
		registerOperatorRender(OperatorDefinition.getExtendedId(IntrinsicOperators.STDDEV_SAMP),new ApacheDrillVarStdevRenderer()); //TODO test
		 
		registerOperatorRender(DateTruncateOperatorDefinition.DATE_TRUNCATE, new ApacheDrillDateTruncateOperatorRenderer()); //TODO test
		//// 

		//
		/*
		 * registerOperatorRender(StringLengthOperatorsDefinition.STRING_LENGTH, new StringLengthRenderer());
		 * registerOperatorRender(TranslateOperatorDefinition.STRING_REPLACE, new TranslateOperatorRenderer("REPLACE"));
		 * registerOperatorRender(TranslateOperatorDefinition.STRING_TRANSLATE, new TranslateOperatorRenderer("TRANSLATE"));
		 * registerOperatorRender(UpperLowerOperatorsDefinition.STRING_UPPER, new UpperLowerOperatorRenderer("UPPER"));
		 * registerOperatorRender(UpperLowerOperatorsDefinition.STRING_LOWER, new UpperLowerOperatorRenderer("LOWER"));
		 * registerOperatorRender(TrimOperatorDefinition.STRING_TRIM, new TrimOperatorRenderer("BOTH"));
		 * registerOperatorRender(TrimOperatorDefinition.STRING_LTRIM, new TrimOperatorRenderer("LEADING"));
		 * registerOperatorRender(TrimOperatorDefinition.STRING_RTRIM, new TrimOperatorRenderer("TRAILING"));
		 * registerOperatorRender(PadOperatorDefinition.STRING_LPAD, new PadOperatorRenderer("LPAD"));
		 * registerOperatorRender(PadOperatorDefinition.STRING_RPAD, new PadOperatorRenderer("RPAD"));
		 //
		  * registerOperatorRender(SortOperatorDefinition.ASC_ID, new SortOperatorRenderer("ASC"));
		  * registerOperatorRender(SortOperatorDefinition.DESC_ID, new SortOperatorRenderer("DESC"));
		  * registerOperatorRender(DateOperatorDefinition.CURRENT_DATE, new CurrentDateTimestampRenderer());
		  * registerOperatorRender(DateOperatorDefinition.CURRENT_TIMESTAMP, new CurrentDateTimestampRenderer());
		  * registerOperatorRender(CeilOperatorDefintion.CEIL, new CeilOperatorRenderer());
		  * registerOperatorRender(FloorOperatorDefintion.FLOOR, new FloorOperatorRenderer());
		  * registerOperatorRender(SignOperatorDefintion.SIGN, new SignOperatorRenderer());
		  * registerOperatorRender(TruncateOperatorDefintion.TRUNCATE, new TruncateOperatorRenderer());
		  * registerOperatorRender(RoundOperatorDefintion.ROUND, new RoundOperatorRenderer());
		  * registerOperatorRender(PowerOperatorDefintion.POWER, new PowerOperatorRenderer());
		  * registerOperatorRender(PiOperatorDefintion.PI, new PiOperatorRenderer());
		  * registerOperatorRender(DegreesOperatorDefintion.DEGREES, new DegreesOperatorRenderer());
		  */
		//
		// NO NUMBER
		// registerOperatorRender(OperatorDefinition.getExtendedId(IntrinsicOperators.COVAR_POP), new CoVarianceRenderer());
		
	
	}

	@Override
	public double computeAccuracy(DatabaseProduct product) {
		try {
			if (product!=null){
				if (IMetadataEngine.APACHEDRILL_NAME.equalsIgnoreCase(product.getProductName())) {
					return PERFECT_MATCH;
				} else {
					return NOT_APPLICABLE;
				}
			}else{
				return NOT_APPLICABLE;
			}
		} catch (Exception e) {
			return NOT_APPLICABLE;
		}
	}

	@Override
	public SQLSkin createSkin(DatabaseProduct product) {
		return new ApacheDrillSkin(this,product);
	}

	@Override
	public ISkinFeatureSupport getFeatureSupport(DefaultJDBCSkin skin, String featureID) {
		if (featureID==ZeroIfNullFeatureSupport.ID) {
			return zeroIfNull;
		} else if (featureID==DataSourceReliable.FeatureSupport.GROUPBY_ALIAS) {
			return ISkinFeatureSupport.IS_SUPPORTED;
		} else if (featureID==SelectStatement.SampleFeatureSupport.SELECT_SAMPLE) {
			return SAMPLE_SUPPORT;
		} else if(featureID == MetatdataSearchFeatureSupport.METADATA_SEARCH_FEATURE_ID){
			return METADATA_SEARCH_SUPPORT;
		} else if (featureID == IGroupingSetSupport.ID) {
			return IGroupingSetSupport.IS_NOT_SUPPORTED;
		} else if(featureID == DataSourceReliable.FeatureSupport.AUTOCOMMIT){
			return ISkinFeatureSupport.IS_SUPPORTED;
		} else if (featureID.equals(IRollupStrategySupport.ID)) {
				return IRollupStrategySupport.DO_NOT_OPTIMIZE_STRATEGY;
		}
		//else
		return super.getFeatureSupport(skin,featureID);
	}

	private SelectStatement.SampleFeatureSupport SAMPLE_SUPPORT =
		new SelectStatement.SampleFeatureSupport() {

		public boolean isCountSupported() {
			return false;
		}

		public boolean isPercentageSupported() {
			return true;
		}

	};

	private MetatdataSearchFeatureSupport METADATA_SEARCH_SUPPORT = new MetatdataSearchFeatureSupport() {
		@Override
		public String createTableSearch(List<String> schemas, String tableName, boolean isCaseSensitive) {
			StringBuilder sqlCode = new StringBuilder();
			sqlCode.append("SELECT TABLE_SCHEMA,TABLE_NAME");
			sqlCode.append(CR_LF);
			sqlCode.append(" FROM INFORMATION_SCHEMA.`TABLES` TABS");
			sqlCode.append(CR_LF);
			sqlCode.append(" WHERE TABLE_SCHEMA IN (" + getGroupSchemaNames(schemas) + ")");
			sqlCode.append(CR_LF);
			sqlCode.append(" AND ("
					+ applyCaseSensitive("TABLE_NAME", isCaseSensitive)
					+ " LIKE " + applyCaseSensitive(tableName, isCaseSensitive)
					+ ") ");
			sqlCode.append(CR_LF);
			sqlCode.append(" ORDER BY 1, 2");
			return sqlCode.toString();
		}

		@Override
		public String createColumnSearch(List<String> schemas, String tableName, String columnName, boolean isCaseSensitive) {
			StringBuilder sqlCode = new StringBuilder();
			sqlCode.append("SELECT TABLE_SCHEMA, TABLE_NAME,COLUMN_NAME");
			sqlCode.append(CR_LF);
			sqlCode.append(" FROM INFORMATION_SCHEMA.COLUMNS COLS");
			sqlCode.append(CR_LF);
			sqlCode.append(" WHERE TABLE_SCHEMA IN (" + getGroupSchemaNames(schemas) + ")");
			sqlCode.append(CR_LF);
			sqlCode.append(" AND ("
					+ applyCaseSensitive("COLUMN_NAME", isCaseSensitive)
					+ " LIKE "
					+ applyCaseSensitive(columnName, isCaseSensitive) + ")");
			sqlCode.append(CR_LF);
			if(tableName != null){
				sqlCode.append(" AND ("
						+ applyCaseSensitive("TABLE_NAME", isCaseSensitive)
						+ " LIKE " + applyCaseSensitive(tableName, isCaseSensitive)
						+ " OR " + applyCaseSensitive("TABLE_NAME", isCaseSensitive)
						+ " LIKE " + applyCaseSensitive(tableName, isCaseSensitive)
						+ ") ");
				sqlCode.append(CR_LF);
			}
			sqlCode.append(" ORDER BY 1, 2,3");
			return sqlCode.toString();
		}

	};

	@Override
	public String getSkinPrefix(DatabaseProduct product) {
		return "apachedrill";
	}

	@Override
	public ISkinProvider getParentSkinProvider() {
		return SkinRegistry.INSTANCE.findSkinProvider(DefaultSkinProvider.class);
	}

}
