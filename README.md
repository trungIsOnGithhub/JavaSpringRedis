# Basic Redis Chat App Demo (Java/Spring)

Basic chat app in Java Spring Boot using Redis for concept demonstration of [pub/sub model](https://redis.io/docs/interact/pubsub/) and commons data structure.

> Using Redis Server 6.0.16, JDK 17.0.2, VS Code, Maven 3.8.8

#### For API documentation and(maybe demos): visit below link

[Azure Web App](http://javaspringtest1.azurewebsites.net/)

#### How to run on local machine

1. Prepare a Redis Server instance: [Normal Installation](https://redis.io/docs/install/install-redis/), Docker Images(https://hub.docker.com/redis),...

Change ```src/main/resources/application.properties``` for desired behavior.

2. Clone, Build and Run Project in any IDE.

If using command line, buid the project with below command
```./mvnw clean package```
or in Windows:
```.\mvnw.cmd clean package```

If using command line, run the app with below command, auto reload included.
```./mvnw spring-boot:run```
or in Windows:
```.\mvnw.cmd spring-boot:run``

##### Run Frontend detached

```sh
cd ./client
yarn upgrade # if unneed, use yarn install
yarn start
```

### Some Details

Automatically fill up Redis database with demo data after checking if there are any data about current user `EXISTS total_users`

##### Creating of demo users:

- Add one user indicator `INCR total_users`
- Then we set user lookup key by username, and write user data is write data to HashSet:
**e.g - nick**
`SET username:nick user:1`.
`HSET user:1 username "nick" password "bcrypt_hashed_password"`

- Each user is automatically added to default "General" room., representing a Set.
- For handling rooms for each user, we have a set that holds the room ids.
**_e.g._ user id 1** `SADD user:1:rooms "0"`.

**Populate private messages between users.**
- If a private room needs to be established, for each user a room id: `room:{userId1}:{userId2}` is generated and add to room set for each user

**e.g.** `SADD user:1:rooms 1:2` and `SADD user:2:rooms 1:2`.

- Add messages to this room by writing to a sorted set:
**e.g.** `ZADD room:1:2 1615480369 "{'from': 1, 'date': 1615480369, 'message': 'Hello', 'roomId': '1:2'}"`.

- Rooms are sorted sets which contains messages where score is the timestamp for each message.

**Populate the "General" room with messages.** Messages are added to the sorted set with id of the "General" room: `room:0`


#### How the data is stored:
- User data is stored in a hash set where each user entry contains values: `{username}`, `{hashed_password}`

- User hash set stored by key `user:{userId}`. The data for it stored with `HSET key field data`.

- User id is calculated by incrementing the `total_users`.

- Another set stored username as key (`username:{username}`) for lookup userId **e.g**`SET username:Alex 4`

#### How the data is accessed:

- **Get User** `HGETALL user:{id}`

  - E.g `HGETALL user:2`, where we get data for the user with id: 2.

- **Online users:** will return ids of users which are online
  - E.g `SMEMBERS online_users`

### Rooms

![How it works](docs/screenshot001.png)

#### How the data is stored:

Each user has a set of rooms associated with them.



- Rooms which user belongs too are stored at `user:{userId}:rooms` as a set of room ids.

  - E.g `SADD user:Alex:rooms 1`

- Set room name: `SET room:{roomId}:name {name}`
  - E.g `SET room:1:name General`

#### How the data is accessed:

- **Get room name** `GET room:{roomId}:name`.

  - E. g `GET room:0:name`. This should return "General"

- **Get room ids of a user:** `SMEMBERS user:{id}:rooms`.
  - E. g `SMEMBERS user:2:rooms`. This will return IDs of rooms for user with ID: 2

### Messages

#### Pub/sub

After initialization, a pub/sub subscription is created: `SUBSCRIBE MESSAGES`.

#### Messsage stored:

- Messages stored at chat room key `room:{roomId}` key in a Sorted Set.

#### Message add:
- `ZADD room:{roomId} {timestamp} {message}` command. Message is serialized to JSON string for simplification

#### Message accessed:
- `ZREVRANGE room:{roomId} {offset_start} {offset_end}` get messages with offsets for the private roomId with the latest order.

### Session handling

The chat server works as a basic _REST_ API which involves keeping the session and handling the user state in the chat rooms (besides the WebSocket/real-time part).

When a WebSocket/real-time server is instantiated, which listens for the next events:

### Connection

- A global set with `online_users` key is to keep the online state for each user. On a new connection:
`SADD online_users 1` (We add user with id 1 to the set **online_users**).

#### Disconnect
- Remove the user for **online_users** set and notify the clients: `SREM online_users 1` (makes user with id 1 offline).

**Message**. A user sends a message, and it needs to be broadcasted to the other clients. The pub/sub allows us also to broadcast this message to all server instances which are connected to this Redis:

`PUBLISH message "{'serverId': 4132, 'type':'message', 'data': {'from': 1, 'date': 1615480369, 'message': 'Hello', 'roomId': '1:2'}}"`

Note we send additional data related to the type of the message and the server id. Server id is used to discard the messages by the server instance which sends them since it is connected to the same `MESSAGES` channel.

`type` field of the serialized JSON corresponds to the real-time method we use for real-time communication (connect/disconnect/message).

`data` is method-specific information. In the example above it's related to the new message.

#### How the data is stored / accessed:

Connect to Redis by utilizing the [**Letuce**](https://github.com/lettuce-io/lettuce-core) client.