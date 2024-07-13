package io.ocepgen.common.config;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 这是多余的配置了，WebMvcConfigurer可以看security模块的config包下的
 */
@Configuration
public class WebConfigure implements WebMvcConfigurer {
        /**
         * 配置消息转换器
         * @param converters
         */
        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
/*            //需要定义一个convert转换消息的对象;
            FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
            //添加fastJson的配置信息;
            FastJsonConfig fastJsonConfig = new FastJsonConfig();
            fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
            //全局时间配置
            fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
            fastJsonConfig.setCharset(Charset.forName("UTF-8"));
            //处理中文乱码问题
            List<MediaType> fastMediaTypes = new ArrayList<>();
            fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
            //在convert中添加配置信息.
            fastConverter.setSupportedMediaTypes(fastMediaTypes);
            fastConverter.setFastJsonConfig(fastJsonConfig);
            //设置Long为字符串，解决精度丢失问题
            SerializeConfig serializeConfig = SerializeConfig.globalInstance;
            serializeConfig.put(Long.class, ToStringSerializer.instance);
            serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
            fastJsonConfig.setSerializeConfig(serializeConfig);
            converters.add(0,fastConverter);*/
        }
}


