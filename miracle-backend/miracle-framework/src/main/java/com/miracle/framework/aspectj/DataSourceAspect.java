package com.miracle.framework.aspectj;

import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.miracle.common.annotation.DataSource;
import com.miracle.common.utils.StringUtils;
import com.miracle.framework.datasource.DynamicDataSourceContextHolder;

/**
 * 多数据源处理
 * 
 * @author miracle
 */
@Aspect
@Order(1)
@Component
public class DataSourceAspect
{
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("@annotation(com.miracle.common.annotation.DataSource)"
            + "|| @within(com.miracle.common.annotation.DataSource)")
    public void dsPointCut()
    {

    }

    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable
    {
        // 新增：保存外层路由，保证嵌套 Mapper / REQUIRES_NEW 返回后恢复业务数据源。
        String previousType = DynamicDataSourceContextHolder.getDataSourceType();
        DataSource dataSource = getDataSource(point);

        if (StringUtils.isNotNull(dataSource))
        {
            DynamicDataSourceContextHolder.setDataSourceType(dataSource.value().name());
        }

        try
        {
            return point.proceed();
        }
        finally
        {
            // 销毁数据源 在执行方法之后
            // 历史实现：DynamicDataSourceContextHolder.clearDataSourceType();
            if (previousType == null) DynamicDataSourceContextHolder.clearDataSourceType();
            else DynamicDataSourceContextHolder.setDataSourceType(previousType);
        }
    }

    /**
     * 获取需要切换的数据源
     */
    public DataSource getDataSource(ProceedingJoinPoint point)
    {
        MethodSignature signature = (MethodSignature) point.getSignature();
        // 历史查询：AnnotationUtils.findAnnotation(signature.getMethod(), DataSource.class);
        // 新增：JDK 代理签名可能来自接口，需解析目标实现方法上的 REQUIRES_NEW 路由。
        java.lang.reflect.Method targetMethod = org.springframework.aop.support.AopUtils
                .getMostSpecificMethod(signature.getMethod(), point.getTarget().getClass());
        DataSource dataSource = AnnotationUtils.findAnnotation(targetMethod, DataSource.class);
        if (Objects.nonNull(dataSource))
        {
            return dataSource;
        }

        // 历史查询：AnnotationUtils.findAnnotation(signature.getDeclaringType(), DataSource.class);
        // 新增：优先读取实现类路由，兼容接口 Mapper 自身的路由注解。
        dataSource = AnnotationUtils.findAnnotation(point.getTarget().getClass(), DataSource.class);
        return dataSource != null ? dataSource : AnnotationUtils.findAnnotation(signature.getDeclaringType(), DataSource.class);
    }

}
