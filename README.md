# flight-advisor-app

<h2>Users</h2>

Users available after starting the application:

	1. Admin user
	- username: admin
	- password: admin
	- role: ADMIN

	2. Regular user
	- username: guest
	- password: guest
	- role: REGULAR

<h2>Files</h2>

For testing purposes, there are APIs that import all cities, airports and routes from files provided in the task.
Those APIs are:

	GET /flight/import/default/cities   - Import all default cities
	GET /flight/import/default/airports - Import all default airports
	GET /flight/import/default/routes   - Import all default routes
	
Files can be found on location: src/main/resources/import/

<h2>All APIs</h2>

Register user with role REGULAR

	PATH: /auth/register
	Method: POST
	Body:
		{
			"username": "Username",
			"password": "Password",
			"firstName": "First name",
			"lastName": "Last name"
		}
	Role: Public API
	
Login

	PATH: /login
	Method: POST
	Body:
		{
			"username": "Username",
			"password": "Password",
		}
	Role: Public API
	
Logout

	PATH: /logout
	Method: ANY
	Body:
		{
			"username": "Username",
			"password": "Password"
		}
	Role: Public API

Finding cheapest route between 2 cities

	PATH: /flight
	Method: GET
	Request params:
		sourceCityId - ID of source city
		destinationCityId - ID of destination city
	Role: REGULAR
	
Import airport data from uploaded file

	PATH: /flight/import/airports
	Method: POST
	Body:
		file - File containing airport data
	Role: ADMIN
		
Import route data from uploaded file

	PATH: /flight/import/routes
	Method: POST
	Body:
		file - File containing route data
	Role: ADMIN
		
Import airport data from default file (Given in task) - For easier testing

	PATH: /flight/import/default/airports
	Method: GET
	Role: ADMIN
	
Import route data from default file (Given in task) - For easier testing

	PATH: /flight/import/default/routes
	Method: GET
	Role: ADMIN
	
Import all cities from default airport file (Given in task) - For easier testing

	PATH: /flight/import/default/cities
	Method: GET
	Role: ADMIN
	
Get all cities

	PATH: /city
	Method: GET
	Request params:
		name - city name (optional)
		commentCount - number of comments returned for each city (optional)
	Role: REGULAR
	
Add city

	PATH: /city
	Method: POST
	Body:
		{
			"name": "City name",
			"country": "Country name",
			"description": "City description" 
		}		
	Role: ADMIN
	
Add comment for city

	PATH: /city/comment
	Method: POST
	Body:
		{
			"cityId": 1,
			"text": "Some comment"
		}
	Role: REGULAR
	
Update comment

	PATH: /city/comment/{id}
	Method: PUT
	Path variables:
		id - comment id
	Body:
		{
			"text": "Some comment"
		}
	Role: REGULAR
	
Delete comment

	PATH: /city/comment/{id}
	Method: DELETE
	Path variables:
		id - comment id
	Role: REGULAR
	

