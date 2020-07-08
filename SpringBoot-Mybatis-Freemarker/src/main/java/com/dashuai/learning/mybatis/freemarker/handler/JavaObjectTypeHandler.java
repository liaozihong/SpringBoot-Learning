package com.dashuai.learning.mybatis.freemarker.handler;

import com.dashuai.learning.mybatis.freemarker.utils.JSONParseUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @description 用以mysql中json格式的字段，进行转换的自定义转换器，转换为实体类的JSONObject属性
 * MappedTypes注解中的类代表此转换器可以自动转换为的java对象
 * MappedJdbcTypes注解中设置的是对应的jdbctype
 **/
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JavaObjectTypeHandler<T> extends BaseTypeHandler<T> {

    private Class<T> clazz;

    //这个构造器是用来获取类型的，如果是泛型的typehandler必须要写
    public JavaObjectTypeHandler(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.clazz = clazz;
    }

    //设置非空参数
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSONParseUtils.object2JsonString(parameter));
    }

    //根据列名，获取可以为空的结果
    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String sqlJson = rs.getString(columnName);
        if (null != sqlJson) {
            return JSONParseUtils.json2Object(sqlJson, clazz);
        }
        return null;
    }

    //根据列索引，获取可以为空的结果
    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String sqlJson = rs.getString(columnIndex);
        if (null != sqlJson) {
            return JSONParseUtils.json2Object(sqlJson, clazz);
        }
        return null;
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String sqlJson = cs.getString(columnIndex);
        if (null != sqlJson) {
            return JSONParseUtils.json2Object(sqlJson, clazz);
        }
        return null;
    }
}

