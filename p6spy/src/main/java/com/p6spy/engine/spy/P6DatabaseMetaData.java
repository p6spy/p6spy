/*
 *
 * ====================================================================
 *
 * The P6Spy Software License, Version 1.1
 *
 * This license is derived and fully compatible with the Apache Software
 * license, see http://www.apache.org/LICENSE.txt
 *
 * Copyright (c) 2001-2002 Andy Martin, Ph.D. and Jeff Goke
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 * any, must include the following acknowlegement:
 * "The original concept and code base for P6Spy was conceived
 * and developed by Andy Martin, Ph.D. who generously contribued
 * the first complete release to the public under this license.
 * This product was due to the pioneering work of Andy
 * that began in December of 1995 developing applications that could
 * seamlessly be deployed with minimal effort but with dramatic results.
 * This code is maintained and extended by Jeff Goke and with the ideas
 * and contributions of other P6Spy contributors.
 * (http://www.p6spy.com)"
 * Alternately, this acknowlegement may appear in the software itself,
 * if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "P6Spy", "Jeff Goke", and "Andy Martin" must not be used
 * to endorse or promote products derived from this software without
 * prior written permission. For written permission, please contact
 * license@p6spy.com.
 *
 * 5. Products derived from this software may not be called "P6Spy"
 * nor may "P6Spy" appear in their names without prior written
 * permission of Jeff Goke and Andy Martin.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

/**
 * Description: JDBC Driver Extension implementing PreparedStatement.
 *
 * $Author: cheechq $
 * $Revision: 1.4 $
 * $Date: 2003/06/03 19:20:25 $
 *
 * $Id: P6DatabaseMetaData.java,v 1.4 2003/06/03 19:20:25 cheechq Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/spy/P6DatabaseMetaData.java,v $
 * $Log: P6DatabaseMetaData.java,v $
 * Revision 1.4  2003/06/03 19:20:25  cheechq
 * removed unused imports
 *
 * Revision 1.3  2003/01/03 20:33:42  aarvesen
 * Added getJDBC() method to return the underlying jdbc object.
 *
 * Revision 1.2  2002/12/06 22:42:11  aarvesen
 * Extend P6Base.
 * New factory registration in the constructor.
 * jdk 1.4 additions
 *
 * Revision 1.1  2002/05/24 07:31:13  jeffgoke
 * version 1 rewrite
 *
 * Revision 1.1  2002/04/10 04:24:26  jeffgoke
 * added support for callable statements and fixed numerous bugs that allowed the real class to be returned
 *
 *
 */

package com.p6spy.engine.spy;

import java.sql.*;

public class P6DatabaseMetaData extends P6Base implements java.sql.DatabaseMetaData {


    protected DatabaseMetaData passthru;
    protected P6Connection connection;

    public P6DatabaseMetaData(P6Factory factory, DatabaseMetaData metadata, P6Connection connection) {
        super(factory);
        this.passthru = metadata;
        this.connection = connection;
    }

    public boolean allProceduresAreCallable() throws java.sql.SQLException {
        return passthru.allProceduresAreCallable();
    }

    public boolean allTablesAreSelectable() throws java.sql.SQLException {
        return passthru.allTablesAreSelectable();
    }

    public boolean dataDefinitionCausesTransactionCommit() throws java.sql.SQLException {
        return passthru.dataDefinitionCausesTransactionCommit();
    }

    public boolean dataDefinitionIgnoredInTransactions() throws java.sql.SQLException {
        return passthru.dataDefinitionIgnoredInTransactions();
    }

    public boolean deletesAreDetected(int p0) throws java.sql.SQLException {
        return passthru.deletesAreDetected(p0);
    }

    public boolean doesMaxRowSizeIncludeBlobs() throws java.sql.SQLException {
        return passthru.doesMaxRowSizeIncludeBlobs();
    }

    public java.sql.ResultSet getBestRowIdentifier(String p0, String p1, String p2, int p3, boolean p4) throws java.sql.SQLException {
        return passthru.getBestRowIdentifier(p0,p1,p2,p3,p4);
    }

    public String getCatalogSeparator() throws java.sql.SQLException {
        return passthru.getCatalogSeparator();
    }

