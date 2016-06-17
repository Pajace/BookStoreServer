# BookStoreServer
This book store server provide following api
- POST    /bookstore/add
- GET     /bookstore/:isbn
- GET     /bookstore/list
- PUT     /bookstore/update
- DELETE  /bookstore/delete/:isbn

## Run server
`sbt run`

## Run test case
`sbt test`  
*Note*: some integration test case need MongoDB's support

## Example for bookstore API
You can you Chrom's plugin - PostMan to do POST/GET/PUT/DELETE or use the following command to test it.

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
