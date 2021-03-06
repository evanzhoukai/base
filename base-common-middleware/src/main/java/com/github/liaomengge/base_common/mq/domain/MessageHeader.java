package com.github.liaomengge.base_common.mq.domain;

import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import com.github.liaomengge.base_common.utils.misc.LyIdGeneratorUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by liaomengge on 2019/11/18.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageHeader {
    private String mqTraceId = LyIdGeneratorUtil.uuid();
    private long sendTime = LyJdk8DateUtil.getMilliSecondsTime();
}
