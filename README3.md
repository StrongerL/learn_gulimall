# 102 - ElasticSearch 商品上架

## 102 ES介绍

ES基本概念

## 103 ES 安装

看文档，安装之后设置开机启动

```shell
docker update elasticsearch --restart=always
```

## 104 Kibana安装

```shell
# kibana指定了了ES交互端口9200  # 5600位kibana主页端口
docker run --name kibana -e ELASTICSEARCH_HOSTS=http://192.168.56.10:9200 -p 5601:5601 -d kibana:7.4.2

# 设置开机启动kibana
docker update kibana  --restart=always
```

## 105 - 121 ES基础

介绍ES基础知识。

## 122 安装ik分词

跟随视频安装即可。

## 123 配置网络、修改yum源

看视频。

## 124 安装nginx、自定义词库

看视频，nginx安装之后，设置开机启动

```
docker update nginx --restart=always
```

## 125 SpringBoot 整合 ES

1. 导入依赖
2. 配置类

## 126 测试存储数据

1. 配置项options
2. IndexRequest，使用json的方式存储

## 127 测试复杂检索

1. SearchRequest
2. 查询条件
3. 检索
4. 分析结果

## 128 分析商品数据的模型

使用空间换时间，使用冗余字段减少查询产生的网络流量。

生成商品索引

```
PUT product
{
    "mappings":{
        "properties": {
            "skuId":{ "type": "long" },
            "spuId":{ "type": "keyword" },
            "skuTitle": {
                "type": "text",
                "analyzer": "ik_smart"
            },
            "skuPrice": { "type": "keyword" },
            "skuImg"  : { "type": "keyword" },
            "saleCount":{ "type":"long" },
            "hasStock": { "type": "boolean" },
            "hotScore": { "type": "long"  },
            "brandId":  { "type": "long" },
            "catalogId": { "type": "long"  },
            "brandName": {"type": "keyword"},
            "brandImg":{
                "type": "keyword",
                "index": false,
                "doc_values": false
            },
            "catalogName": {"type": "keyword" },
            "attrs": {
                "type": "nested",
                "properties": {
                    "attrId": {"type": "long"  },
                    "attrName": {
                        "type": "keyword",
                        "index": false,
                        "doc_values": false
                    },
                    "attrValue": {"type": "keyword" }
                }
            }
        }
    }
}
```

## 129 nested字段分析

不使用的话默认会进行扁平化处理。

## 130 商品上架-构造基本数据

1. 在common中添加ES相关的TO
2. SpuInfoController 添加方法
3. 对应的service和impl

## 131 商品上架-构造SKU检索属性

1. service、impl
2. mapper、xml

## 132 商品上架-远程查询库存

1. ware的controller
2. VO
3. service、impl
4. 使用泛型改造R

## 133 商品上架-上架接口

1. ES的controller
2. 远程接口
3. 修改上架状态

## 134 - 135 商品上架-上架测试

1. 重构R，之前的有错误。









