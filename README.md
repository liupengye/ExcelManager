# Excel报表管理系统

基于SpringBoot + Vue3的Excel报表管理系统,支持Excel文件的导入、预览、编辑等功能。

## 核心功能

### 后端功能
- Excel文件解析与导入
  - 使用EasyExcel解析Excel文件
  - 支持动态创建数据表存储Excel数据
  - 自动解析表头信息并存储元数据
- 通用数据操作
  - 支持对导入数据的增删改查
  - 动态SQL构建,适配不同表结构

### 前端功能
- 文件上传
  - 支持Excel文件上传
  - 文件格式校验
- 数据展示
  - 使用Element Plus表格组件展示数据
  - 支持分页查询
- 数据编辑
  - 单元格内容在线编辑
  - 实时保存修改

## 技术栈

### 后端
- Spring Boot 3.1.0
- MyBatis Plus 3.5.3
- EasyExcel 3.3.3
- MySQL

### 前端
- Vue 3
- Element Plus
- Vite


## 使用说明

1. 文件上传
- 支持.xlsx/.xls格式的Excel文件
- Excel文件第一行必须是表头
- 数据行从第二行开始

2. 数据管理
- 可查看所有已导入的Excel文件列表
- 点击文件名进入数据预览和编辑页面
- 支持单元格双击编辑

## 数据库设计

1. excel_meta - Excel元数据表
   sql
   CREATE TABLE excel_meta (
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   file_name VARCHAR(255),
   table_name VARCHAR(255),
   headers TEXT,
   create_time DATETIME,
   update_time DATETIME
   );

2. 动态数据表
- 系统会根据上传的Excel自动创建对应的数据表
- 表名格式: excel_data_{timestamp}