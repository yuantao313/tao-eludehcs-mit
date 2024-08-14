package com.oat.common.utils.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author:yhl
 * @create: 2022-08-24 14:15
 * @Description: 多个数据源的选择
 */
public class MultipleDataSourceToChoose extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return HandlerDataSource.getDataSource();
    }
}
