# kafttp

Test task

## Installation

install: docker

## Usage

to run the app clone the repository and run from the root folder:

    ./start-app.sh

this will run docker containers

to stop the app run from the root folder:

     ./stop-app.sh

## Swagger
to discover the API follow the link to Swagger: http://localhost:3000/

<mark style="background-color: lightblue"> sometimes it takes some time to start docker containers, so check "docker ps" to see if all containers are started and try to refresh http://localhost:3000/ several times </mark>

## Examples

Out of the box the app listens to `books` and `films` topics

create a topic filter: POST http://localhost:3000/filters

    {
      "topic": "films",
      "q": "star wars"
    }

response example:

    {
      "q": "star wars",
      "topic": "films",
      "id": "63523684-367f-4e98-bac5-4bd3d0f0b264"
    }

connect messages to the Kafka topic:

    docker exec --interactive --tty broker \
    kafka-console-producer --bootstrap-server broker:9092 \
    --topic films

post message:

    > I love star wars

get the filter messages: GET http://localhost:3000/filters/{filter-id}/messages

    [{
       "message": "I love star wars",
       "timestamp": 1684089255762,
       "id": "395eeef5-1a0c-4f13-9b79-b426f7da6ebc"
    }]


### One more example:

    {
      "topic": "books",
      "q": "sicp"
    }

connect to the books topic:

    docker exec --interactive --tty broker \
    kafka-console-producer --bootstrap-server broker:9092 \
    --topic books

post message:

    > I've read sicp