    public String getCatalogTerm() throws java.sql.SQLException {
        return passthru.getCatalogTerm();
    }

    public java.sql.ResultSet getCatalogs() throws java.sql.SQLException {
        return passthru.getCatalogs();
    }

    public java.sql.ResultSet getColumnPrivileges(String p0, String p1, String p2, String p3) throws java.sql.SQLException {
        return passthru.getColumnPrivileges(p0,p1,p2,p3);
    }

    public java.sql.ResultSet getColumns(String p0, String p1, String p2, String p3) throws java.sql.SQLException {
        return passthru.getColumns(p0,p1,p2,p3);
    }

    public java.sql.Connection getConnection() throws java.sql.SQLException {
        // return our p6connection
        return connection;
    }

    public java.sql.ResultSet getCrossReference(String p0, String p1, String p2, String p3, String p4, String p5) throws java.sql.SQLException {
        return passthru.getCrossReference(p0,p1,p2,p3,p4,p5);
    }

    public String getDatabaseProductName() throws java.sql.SQLException {
        return passthru.getDatabaseProductName();
    }

    public String getDatabaseProductVersion() throws java.sql.SQLException {
        return passthru.getDatabaseProductVersion();
    }

    public int getDefaultTransactionIsolation() throws java.sql.SQLException {
        return passthru.getDefaultTransactionIsolation();
    }

    public int getDriverMajorVersion() {
        return passthru.getDriverMajorVersion();
    }

    public int getDriverMinorVersion() {
        return passthru.getDriverMinorVersion();
    }

    public String getDriverName() throws java.sql.SQLException {
        return passthru.getDriverName();
    }

    public String getDriverVersion() throws java.sql.SQLException {
        return passthru.getDriverVersion();
    }

    public java.sql.ResultSet getExportedKeys(String p0, String p1, String p2) throws java.sql.SQLException {
        return passthru.getExportedKeys(p0,p1,p2);
    }

    public String getExtraNameCharacters() throws java.sql.SQLException {
        return passthru.getExtraNameCharacters();
    }

    public String getIdentifierQuoteString() throws java.sql.SQLException {
        return passthru.getIdentifierQuoteString();
    }

    public java.sql.ResultSet getImportedKeys(String p0, String p1, String p2) throws java.sql.SQLException {
        return passthru.getImportedKeys(p0,p1,p2);
    }

    public java.sql.ResultSet getIndexInfo(String p0, String p1, String p2, boolean p3, boolean p4) throws java.sql.SQLException {
        return passthru.getIndexInfo(p0,p1,p2,p3,p4);
    }

    public int getMaxBinaryLiteralLength() throws java.sql.SQLException {
        return passthru.getMaxBinaryLiteralLength();
    }

    public int getMaxCatalogNameLength() throws java.sql.SQLException {
        return passthru.getMaxCatalogNameLength();
    }

    public int getMaxCharLiteralLength() throws java.sql.SQLException {
        return passthru.getMaxCharLiteralLength();
    }

    public int getMaxColumnNameLength() throws java.sql.SQLException {
        return passthru.getMaxColumnNameLength();
    }

    public int getMaxColumnsInGroupBy() throws java.sql.SQLException {
        return passthru.getMaxColumnsInGroupBy();
    }

    public int getMaxColumnsInIndex() throws java.sql.SQLException {
        return passthru.getMaxColumnsInIndex();
    }

    public int getMaxColumnsInOrderBy() throws java.sql.SQLException {
        return passthru.getMaxColumnsInOrderBy();
    }

    public int getMaxColumnsInSelect() throws java.sql.SQLException {
        return passthru.getMaxColumnsInSelect();
    }

    public int getMaxColumnsInTable() throws java.sql.SQLException {
        return passthru.getMaxColumnsInTable();
    }

    public int getMaxConnections() throws java.sql.SQLException {
        return passthru.getMaxConnections();
    }

    public int getMaxCursorNameLength() throws java.sql.SQLException {
        return passthru.getMaxCursorNameLength();
    }

    public int getMaxIndexLength() throws java.sql.SQLException {
        return passthru.getMaxIndexLength();
    }

