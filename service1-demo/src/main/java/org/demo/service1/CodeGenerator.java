package org.demo.service1;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create(
                "jdbc:mysql://dev.c1m4088oypfc.ap-east-1.rds.amazonaws.com:3306/draw?rewriteBatchedStatements=true&allowMultiQueries=true&useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC",
                        "root", "D6DKytYqXVTrMaasWwTz").globalConfig(builder -> {
                builder.author("yhx") // 设置作者
                    .dateType(DateType.TIME_PACK)// 时间策略
                    .commentDate("yyyy-MM-dd").outputDir(System.getProperty("user.dir") + "\\src\\main\\java"); // 指定输出目录
            }).packageConfig(builder -> {
                builder.parent("org.center.bh.pro.order.core") // 设置父包名
                    .entity("entity").service("service").serviceImpl("service.impl").mapper("mapper").xml("mapper.xml")
                    .controller("controller").pathInfo(Collections.singletonMap(OutputFile.xml,
                        System.getProperty("user.dir") + "\\src\\main\\resources\\mapper")); // 设置mapperXml生成路径
            }).strategyConfig(builder -> {
                builder.addInclude("t_snaplii_token") // 设置需要生成的表名
                    .addTablePrefix("t_") // 设置过滤表前缀
                    .controllerBuilder().enableRestStyle()// 开启restful风格
                    .entityBuilder().enableFileOverride().enableLombok().mapperBuilder().enableFileOverride()
                    .superClass(BaseMapper.class).enableBaseResultMap().enableBaseColumnList()
                    .formatMapperFileName("%sMapper").formatXmlFileName("%sMapper");// 开启 Lombok

            }).templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
            .execute();
    }
}