# Purifier
[![travis.ci](https://travis-ci.com/Nachtbeere/Purifier.svg?branch=master)](https://travis-ci.com/github/Nachtbeere/Purifier)

A RESTful API Endpoint Plugin for Bukkit/Spigot API Server

## Features
### REST API
Access server resource by web request. no need to use [buggy rcon](https://bugs.mojang.com/browse/MC-87863)

### JWT Authorization
Make control for api access. you can set access accounts in config.yml. make sure you change "secret" before run server.

### Local Cached Username-UUID lookup
The mojang api have hard limit for call. use cached user data for less mojang api call.
*only works for joined player before

### Auto-generated Swagger API Docs
Stop to handwriting api documents. It makes more free time for developers.
you can find easy for spec and correct request. we recommended use the [postman](https://www.postman.com/) for develop.

## Usage
### Create JWT Token
1. make sure postman or test method have imported api url.
2. POST request to <code>{baseUrl}/auth</code> with <code>{ "username": :username, "password": :password }</code>.
    * if you want more secure for authentication, use https request with proxy server.
    * do not expose purifier server alone. It may cause of security issue.
3. grab <code>{ "token": :token }</code> for future access.

### Username-UUID lookup
1. make sure postman or test method have imported api url.
2. request to <code>{baseUrl}/api/v1/mojang-users/:username</code>.
    * if you enabled authentication method, create bearer token and send with "Authorization: Bearer {token}" header.
3. this api response makes mojang-format compatible. you can grab same as mojang api like <code>{ "id": :uuid, "name": :username }</code>.
    * if requested user is not exist, it return <code>NO_CONTENT_204</code> status code same as mojang api.
4. it's only response for joined players. for "real existed all mojang users", use mojang api as failsafe in your application.

### REST API
1. make sure postman or test method have imported api url.
2. follow <code>api-docs</code> for access.
3. if there is bug or something weird. make issue or pull request here.

## API Access permissions examples
see [latest code](https://github.com/Nachtbeere/Purifier/blob/master/src/main/kotlin/net/nachtbeere/minecraft/purifier/Route.kt)
for check api access permission.

* ANON
    * it can access for anyone.
    * <code>/health</code> - check if server alive.
* READ
    * access for read permission user only.
    * this api only do read stuff.
    * <code>/worlds/current-time</code> - show current time in worlds.
* WRITE
    * access for read/write permission user only.
    * this api do read/write stuff for non-critical things.
    * <code>/worlds/:world/storm</code> - toggle storm for target world.
* ADMIN
    * access for admin permission user only.
    * this api do critical stuff.
    * all <code>/server</code> api.
        * it contains <code>broadcast</code>, <code>reload</code> and <code>shutdown</code>

## Quick Start

1. Run <code>$ gradlew shadowJar</code>
2. Put <code>purifier-${version}-SNAPSHOT-all.jar</code> to your Bukkit/Spigot/Paper Server's <code>plugins</code> directory
    * It located in <code>build/libs</code>
    * or just download latest release.
3. Explore with OpenAPIv3 Supported Tools (like Postman)
    * You can access in http://localhost:8080/api-docs