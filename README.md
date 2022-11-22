# System of Registration Solar Power Plants

The system for registration of a solar power plant to collect and store data on electricity generated, which can be exported as CSV, XML, or JSON files.

## Description

The system provides three roles:
* User
* Editor
* Administrator

Using this system you can:
* register a profile
* view and edit a profile info

  <img src="https://user-images.githubusercontent.com/38464535/203280690-bcae9b30-5624-4f26-ba4a-6a48971f4b4f.png" width=50%>
* register several solar power plants
* view registered solar power plants

  <img src="https://user-images.githubusercontent.com/38464535/203280052-5d509787-8360-4be8-8295-bcf4339bda93.png" width=85%>
* view info about station

  <img src="https://user-images.githubusercontent.com/38464535/203281686-4e8795bb-0498-4f47-bdcc-20a0022a939a.png" width=85%>
* view data on electricity generation
* view data in the form of graphs
* export data in the form of CSV, XML, and JSON files
* receive messages from the system and communicate with the administrator

  <img src="https://user-images.githubusercontent.com/38464535/203280423-dacf7f2b-1408-4d15-bbc1-1ad72c716ae1.png" width=85%>


The project implements an administrator mode where you can:
* perform CRUD operations with users, you cannot delete yourself :)
 
  <img src="https://user-images.githubusercontent.com/38464535/203277465-4469f222-0701-4353-939b-9d00e20754e6.png" width=85%>
* perform CRUD operations with solar power plants

  <img src="https://user-images.githubusercontent.com/38464535/203278606-2d86406b-506d-49c5-ac53-5deb644d94e8.png" width=85%>


### Built With

* Spring Framework
* JPA
* MySQL
* Bootstrap
* Thymeleaf

## Getting Started

### Installing

1. Get a free API Key at [https://openweathermap.org/api](https://openweathermap.org/api) and [https://developer.mapquest.com/documentation](https://developer.mapquest.com/documentation/).
2. Clone the repo
   ```sh
   git clone https://github.com/PavloDrabchuk/registration_solar_power_plant.git
   ```
3. Rename `project.properties.example` to `project.properties` and `application.properties.example` to `application.properties`
4. Enter your API's in `project.properties`
   ```properties
   OPEN_WEATHER_MAP_API_KEY=YOUR_API_KEY
   MAP_QUEST_API_KEY=YOUR_API_KEY
5. Enter your e-mail as admin in `project.properties`
   ```properties
   ADMIN_EMAIL=admin@example.com
6. Enter your datasource settings in `application.properties`
   ```properties
   spring.datasource.url=datasource_url
   spring.datasource.username=username
   spring.datasource.password=password
7. Enter your email sender info in `application.properties`
   ```properties
   spring.mail.username=mail_username
   spring.mail.password=mail_password
8. **Optional**
   ``
   If you need fake data, the system implements a Seeder to populate the database
   ``

### Executing program

* Run project
* Open in your web browser:
   ```
   http://localhost:8080/
   ```

## Help

If you have a problem, write me: ravluk2000@gmail.com

## Authors

* **Pavlo Drabchuk** - ravluk2000@gmail.com
