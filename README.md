<h1 align="center">
  Event Management API
</h1>

This is an application made to manage information about events, as seen on [this vídeo](https://www.youtube.com/watch?v=d0KaNzAMVO4).

## Technologies

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [Spring JPA](https://spring.io/projects/spring-data-jpa)
- [Lombok](https://projectlombok.org/)
- [PostgreSQL](https://www.postgresql.org/)
- [Amazon S3](https://aws.amazon.com/s3/)


## How to run
- Clone git repo:
```
git clone https://github.com/lukeiayf/event-management-api
```

- Build project
```
./mvnw clean package
```

- Run:
```
java -jar ./target/event-api-0.0.1-SNAPSHOT.jar
```

## AWS Integration

This project is fully build to work with AWS, out of the box it can work with an existing S3 bucket, you only need to have the AWS credentials configured on your machine and change the bucket name in the application.properties file.
[Here is a guide for it](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-quickstart.html). Using the AWS SDK you can already do operations like uploading and getting files from the bucket from the get go.


# API Endpoints

## 1. Create a New Event

**POST** `/api/event`

This endpoint allows you to create a new event.

### Request

- **URL:** `/api/event`
- **Method:** `POST`
- **Content-Type:** `multipart/form-data`
- **Parameters:**

| Name         | Type      | Required | Description                             |
|--------------|-----------|----------|-----------------------------------------|
| `title`      | `string`  | Yes      | Title of the event                      |
| `description`| `string`  | No       | Description of the event                |
| `date`       | `long`    | Yes      | Event date (epoch time in milliseconds) |
| `city`       | `string`  | Yes      | City where the event takes place        |
| `state`      | `string`  | Yes      | State where the event takes place       |
| `remote`     | `boolean` | Yes      | Is the event remote? (true/false)       |
| `eventUrl`   | `string`  | Yes      | URL of the event                        |
| `image`      | `file`    | No       | Image file of the event                 |

### Response

- **Status:** `200 OK` on success
- **Response Body (JSON):**

```json
{
    "id": "1234",
    "title": "Sample Event",
    "description": "This is a sample event",
    "date": 1726864231000,
    "city": "Belo Horizonte",
    "state": "MG",
    "remote": false,
    "eventUrl": "https://google.com",
    "imgUrl": "https://imageurl.com/event.jpg"
}
```


## 2. Retrieve  Events

**GET** `/api/event`

**GET** `/api/event/filter`

**GET** `/api/event/{uuid}`

This endpoint allows you to retrieve a list of events or a single one by its UUID.

### Request

- **URL:** `/api/event/{uuid}`
- **Method:** `GET`
- **Parameters:**

| Name   | Type      | Required | Description       |
|--------|-----------|----------|-------------------|
| `uuid` | `string`  | Yes      | UUID of the event |


### Pagination

- If retrieving a list of events, pagination is possible by using path parameters

| Name          | Type      | Required | Description                             |
|---------------|-----------|----------|-----------------------------------------|
| `title`       | `string`  | Yes      | Title of the event                      |
| `description` | `string`  | No       | Description of the event                |
| `date`        | `long`    | Yes      | Event date (epoch time in milliseconds) |
| `city`        | `string`  | Yes      | City where the event takes place        |
| `uf`          | `string`  | Yes      | State where the event takes place       |

### Filters

- You can also filter the results by one or more parameters


| Name          | Description                     |
|---------------|---------------------------------|
| `title`       | Current page of the result list |
| `description` |  Number of items per page        |

### Response

- **Status:** `200 OK` on success
- **Response Body (JSON):**

```json
{
    "id": "1234",
    "title": "Sample Event",
    "description": "This is a sample event",
    "date": 1726864231000,
    "city": "Belo Horizonte",
    "state": "MG",
    "remote": false,
    "eventUrl": "https://google.com",
    "imgUrl": "https://imageurl.com/event.jpg"
}
```