    public int getMaxProcedureNameLength() throws java.sql.SQLException {
        return passthru.getMaxProcedureNameLength();
    }

    public int getMaxRowSize() throws java.sql.SQLException {
        return passthru.getMaxRowSize();
    }

    public int getMaxSchemaNameLength() throws java.sql.SQLException {
        return passthru.getMaxSchemaNameLength();
    }

    public int getMaxStatementLength() throws java.sql.SQLException {
        return passthru.getMaxStatementLength();
    }

    public int getMaxStatements() throws java.sql.SQLException {
        return passthru.getMaxStatements();
    }

    public int getMaxTableNameLength() throws java.sql.SQLException {
        return passthru.getMaxTableNameLength();
    }

    public int getMaxTablesInSelect() throws java.sql.SQLException {
        return passthru.getMaxTablesInSelect();
    }

    public int getMaxUserNameLength() throws java.sql.SQLException {
        return passthru.getMaxUserNameLength();
    }

    public String getNumericFunctions() throws java.sql.SQLException {
        return passthru.getNumericFunctions();
    }

    public java.sql.ResultSet getPrimaryKeys(String p0, String p1, String p2) throws java.sql.SQLException {
        return passthru.getPrimaryKeys(p0,p1,p2);
    }

    public java.sql.ResultSet getProcedureColumns(String p0, String p1, String p2, String p3) throws java.sql.SQLException {
        return passthru.getProcedureColumns(p0,p1,p2,p3);
    }

    public String getProcedureTerm() throws java.sql.SQLException {
        return passthru.getProcedureTerm();
    }

    public java.sql.ResultSet getProcedures(String p0, String p1, String p2) throws java.sql.SQLException {
        return passthru.getProcedures(p0,p1,p2);
    }

    public String getSQLKeywords() throws java.sql.SQLException {
        return passthru.getSQLKeywords();
    }

    public String getSchemaTerm() throws java.sql.SQLException {
        return passthru.getSchemaTerm();
    }

    public java.sql.ResultSet getSchemas() throws java.sql.SQLException {
        return passthru.getSchemas();
    }

    public String getSearchStringEscape() throws java.sql.SQLException {
        return passthru.getSearchStringEscape();
    }

    public String getStringFunctions() throws java.sql.SQLException {
        return passthru.getStringFunctions();
    }

    public String getSystemFunctions() throws java.sql.SQLException {
        return passthru.getSystemFunctions();
    }

    public java.sql.ResultSet getTablePrivileges(String p0, String p1, String p2) throws java.sql.SQLException {
        return passthru.getTablePrivileges(p0,p1,p2);
    }

    public java.sql.ResultSet getTableTypes() throws java.sql.SQLException {
        return passthru.getTableTypes();
    }

    public java.sql.ResultSet getTables(String p0, String p1, String p2, String[] p3) throws java.sql.SQLException {
        return passthru.getTables(p0,p1,p2,p3);
    }

    public String getTimeDateFunctions() throws java.sql.SQLException {
        return passthru.getTimeDateFunctions();
    }

    public java.sql.ResultSet getTypeInfo() throws java.sql.SQLException {
        return passthru.getTypeInfo();
    }

    public java.sql.ResultSet getUDTs(String p0, String p1, String p2, int[] p3) throws java.sql.SQLException {
        return passthru.getUDTs(p0,p1,p2,p3);
    }

    public String getURL() throws java.sql.SQLException {
        return passthru.getURL();
    }

    public String getUserName() throws java.sql.SQLException {
        return passthru.getUserName();
    }

    public java.sql.ResultSet getVersionColumns(String p0, String p1, String p2) throws java.sql.SQLException {
        return passthru.getVersionColumns(p0,p1,p2);
    }

    public boolean insertsAreDetected(int p0) throws java.sql.SQLException {
        return passthru.insertsAreDetected(p0);
    }

    public boolean isCatalogAtStart() throws java.sql.SQLException {
        return passthru.isCatalogAtStart();
    }

    public boolean isReadOnly() throws java.sql.SQLException {
        return passthru.isReadOnly();
    }

    public boolean nullPlusNonNullIsNull() throws java.sql.SQLException {
        return passthru.nullPlusNonNullIsNull();
    }

