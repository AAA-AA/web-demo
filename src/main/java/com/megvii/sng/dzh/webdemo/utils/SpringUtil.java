package com.megvii.sng.dzh.webdemo.utils;/**
 * @ProjectName: carwl
 * @Package: com.megvii.carwl.utils
 * @Description:
 * @Author: luyaoyu
 * @CreateDate: 2018/11/27 14:09
 * @UpdateDate: 2018/11/27 14:09
 */

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * describe:
 *
 * @author luyaoyu
 * @date 2018/11/27
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContextParam) throws BeansException {
        applicationContext = applicationContextParam;
    }

    public static Object getObject(String id) {
        Object object = null;
        object = applicationContext.getBean(id);
        return object;
    }

	public static String getProperty(String key) {
		return applicationContext.getEnvironment().getProperty(key);
	}

    public static <T> T getObject(Class<T> tClass) {
        return applicationContext.getBean(tClass);
    }

    public static <T> T getBean(Class<T> tClass) {
        return applicationContext.getBean(tClass);
    }
}
