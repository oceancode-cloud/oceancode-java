package com.oceancode.cloud.common.handler.domain;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CharArrayTypeHandler extends BaseTypeHandler<char[]> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, char[] parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, new String(parameter));
    }

    @Override
    public char[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String data = rs.getString(columnName);
        return data != null ? data.toCharArray() : null;
    }

    @Override
    public char[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String data = rs.getString(columnIndex);
        return data != null ? data.toCharArray() : null;
    }

    @Override
    public char[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String data = cs.getString(columnIndex);
        return data != null ? data.toCharArray() : null;
    }
}