    public boolean nullsAreSortedAtEnd() throws java.sql.SQLException {
        return passthru.nullsAreSortedAtEnd();
    }

    public boolean nullsAreSortedAtStart() throws java.sql.SQLException {
        return passthru.nullsAreSortedAtStart();
    }

    public boolean nullsAreSortedHigh() throws java.sql.SQLException {
        return passthru.nullsAreSortedHigh();
    }

    public boolean nullsAreSortedLow() throws java.sql.SQLException {
        return passthru.nullsAreSortedLow();
    }

    public boolean othersDeletesAreVisible(int p0) throws java.sql.SQLException {
        return passthru.othersDeletesAreVisible(p0);
    }

    public boolean othersInsertsAreVisible(int p0) throws java.sql.SQLException {
        return passthru.othersInsertsAreVisible(p0);
    }

    public boolean othersUpdatesAreVisible(int p0) throws java.sql.SQLException {
        return passthru.othersUpdatesAreVisible(p0);
    }

    public boolean ownDeletesAreVisible(int p0) throws java.sql.SQLException {
        return passthru.ownDeletesAreVisible(p0);
    }

    public boolean ownInsertsAreVisible(int p0) throws java.sql.SQLException {
        return passthru.ownInsertsAreVisible(p0);
    }

    public boolean ownUpdatesAreVisible(int p0) throws java.sql.SQLException {
        return passthru.ownUpdatesAreVisible(p0);
    }

    public boolean storesLowerCaseIdentifiers() throws java.sql.SQLException {
        return passthru.storesLowerCaseIdentifiers();
    }

    public boolean storesLowerCaseQuotedIdentifiers() throws java.sql.SQLException {
        return passthru.storesLowerCaseQuotedIdentifiers();
    }

    public boolean storesMixedCaseIdentifiers() throws java.sql.SQLException {
        return passthru.storesMixedCaseIdentifiers();
    }

    public boolean storesMixedCaseQuotedIdentifiers() throws java.sql.SQLException {
        return passthru.storesMixedCaseQuotedIdentifiers();
    }

    public boolean storesUpperCaseIdentifiers() throws java.sql.SQLException {
        return passthru.storesUpperCaseIdentifiers();
    }

    public boolean storesUpperCaseQuotedIdentifiers() throws java.sql.SQLException {
        return passthru.storesUpperCaseQuotedIdentifiers();
    }

    public boolean supportsANSI92EntryLevelSQL() throws java.sql.SQLException {
        return passthru.supportsANSI92EntryLevelSQL();
    }

    public boolean supportsANSI92FullSQL() throws java.sql.SQLException {
        return passthru.supportsANSI92FullSQL();
    }

    public boolean supportsANSI92IntermediateSQL() throws java.sql.SQLException {
        return passthru.supportsANSI92IntermediateSQL();
    }

    public boolean supportsAlterTableWithAddColumn() throws java.sql.SQLException {
        return passthru.supportsAlterTableWithAddColumn();
    }

    public boolean supportsAlterTableWithDropColumn() throws java.sql.SQLException {
        return passthru.supportsAlterTableWithDropColumn();
    }

    public boolean supportsBatchUpdates() throws java.sql.SQLException {
        return passthru.supportsBatchUpdates();
    }

    public boolean supportsCatalogsInDataManipulation() throws java.sql.SQLException {
        return passthru.supportsCatalogsInDataManipulation();
    }

    public boolean supportsCatalogsInIndexDefinitions() throws java.sql.SQLException {
        return passthru.supportsCatalogsInIndexDefinitions();
    }

    public boolean supportsCatalogsInPrivilegeDefinitions() throws java.sql.SQLException {
        return passthru.supportsCatalogsInPrivilegeDefinitions();
    }

    public boolean supportsCatalogsInProcedureCalls() throws java.sql.SQLException {
        return passthru.supportsCatalogsInProcedureCalls();
    }

    public boolean supportsCatalogsInTableDefinitions() throws java.sql.SQLException {
        return passthru.supportsCatalogsInTableDefinitions();
    }

