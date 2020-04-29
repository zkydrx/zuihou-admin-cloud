package com.github.zuihou.authority.dao.defaults;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.github.zuihou.authority.entity.defaults.Tenant;
import com.github.zuihou.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 企业
 * </p>
 *
 * @author zuihou
 * @date 2019-10-25
 */
@Repository
@SqlParser(filter = true)
public interface TenantMapper extends SuperMapper<Tenant>
{

    /**
     * 根据租户编码查询
     *
     * @param code 租户编码
     * @return
     */
    Tenant getByCode(@Param("code") String code);
}
