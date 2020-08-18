package cn.ly.base_common.helper.redis.pubsub;

import cn.ly.base_common.helper.mail.MailHelper;
import cn.ly.base_common.utils.date.LyJdk8DateUtil;
import cn.ly.base_common.utils.error.LyThrowableUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.net.LyNetworkUtil;
import org.slf4j.Logger;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

/**
 * Created by liaomengge on 17/9/7.
 */
public class RedisMessagePublisher implements MessagePublisher {

    private static final Logger log = LyLogger.getInstance(RedisMessagePublisher.class);

    private final StringRedisTemplate stringRedisTemplate;

    private final ChannelTopic channelTopic;

    private final MailHelper mailHelper;

    public RedisMessagePublisher(StringRedisTemplate stringRedisTemplate, ChannelTopic channelTopic,
                                 MailHelper mailHelper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.channelTopic = channelTopic;
        this.mailHelper = mailHelper;
    }

    @Override
    public void publish(String message) {
        try {
            stringRedisTemplate.convertAndSend(channelTopic.getTopic(), message);
        } catch (Exception e) {
            log.error("Publish Redis异常 ===> ", e);
            if (mailHelper != null) {
                mailHelper.sendTextMail(LyNetworkUtil.getHostAddress() + "/" + LyNetworkUtil.getHostName() +
                        "发布消息异常!", LyJdk8DateUtil.getNowDate2String() + " ===> " + LyThrowableUtil.getStackTrace(e));
            }
        }
    }
}
