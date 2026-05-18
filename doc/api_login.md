# /login
----------
登录接口

URL
---
https://{SERVERURL:PORT}/login

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
deviceId   |  yes  | string    | 用户设备ID
platform    |  no   | string    | 平台类型，如facebook_id, google_play_id, app_store_id
platformId |  no   | string    | 平台ID


调用示例
--------

```
curl -d '{ "device_id": "testdevice"}' -H "Content-Type: application/json" -X POST http://{SERVERURL:PORT}/login
```

返回说明
--------
名称        | 类型      | 说明
------------|-----------|---------
code        |  string    | 错误码，0 代表成功
msg         |  string    | 错误描述信息
data        |  Account   | 用户账号信息

Account 信息描述
---------------
名称        | 类型      | 说明
------------|-----------|---------
accountId  |  string    | 账号ID
deviceId   |  string    | 设备ID
facebookId |  string    | facebookId
role        |  RoleInfo  | 玩家角色信息

RoleInfo 信息描述
---------------
名称        | 类型      | 说明
------------|-----------|---------
role_id     |  int      | 角色ID
serverId   |  int      | 服务器ID
level       |  int      | facebookId
updatedAt  |  long     | 上次登录时间戳

返回示例
--------
```
{
  "code": 0,
  "data": {
    "accountId": "hello",
    "deviceId": "hello",
    "facebookId": "ddddd",
    "role": [
      {
        "level": 0,
        "roleId": 1,
        "serverId": 0,
        "updatedAt": 1593762950
      },
      {
        "level": 1,
        "roleId": 1,
        "serverId": 1,
        "updatedAt": 1593761458
      }
    ]
  },
  "msg": "Success."
}
```
