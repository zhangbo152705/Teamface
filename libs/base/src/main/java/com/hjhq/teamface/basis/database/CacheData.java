package com.hjhq.teamface.basis.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2018/9/25.
 * Describe：
 */
@Entity
public class CacheData {
    String companyId;
    String employeeId;
    @Id(autoincrement = true)
    Long id;
    String type;
    String key;
    String content;

    @Keep
    public CacheData(String companyId, String employeeId, Long id, String type,
                     String key, String content) {
        this.companyId = companyId;
        this.employeeId = employeeId;
        this.id = id;
        this.type = type;
        this.key = key;
        this.content = content;
    }

    public CacheData() {
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