    public boolean supportsColumnAliasing() throws java.sql.SQLException {
        return passthru.supportsColumnAliasing();
    }

    public boolean supportsConvert() throws java.sql.SQLException {
        return passthru.supportsConvert();
    }

    public boolean supportsConvert(int p0, int p1) throws java.sql.SQLException {
        return passthru.supportsConvert(p0,p1);
    }

    public boolean supportsCoreSQLGrammar() throws java.sql.SQLException {
        return passthru.supportsCoreSQLGrammar();
    }

    public boolean supportsCorrelatedSubqueries() throws java.sql.SQLException {
        return passthru.supportsCorrelatedSubqueries();
    }

    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws java.sql.SQLException {
        return passthru.supportsDataDefinitionAndDataManipulationTransactions();
    }

    public boolean supportsDataManipulationTransactionsOnly() throws java.sql.SQLException {
        return passthru.supportsDataManipulationTransactionsOnly();
    }

    public boolean supportsDifferentTableCorrelationNames() throws java.sql.SQLException {
        return passthru.supportsDifferentTableCorrelationNames();
    }

    public boolean supportsExpressionsInOrderBy() throws java.sql.SQLException {
        return passthru.supportsExpressionsInOrderBy();
    }

    public boolean supportsExtendedSQLGrammar() throws java.sql.SQLException {
        return passthru.supportsExtendedSQLGrammar();
    }

    public boolean supportsFullOuterJoins() throws java.sql.SQLException {
        return passthru.supportsFullOuterJoins();
    }

    public boolean supportsGroupBy() throws java.sql.SQLException {
        return passthru.supportsGroupBy();
    }

    public boolean supportsGroupByBeyondSelect() throws java.sql.SQLException {
        return passthru.supportsGroupByBeyondSelect();
    }

    public boolean supportsGroupByUnrelated() throws java.sql.SQLException {
        return passthru.supportsGroupByUnrelated();
    }

    public boolean supportsIntegrityEnhancementFacility() throws java.sql.SQLException {
        return passthru.supportsIntegrityEnhancementFacility();
    }

    public boolean supportsLikeEscapeClause() throws java.sql.SQLException {
        return passthru.supportsLikeEscapeClause();
    }

    public boolean supportsLimitedOuterJoins() throws java.sql.SQLException {
        return passthru.supportsLimitedOuterJoins();
    }

    public boolean supportsMinimumSQLGrammar() throws java.sql.SQLException {
        return passthru.supportsMinimumSQLGrammar();
    }

    public boolean supportsMixedCaseIdentifiers() throws java.sql.SQLException {
        return passthru.supportsMixedCaseIdentifiers();
    }

    public boolean supportsMixedCaseQuotedIdentifiers() throws java.sql.SQLException {
        return passthru.supportsMixedCaseQuotedIdentifiers();
    }

    public boolean supportsMultipleResultSets() throws java.sql.SQLException {
        return passthru.supportsMultipleResultSets();
    }

    public boolean supportsMultipleTransactions() throws java.sql.SQLException {
        return passthru.supportsMultipleTransactions();
    }

    public boolean supportsNonNullableColumns() throws java.sql.SQLException {
        return passthru.supportsNonNullableColumns();
    }

    public boolean supportsOpenCursorsAcrossCommit() throws java.sql.SQLException {
        return passthru.supportsOpenCursorsAcrossCommit();
    }

    public boolean supportsOpenCursorsAcrossRollback() throws java.sql.SQLException {
        return passthru.supportsOpenCursorsAcrossRollback();
    }

    public boolean supportsOpenStatementsAcrossCommit() throws java.sql.SQLException {
        return passthru.supportsOpenStatementsAcrossCommit();
    }

    public boolean supportsOpenStatementsAcrossRollback() throws java.sql.SQLException {
        return passthru.supportsOpenStatementsAcrossRollback();
    }

    public boolean supportsOrderByUnrelated() throws java.sql.SQLException {
        return passthru.supportsOrderByUnrelated();
    }

    public boolean supportsOuterJoins() throws java.sql.SQLException {
        return passthru.supportsOuterJoins();
    }

    public boolean supportsPositionedDelete() throws java.sql.SQLException {
        return passthru.supportsPositionedDelete();
    }

