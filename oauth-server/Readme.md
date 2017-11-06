### 用户认证服务

#### OAuth 认证服务器信息

* Context path : /uaa
 
        不要设置为 / ,浏览器内跨域情况下可能出现cookie混淆的问题

* 授权认证过程使用地址
    * 用户授权地址 : /uaa/oauth/authorize GET
    * 获取 access_token 地址 : /uaa/oauth/token POST

* 认证后用户资源访问地址
    * 获取已认证用户信息 : /uaa/user  GET
    * 回收已认证用户的 access_token : /uaa/user/token DELETE          
