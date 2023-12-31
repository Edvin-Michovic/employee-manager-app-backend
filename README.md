# employee-manager-app-backend

Backend part of the employee manager application. 
Made using Java / Maven / Spring Boot on the backend and Angular framework on frontend. 

The project was created in the learning purposes, which allows to get the better understanding of
frontend and backend app communication. 

Concrete in this application all CRUD operations for the Employee object(s) were created:

- Add employee;
- Get all employees & find employee by ID;
- Update (edit) employee;
- Delete employee.

# How to Run
1. Clone this repository,
2. Make sure you are using at least JDK 17.x and Maven 3.x
3. Build the project and run the tests by runing ```mvn clean package```
4. Once successfully build, you can run the service by one of these two methods:

```java -jar target/employeemanager-0.0.1-SNAPSHOT.jar```

or 

```mvn spring-boot:run -D run.arguments="spring.profiles.active=test"```

# Details

## `GET/employee/all`

This end-point is called to retrieve a list of employees from the MySQL database. 

If there are employee objects in the MySQL database, the endpoint will return retrieved list in JSON format:

e.g. `GET/employee/all`, should return:

**Body:**
```json
[
    {
        "id": 1,
        "name": "Edvin Michovic",
        "email": "edvin.michovic@gmail.com",
        "jobTitle": "Java Developer",
        "phone": "860000000",
        "imageUrl": "https://media.licdn.com/dms/image/C4E03AQHNTQifE1sU0g/profile-displayphoto-shrink_800_800/0/1653295160773?e=1704931200&v=beta&t=XjSJkqBSma7qUtRB139KtE3__0Re9gM07G0ZmDEPcwE",
        "employeeCode": "ef9fbe9c-abef-4bed-aaf6-fdf31b67b7b7"
    },
    {
        "id": 2,
        "name": "Ann Coby",
        "email": "ann.coby@company.com",
        "jobTitle": "Senior UI/UX Designer",
        "phone": "860000000",
        "imageUrl": "https://kottke.org/plus/misc/images/ai-faces-01.jpg",
        "employeeCode": "987046a0-0cde-4316-8cff-7474fd854aee"
    },
    {
        "id": 3,
        "name": "Eric Watson",
        "email": "eric.watson@company.com",
        "jobTitle": "SQL Developer",
        "phone": "861010100`",
        "imageUrl": "https://static01.nyt.com/newsgraphics/2020/11/12/fake-people/4b806cf591a8a76adfc88d19e90c8c634345bf3d/fallbacks/mobile-03.jpg",
        "employeeCode": "2f3f459a-148a-4386-8aba-01758485e4a6"
    }
]
```

## `GET/employee/find/{id}`

This end-point is called to retrieve a concrete employee from the MySQL database by this employee's ID.

In case of the proper (existing in DB employee's) ID, the JSON representation of employee object should be returned:

e.g. `GET/employee/find/2`, should return:

**Body:**
```json
{
    "id": 2,
    "name": "Ann Coby",
    "email": "ann.coby@company.com",
    "jobTitle": "Senior UI/UX Designer",
    "phone": "860000000",
    "imageUrl": "https://kottke.org/plus/misc/images/ai-faces-01.jpg",
    "employeeCode": "987046a0-0cde-4316-8cff-7474fd854aee"
}
```

In case of the improper (non-existing employee's) ID, the UserNotFoundException should be thrown.

e.g. `GET/employee/find/10`, should return:

**Exception message:**
```
User by id 10 was not found.
```

## `POST/employee/add`

This end-point is called to save a representation of employee object in JSON into the MySQL database.

ID and employeeCode are unnecessary in that case.

e.g. `POST/employee/add`, with:

**Body (Request):**
```json
{
    "name": "Name Surname",
    "email": "name.surname@company.com",
    "jobTitle": "Job title or/and position",
    "phone": "860000000",
    "imageUrl": "URL link of the image"
}
```
If the body of the POST Request is proper, the response should be returned with **Status: 201 Created**, 
and JSON representation of the added Employee object:

**Body (Response):**
```json
{
    "id": 52,
    "name": "Name Surname",
    "email": "name.surname@company.com",
    "jobTitle": "Job title or/and position",
    "phone": "860000000",
    "imageUrl": "URL link for the image",
    "employeeCode": "cc46a1ce-df7a-46ea-91e3-e0860aa6093e"
}
```

## `PUT/employee/update`

This end-point is called to update a representation of employee object in JSON in the MySQL database.

ID and employeeCode attributes are **necessary** in that case, since hibernate updates the whole representation of the Employee object.

e.g. `PUT/employee/update`, with:

**Body:**
```json
{
    "id": 52,
    "name": "Updated Name Surname",
    "email": "Updated name.surname@company.com",
    "jobTitle": "Updated job title or/and position",
    "phone": "861111111",
    "imageUrl": "Updated URL link for the image",
    "employeeCode": "cc46a1ce-df7a-46ea-91e3-e0860aa6093e"
}
```

should return:

**Body:**
```json
{
    "id": 52,
    "name": "Updated Name Surname",
    "email": "Updated name.surname@company.com",
    "jobTitle": "Updated job title or/and position",
    "phone": "861111111",
    "imageUrl": "Updated URL link for the image",
    "employeeCode": "cc46a1ce-df7a-46ea-91e3-e0860aa6093e"
}
```

## `DELETE/employee/delete/{id}`

This end-point is called to delete employee record from the MySQL database, where **id** - 
ID of the employee in the MySQL database. 

Request do not return any response body, except the **Status: 200 OK** in cases, whether ID of the 
employee was proper or not.

e.g. `DELETE/employee/delete/2`