    public boolean supportsPositionedUpdate() throws java.sql.SQLException {
        return passthru.supportsPositionedUpdate();
    }

    public boolean supportsResultSetConcurrency(int p0, int p1) throws java.sql.SQLException {
        return passthru.supportsResultSetConcurrency(p0,p1);
    }

    public boolean supportsResultSetType(int p0) throws java.sql.SQLException {
        return passthru.supportsResultSetType(p0);
    }

    public boolean supportsSchemasInDataManipulation() throws java.sql.SQLException {
        return passthru.supportsSchemasInDataManipulation();
    }

    public boolean supportsSchemasInIndexDefinitions() throws java.sql.SQLException {
        return passthru.supportsSchemasInIndexDefinitions();
    }

    public boolean supportsSchemasInPrivilegeDefinitions() throws java.sql.SQLException {
        return passthru.supportsSchemasInPrivilegeDefinitions();
    }

    public boolean supportsSchemasInProcedureCalls() throws java.sql.SQLException {
        return passthru.supportsSchemasInProcedureCalls();
    }

    public boolean supportsSchemasInTableDefinitions() throws java.sql.SQLException {
        return passthru.supportsSchemasInTableDefinitions();
    }

    public boolean supportsSelectForUpdate() throws java.sql.SQLException {
        return passthru.supportsSelectForUpdate();
    }

    public boolean supportsStoredProcedures() throws java.sql.SQLException {
        return passthru.supportsStoredProcedures();
    }

    public boolean supportsSubqueriesInComparisons() throws java.sql.SQLException {
        return passthru.supportsSubqueriesInComparisons();
    }

    public boolean supportsSubqueriesInExists() throws java.sql.SQLException {
        return passthru.supportsSubqueriesInExists();
    }

    public boolean supportsSubqueriesInIns() throws java.sql.SQLException {
        return passthru.supportsSubqueriesInIns();
    }

    public boolean supportsSubqueriesInQuantifieds() throws java.sql.SQLException {
        return passthru.supportsSubqueriesInQuantifieds();
    }

    public boolean supportsTableCorrelationNames() throws java.sql.SQLException {
        return passthru.supportsTableCorrelationNames();
    }

    public boolean supportsTransactionIsolationLevel(int p0) throws java.sql.SQLException {
        return passthru.supportsTransactionIsolationLevel(p0);
    }

    public boolean supportsTransactions() throws java.sql.SQLException {
        return passthru.supportsTransactions();
    }

    public boolean supportsUnion() throws java.sql.SQLException {
        return passthru.supportsUnion();
    }

    public boolean supportsUnionAll() throws java.sql.SQLException {
        return passthru.supportsUnionAll();
    }

    public boolean updatesAreDetected(int p0) throws java.sql.SQLException {
        return passthru.updatesAreDetected(p0);
    }

    public boolean usesLocalFilePerTable() throws java.sql.SQLException {
        return passthru.usesLocalFilePerTable();
    }

    public boolean usesLocalFiles() throws java.sql.SQLException {
        return passthru.usesLocalFiles();
    }

    // Since JDK 1.4
    public boolean supportsSavepoints() throws java.sql.SQLException {
        return passthru.supportsSavepoints();
    }

    // Since JDK 1.4
    public boolean supportsNamedParameters() throws java.sql.SQLException {
        return passthru.supportsNamedParameters();
    }

    // Since JDK 1.4
    public boolean supportsMultipleOpenResults() throws java.sql.SQLException {
        return passthru.supportsMultipleOpenResults();
    }

    // Since JDK 1.4
    public boolean supportsGetGeneratedKeys() throws java.sql.SQLException {
        return passthru.supportsGetGeneratedKeys();
    }

    // Since JDK 1.4
    public java.sql.ResultSet getSuperTypes(String p0, String p1, String p2) throws java.sql.SQLException {
        return passthru.getSuperTypes(p0, p1, p2);
    }

    // Since JDK 1.4
    public java.sql.ResultSet getSuperTables(String p0, String p1, String p2) throws java.sql.SQLException {
        return passthru.getSuperTables(p0, p1, p2);
    }

