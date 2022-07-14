package com.atjgl.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author 小亮
 **/

@Component
@Data
@PropertySource("classpath:tencentcould.properties")
@ConfigurationProperties(prefix = "tencent.could")
public class TencentCouldKey {
    private String id;
    private String key;
}
