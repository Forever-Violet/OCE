/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.ocepgen.modules.sys.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.ocepgen.common.page.PageData;
import io.ocepgen.common.service.impl.BaseServiceImpl;
import io.ocepgen.common.utils.ConvertUtils;
import io.ocepgen.modules.sys.dao.SysDictDataDao;
import io.ocepgen.modules.sys.dao.SysDictTypeDao;
import io.ocepgen.modules.sys.dto.SysDictTypeDTO;
import io.ocepgen.modules.sys.entity.DictData;
import io.ocepgen.modules.sys.entity.DictType;
import io.ocepgen.modules.sys.entity.SysDictTypeEntity;
import io.ocepgen.modules.sys.service.SysDictTypeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 字典类型
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
@AllArgsConstructor
public class SysDictTypeServiceImpl extends BaseServiceImpl<SysDictTypeDao, SysDictTypeEntity> implements SysDictTypeService {
    private final SysDictDataDao sysDictDataDao;

    @Override
    public PageData<SysDictTypeDTO> page(Map<String, Object> params) {
        IPage<SysDictTypeEntity> page = baseDao.selectPage(
                getPage(params, "sort", true),
                getWrapper(params)
        );

        return getPageData(page, SysDictTypeDTO.class);
    }

    private QueryWrapper<SysDictTypeEntity> getWrapper(Map<String, Object> params) {
        String dictType = (String) params.get("dictType");
        String dictName = (String) params.get("dictName");

        QueryWrapper<SysDictTypeEntity> wrapper = new QueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(dictType), "dict_type", dictType);
        wrapper.like(StrUtil.isNotBlank(dictName), "dict_name", dictName);

        return wrapper;
    }

    @Override
    public SysDictTypeDTO get(Long id) {
        SysDictTypeEntity entity = baseDao.selectById(id);

        return ConvertUtils.sourceToTarget(entity, SysDictTypeDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysDictTypeDTO dto) {
        SysDictTypeEntity entity = ConvertUtils.sourceToTarget(dto, SysDictTypeEntity.class);

        insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysDictTypeDTO dto) {
        SysDictTypeEntity entity = ConvertUtils.sourceToTarget(dto, SysDictTypeEntity.class);

        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long[] ids) {
        //删除
        deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public List<DictType> getAllList() {
        List<DictType> typeList = baseDao.getDictTypeList();
        List<DictData> dataList = sysDictDataDao.getDictDataList();
        for (DictType type : typeList) {
            for (DictData data : dataList) {
                if (type.getId().equals(data.getDictTypeId())) {
                    type.getDataList().add(data);
                }
            }
        }
        return typeList;
    }

}
