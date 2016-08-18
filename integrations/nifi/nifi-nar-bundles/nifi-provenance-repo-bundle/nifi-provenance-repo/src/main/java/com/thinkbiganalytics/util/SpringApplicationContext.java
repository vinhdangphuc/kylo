package com.thinkbiganalytics.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by sr186054 on 3/3/16.
 */
public class SpringApplicationContext {
    private static final Logger log = LoggerFactory.getLogger(SpringApplicationContext.class);

    private static class LazyHolder {
        static final SpringApplicationContext INSTANCE = new SpringApplicationContext();
    }

    public static SpringApplicationContext getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static ApplicationContext applicationContext;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    public void initializeSpring() {
        if (initialized.compareAndSet(false,true)) {
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"application-context.xml"});
            this.applicationContext = applicationContext;
        }
    }

    public  ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public  Object getBean(String beanName) throws BeansException {
        if(applicationContext == null){
            initializeSpring();
        }
        if(applicationContext != null) {
            try {
                return this.applicationContext.getBean(beanName);
            }catch(Exception e){
                log.error("Error getting bean {} , {} ",beanName,e.getMessage(),e);
            }
        }
        else {
            log.error("Unable to get Spring bean for {}.  Application Context is null");
        }
        return null;

    }

    public  String printBeanNames(){
        if(applicationContext == null){
            initializeSpring();
        }
        if(applicationContext != null) {
            String beanNames = StringUtils.join(applicationContext.getBeanDefinitionNames(), ",");
            log.info("SPRING BEANS: " + beanNames);
            return beanNames;
        }
        else {
            log.error("LOG: ERROR CONTEXT IS NULL");
            return null;
        }
    }

    /**
     * Autowire properties in the object
     * @param key
     * @param obj
     * @return
     */
    public  Object  autowire(String key, Object obj) {

        return autowire(key,obj,true);
    }


    /**
     * Autowire an object
     * Force it to be autowired even if the bean is not registered with the appcontext
     * @param key
     * @param obj
     * @param force
     * @return
     */
    public Object autowire(String key, Object obj,boolean force) {
        Object bean = null;
            try {
                bean = SpringApplicationContext.getInstance().getBean(key);
            } catch (Exception e) {

            }
        if (bean == null  || force) {

            try {
                if (applicationContext == null) {
                    initializeSpring();
                }
                if (applicationContext != null) {
                    AutowireCapableBeanFactory autowire = getApplicationContext().getAutowireCapableBeanFactory();
                    autowire.autowireBean(obj);
                    //fire PostConstruct methods
                    autowire.initializeBean(obj, key);
                    return obj;
                } else {
                    log.error("Unable to autowire {} with Object.  ApplicationContext is null.", key, obj);
                }
            } catch (Exception e) {
                log.error("Unable to autowire {} with Object ", key, obj);
            }
        }
        else if( bean != null){
            return bean;
        }
        return null;
    }
}
