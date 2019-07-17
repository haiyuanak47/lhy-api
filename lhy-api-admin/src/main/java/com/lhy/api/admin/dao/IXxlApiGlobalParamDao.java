package com.lhy.api.admin.dao;

import com.lhy.api.admin.core.model.XxlApiGlobalParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 */
@Mapper
public interface IXxlApiGlobalParamDao {

    public int add(XxlApiGlobalParam xxlApiGlobalParam);
    public int update(XxlApiGlobalParam xxlApiGlobalParam);
    public int delete(@Param("id") int id);

    public XxlApiGlobalParam load(@Param("id") int id);

    public List<XxlApiGlobalParam> loadByProjectId(@Param("projectId") int projectId);

}
