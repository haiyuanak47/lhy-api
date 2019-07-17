package com.lhy.api.admin.core.model;

import java.util.Date;

/**
 *
 */
public class XxlApiGlobalParam {

    private int id;                     // ID
    private int projectId;              // 项目ID
    private String globalQueryParams;   // Global Query String Parameters：全局参数
    private Date addTime;               // 创建时间
    private Date updateTime;            // 更新时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getGlobalQueryParams() {
        return globalQueryParams;
    }

    public void setGlobalQueryParams(String globalQueryParams) {
        this.globalQueryParams = globalQueryParams;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
