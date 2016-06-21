# BookStoreServer

This book store server is a practice by using Twitter's Finatra, MongoDB and Scala to build up. In this project, you can know how to wriate a future test, unit test by Scala, and how to use Scala MongoDB driver to operate MongoDB.

## This book store server provide following api
- GET     /bookstore/:isbn  
Get one book by using isbn 
- GET     /bookstore/list  
Get all books info
- GET     /bookstore/find_by_name?name="BOOKS'S NAME"  
Search books info by book's name
- POST    /bookstore/add  
Insert one book 
- PUT     /bookstore/update  
Update one book's info
- DELETE  /bookstore/delete/:isbn  
Delete one book

## Run server
`sbt run`

## Run test case
`sbt test`  
*Note*: some integration test case need MongoDB's support

## Example for bookstore API
You can use Chrome's plugin - *PostMan* to do POST/GET/PUT/DELETE or use the following command to test it.

- add book into book server
```
curl -H 'Content-Type: application/json' -X POST -d '[JSON CONTENT with books]' http://127.0.0.1:8888/bookstore/add
```

- get book info with isbn 9789863476733
```
curl 127.0.0.1:8888/bookstore/9789863476733
```

- get all books list
```
curl  127.0.0.1:8888/bookstore/list
```

- update one book
```
curl -H 'Content-Type: application/json' -X PUT -d '[JSON CONTENT with books]' http://127.0.0.1:8888/bookstore/update
```

- Delete one book with isbn (9789863476733)
```
curl -X DELETE http://127.0.0.1:8888/bookstore/delete/9789863476733
```

## Book's Json Structure
Example:
```json
{
"isbn":"9789863476733",
"name":"Learning Agile",
"author":"Andrew Stellman, Jennifer Greene",
"publishing":"O'Reilly Media",
"version":"1st ed.",
"price":35.99
}
```

## Database
Default database is MongoDB with localhost ip  
You can modify `BookStoreServer.scala` to change MongoDB's ip address.  

### Create a local host test mongo db docker
```bash
# pull official mongo db docker image with version 3.3
$ docker pull mongo:3.3

# create and run mongodb docker container with name mongodb_3.3
$ docker run -d --name mongodb_3.3 -p 27017:27017 mongo:3.3.6
```

# Reference
- [Finatra.info](http://twitter.github.io/finatra/)
- [Docker image for Mongo DB](https://hub.docker.com/_/mongo/)
- [Mongo DB Scala Driver](https://docs.mongodb.com/ecosystem/drivers/scala/#mongo-scala-driver)
- [Scala Mock](http://scalamock.org/)
- [Scala Sbt](http://www.scala-sbt.org/)
