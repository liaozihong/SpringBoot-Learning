package com.dashuai.learning.mybatis.freemarker.handler;

import com.dashuai.learning.mybatis.freemarker.utils.JSONParseUtils;
import com.google.common.reflect.TypeToken;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

//@MappedTypes({JSONObject.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class ListJsonHandler extends BaseTypeHandler<List<?>> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int columnIndex, List<?> list, JdbcType jdbcType) throws SQLException {
        ps.setString(columnIndex, JSONParseUtils.object2JsonString(list));
    }

    @Override
    public List getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String sqlJson = resultSet.getString(columnName);
        if (null != sqlJson) {
            return JSONParseUtils.json2GenericObject(sqlJson, new TypeToken<List>() {
            });
        }
        return null;
    }

    @Override
    public List getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String sqlJson = resultSet.getString(columnIndex);
        if (null != sqlJson) {
            return JSONParseUtils.json2GenericObject(sqlJson, new TypeToken<List>() {
            });
        }
        return null;
    }

    @Override
    public List getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        String sqlJson = callableStatement.getString(columnIndex);
        if (null != sqlJson) {
            return JSONParseUtils.json2GenericObject(sqlJson, new TypeToken<List>() {
            });
        }
        return null;
    }
}
