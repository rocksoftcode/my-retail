# Target Case Study: myRetail API
This is a solution to Target's Case Study for a simple API layer.  Its purpose is to aggregate the result of an internal catalog API call with pricing data from a NoSQL database. You may also update the product price information in the database. 

## Database layer
This application requires a MongoDB database.  The integration tests use an in-memory instance.  If you don't have an instance to connect with, simply run `./gradlew startMongoDb` and a fresh instance will start up for you. You will need to set up some data.  You'll want to stop the database when you're done, too: `./gradlew stopMongoDb`

## Internal API
By default, the application looks for a stub API.  One can be started via Gradle: `./gradlew runMockApi`.  Or if you're comfortable with Node.js and `npm`, run `npm install && npm start` from the project root. This mock API simply returns a fake product with whichever ID has been provided.  You can point at any service you'd like by overriding the following properties: `catalog.host` and `catalog.port`.

## Running the App
Once you've started/specified a database and app layer, you can start the app by running `./gradlew bootRun`

## Accessing
Sample `GET` request: `curl http://127.0.0.1:8080/products/13860428`

Sample `PUT` request: 
```
curl --location --request PUT 'http://localhost:8080/products/24601' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--header 'Content-Type: text/plain' \
--data-raw '{
	"id": 24601,
	"name": "Foobar",
	"current_price": {
	  "value": 24.60,
	  "currency_code": "EUR"
    }
}'
```
Bear in mind that the `PUT` will only update the price information, per the case study outline.