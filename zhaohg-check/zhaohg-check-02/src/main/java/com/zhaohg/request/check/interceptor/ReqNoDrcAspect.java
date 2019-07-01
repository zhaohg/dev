package com.zhaohg.request.check.interceptor;

import com.zhaohg.request.check.properties.CheckReqProperties;
import com.zhaohg.sbcorder.common.enums.StatusEnum;
import com.zhaohg.sbcorder.common.exception.SBCException;
import com.zhaohg.sbcorder.common.req.BaseRequest;
import com.zhaohg.sbcorder.common.util.StringUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Function:切面
 * @author zhaohg
 * Date: 2017/7/31 20:07
 * @since JDK 1.8
 */
@Aspect//切面注解
@Component//扫描
@EnableAspectJAutoProxy(proxyTargetClass = true)//开启cglib代理
public class ReqNoDrcAspect {
    private static Logger logger = LoggerFactory.getLogger(ReqNoDrcAspect.class);

    @Autowired
    private CheckReqProperties properties;

    private String prefixReq;

    private long day;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public static BaseRequest getBaseRequest(JoinPoint joinPoint) throws Exception {
        BaseRequest returnRequest = null;
        Object[] arguments = joinPoint.getArgs();
        if (arguments != null && arguments.length > 0) {
            returnRequest = (BaseRequest) arguments[0];
        }
        return returnRequest;
    }

    @PostConstruct
    public void init() throws Exception {
        prefixReq = properties.getRedisKey() == null ? "reqNo" : properties.getRedisKey();
        day = properties.getRedisTimeout() == null ? 1L : properties.getRedisTimeout();
        logger.info("sbc-request-check init......");
        logger.info(String.format("redis prefix is [%s],timeout is [%s]", prefixReq, day));
    }

    /**
     * 切面该注解
     */
    @Pointcut("@annotation(com.zhaohg.request.check.anotation.CheckReqNo)")
    public void checkRepeat() {
    }

    @Before("checkRepeat()")
    public void before(JoinPoint joinPoint) throws Exception {
        BaseRequest request = getBaseRequest(joinPoint);
        if (request != null) {
            final String reqNo = request.getReqNo();
            if (StringUtil.isEmpty(reqNo)) {
                throw new SBCException(StatusEnum.REPEAT_REQUEST);
            } else {
                try {
                    String tempReqNo = redisTemplate.opsForValue().get(prefixReq + reqNo);
                    logger.debug("tempReqNo=" + tempReqNo);

                    if ((StringUtil.isEmpty(tempReqNo))) {
                        redisTemplate.opsForValue().set(prefixReq + reqNo, reqNo, day, TimeUnit.DAYS);
                    } else {
                        throw new SBCException("请求号重复," + prefixReq + "=" + reqNo);
                    }

                } catch (RedisConnectionFailureException e) {
                    logger.error("redis操作异常", e);
                    throw new SBCException("need redisService");
                }
            }

        }
    }
}
