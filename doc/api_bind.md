# /bind
----------
绑定接口

URL
---
https://{SERVERURL:PORT}/bind

数据格式
--------
JSON

HTTP请求方式
------------
POST

是否需要登录
------------
否

请求参数
--------

名称        | 必选  | 类型      | 说明
------------|-------|-----------|---------
accountId  |  yes  | string    | 账号ID
platform    |  no   | string    | 平台类型，如facebook_id, google_play_id, app_store_id
platformId |  no   | string    | 平台ID

调用示例
--------

```
curl -d '{ "device_id": "testdevice"}' -H "Content-Type: application/json" -X POST http://{SERVERURL:PORT}/bind
```

返回说明
--------
名称        | 类型      | 说明
------------|-----------|---------
code        |  string    | 错误码，0 代表成功
msg         |  string    | 错误描述信息

返回示例
--------
```
{
  "code": 0,
  "msg": "No error has occured."
}
```
