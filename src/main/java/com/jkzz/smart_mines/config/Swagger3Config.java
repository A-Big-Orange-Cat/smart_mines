package com.jkzz.smart_mines.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.jkzz.smart_mines.enumerate.NameValueEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.PropertySpecificationBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.schema.Annotations;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableKnife4j
@EnableOpenApi
public class Swagger3Config implements ModelPropertyBuilderPlugin {

    /**
     * 用于读取配置文件 application.properties 中 swagger 属性是否开启
     */
    @Value("#{T(java.lang.Boolean).parseBoolean('${swagger.enabled}')}")
    private Boolean swaggerEnabled;

    @Bean
    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
                }
                return bean;
            }

            private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
                List<T> copy = mappings.stream()
                        .filter(mapping -> mapping.getPatternParser() == null)
                        .collect(Collectors.toList());
                mappings.clear();
                mappings.addAll(copy);
            }

            @SuppressWarnings("unchecked")
            private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
                try {
                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                    field.setAccessible(true);
                    return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("标准接口")
                .apiInfo(apiInfo())
                //是否开启swagger
                .enable(swaggerEnabled)
                //通过.select()方法，配置扫描哪些API接口,RequestHandlerSelectors配置如何扫描接口
                .select()
                //过滤条件,扫描指定路径下的文件
                .apis(RequestHandlerSelectors.basePackage("com.jkzz.smart_mines.controller"))
                //指定路径处理,PathSelectors.any()代表不过滤任何路径
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        /*作者信息*/
        Contact contact = new Contact("孙志东", "#", "770446923@qq.com");
        return new ApiInfoBuilder()
                .title("智慧矿山系统Swagger3接口文档")
                .description("智慧矿山系统Swagger3测试")
                .version("v1.0")
                .contact(contact)
                .build();
    }

    /**
     * 为枚举字段设置注释
     */
    @Override
    public void apply(ModelPropertyContext context) {
        Optional<ApiModelProperty> annotation = Optional.empty();

        // 找到 @ApiModelProperty 注解修饰的枚举类
        if (context.getBeanPropertyDefinition().isPresent()) {
            annotation = Annotations.findPropertyAnnotation(context.getBeanPropertyDefinition().get(), ApiModelProperty.class);
        }

        // 没有 @ApiModelProperty 或者 notes 属性没有值，直接返回
        if (!annotation.isPresent() || StringUtils.isEmpty((annotation.get()).notes())) {
            return;
        }

        // @ApiModelProperties 中的 notes 指定的 class 类型
        Class rawPrimaryType;
        try {
            rawPrimaryType = Class.forName((annotation.get()).notes());
        } catch (ClassNotFoundException e) {
            //如果指定的类型无法转化，直接忽略
            return;
        }

        Object[] subItemRecords = null;
        // 判断 rawPrimaryType 是否为枚举，且实现了 NameValueEnum 接口
        if (Enum.class.isAssignableFrom(rawPrimaryType) && NameValueEnum.class.isAssignableFrom(rawPrimaryType)) {
            // 拿到枚举的所有的值
            subItemRecords = rawPrimaryType.getEnumConstants();
        } else {
            return;
        }

        final List<String> displayValues = Arrays.stream(subItemRecords).filter(Objects::nonNull)
                // 调用枚举类的 description 方法
                .map(p -> ((NameValueEnum) p).description()).filter(Objects::nonNull).collect(Collectors.toList());

        String joinText = " (" + String.join("; ", displayValues) + ")";
        try {
            // 拿到字段上原先的描述
            Field mField = PropertySpecificationBuilder.class.getDeclaredField("description");
            mField.setAccessible(true);
            // context 中的 builder 对象保存了字段的信息
            joinText = mField.get(context.getSpecificationBuilder()) + joinText;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        // 设置新的字段说明并且设置字段类型
        context.getSpecificationBuilder().description(joinText);
    }

    /**
     * 返回是否应根据给定的分隔符调用插件
     */
    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }

}