    // Since JDK 1.4
    public java.sql.ResultSet getAttributes(String p0, String p1, String p2, String p3) throws java.sql.SQLException {
        return passthru.getAttributes(p0, p1, p2, p3);
    }

    // Since JDK 1.4
    public boolean supportsResultSetHoldability(int p0) throws java.sql.SQLException {
        return passthru.supportsResultSetHoldability(p0);
    }

    // Since JDK 1.4
    public int getResultSetHoldability() throws java.sql.SQLException {
        return passthru.getResultSetHoldability();
    }

    // Since JDK 1.4
    public int getDatabaseMajorVersion() throws java.sql.SQLException {
        return passthru.getDatabaseMajorVersion();
    }

    // Since JDK 1.4
    public int getDatabaseMinorVersion() throws java.sql.SQLException {
        return passthru.getDatabaseMinorVersion();
    }

    // Since JDK 1.4
    public int getJDBCMajorVersion() throws java.sql.SQLException {
        return passthru.getJDBCMajorVersion();
    }

    // Since JDK 1.4
    public int getJDBCMinorVersion() throws java.sql.SQLException {
        return passthru.getJDBCMinorVersion();
    }

    // Since JDK 1.4
    public int getSQLStateType() throws java.sql.SQLException {
        return passthru.getSQLStateType();
    }

    // Since JDK 1.4
    public boolean locatorsUpdateCopy() throws java.sql.SQLException {
        return passthru.locatorsUpdateCopy();
    }

    // Since JDK 1.4
    public boolean supportsStatementPooling() throws java.sql.SQLException {
        return passthru.supportsStatementPooling();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.DatabaseMetaData#autoCommitFailureClosesAllResultSets()
     */
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        return passthru.autoCommitFailureClosesAllResultSets();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.DatabaseMetaData#getClientInfoProperties()
     */
    public ResultSet getClientInfoProperties() throws SQLException {
        return passthru.getClientInfoProperties();
    }

    /**
     * @param catalog
     * @param schemaPattern
     * @param functionNamePattern
     * @param columnNamePattern
     * @return
     * @throws SQLException
     * @see java.sql.DatabaseMetaData#getFunctionColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
        return passthru.getFunctionColumns(catalog, schemaPattern, functionNamePattern, columnNamePattern);
    }

    /**
     * @param catalog
     * @param schemaPattern
     * @param functionNamePattern
     * @return
     * @throws SQLException
     * @see java.sql.DatabaseMetaData#getFunctions(java.lang.String, java.lang.String, java.lang.String)
     */
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
        return passthru.getFunctions(catalog, schemaPattern, functionNamePattern);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.DatabaseMetaData#getRowIdLifetime()
     */
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        return passthru.getRowIdLifetime();
    }

    /**
     * @param catalog
     * @param schemaPattern
     * @return
     * @throws SQLException
     * @see java.sql.DatabaseMetaData#getSchemas(java.lang.String, java.lang.String)
     */
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        return passthru.getSchemas(catalog, schemaPattern);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.DatabaseMetaData#supportsStoredFunctionsUsingCallSyntax()
     */
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        return passthru.supportsStoredFunctionsUsingCallSyntax();
    }

    /**
     * @param iface
     * @return
     * @throws SQLException
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return passthru.isWrapperFor(iface);
    }

    /**
     * @param <T>
     * @param iface
     * @return
     * @throws SQLException
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return passthru.unwrap(iface);
    }

    /**
     * Returns the underlying JDBC object (in this case, a
     * java.sql.DatabaseMetaData)
     * @return the wrapped JDBC object
     */
    public DatabaseMetaData getJDBC() {
	DatabaseMetaData wrapped = (passthru instanceof P6DatabaseMetaData) ?
	    ((P6DatabaseMetaData) passthru).getJDBC() :
	    passthru;

	return wrapped;
    }

    // since 1.7
    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        return passthru.generatedKeyAlwaysReturned();
    }

    // since 1.7
    @Override
    public ResultSet getPseudoColumns(final String catalog, final String schemaPattern, final String tableNamePattern, final String columnNamePattern)
        throws SQLException {
        return passthru.getPseudoColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
    }
}
