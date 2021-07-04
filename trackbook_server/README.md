# :cloud: TrackBook Application (back-end)  

This repository hosts the source code of the back-end services developed to assist my TrackBook application.

## :heart: Powered By

The services are very basic but *I tried to use modern technologies* such as:

- **NodeJS**, it is a JavaScript runtime built on Chrome's V8 JavaScript engine;
- **TypeScript**, it is an open-source language which builds on JavaScript, one of the world’s most used tools, by adding static type definitions
- **express**, it is a minimal and flexible Node.js web application framework that provides a robust set of features for web and mobile applications.
- **KNEX**, it is a SQL query builder.
- **PostgreSQL**, it is a SQL-based DBMS.
- **Pattern Design**, learned during Software engeering course.

## :book: API documentation

The services is based on the URL:
> http|https://YOUR_DOMAIN/api/ ...

There are 3 main routes: *account*, *user* and *book*.

>/api/account

>/api/user

>/api/book

### Account route

**Role**: *Allow the registration of new users, authentication of existing users or password's change function*.

| API                     | Method | Role                     | Parameter                                                              | Response                                                                            |
| ----------------------- | ------ | ------------------------ | ---------------------------------------------------------------------- | ----------------------------------------------------------------------------------- |
| _/api/account/register_ | POST   | Register a new user      | **email**:string <br> **penName**:string <br> **password**:md5hash     | **JSON Object**: <br> { *status*:string, *result*:string }                          |
| _/api/account/login_    | POST   | Authenticate an user     | **email**:string <br> **password**:md5hash                             | **JSON Object**: <br> { *status*:string, *result*: {penName:string, token:string} } |
| _/api/account/change_   | POST   | Change a user's password | **email**:string <br> **penName**:string <br> **new_password**:md5hash | **JSON Object**: <br> { *status*:string, *result*:string }                          |

### User route

**Role**: *Allow the backup and restore of user's data*.

**Authentication**: To **access this API you must fill the Authorization header** of the request with the **token** received with the _login api_.

| API          | Method | Role                 | Parameter                                 | Response                                                                                                                                                     |
| ------------ | ------ | -------------------- | ----------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| _/api/user/_ | GET    | Retrieve user's data | None                                      | **JSON Object**: <br> *Success* <br> { *status*:string, *result*:[{*title*:string, *isbn*:string, *page_read*:int, *color*:string, start_read:string}, ...]} |
| _/api/user/_ | POST   | Backup user's data   | user's data in JSON Array on body request | **JSON Object**: <br> *Success* <br> { *status*:string, *result*:string}                                                                                     |

### Book route

**Role**: *Search for book's title inside application database*.

**Authentication**: To **access this API you must fill the Authorization header** of the request with the **token** received with the _login api_.

| API               | Method | Role                 | Parameter     | Response                                                 |
| ----------------- | ------ | -------------------- | ------------- | -------------------------------------------------------- |
| _/api/book/:isbn_ | GET    | Retrieve book's data | *isbn*:string | **JSON OBject**: <br>{ *status*:string, *result*:string} |

## :gear: Setup

You must fulfill the prerequisites before building the back-end:

- *NodeJS* 14+ with *npm*
- *PosgreSQL* with a *role and database* ready for the applicaition to use

If you got everything, we can start building:

1) Install all the dependencies using __npm install__ inside the back-end repo folder.
2) Make a copy of _.env.dist_ from tesmplates folder and paste it on back-end root folder, then fill it with your settings.
3) Initialize the database using __npx knex:migrate up__.
4) Build the application running __npm run build__.
5) Run the application running __npm run start__.
6) The back-end will listening to locahost at your prefered port. You can now set up a reverse proxy or a direct DNS record to your machine. This is sys-admin stuff that do not requires my explaination.

## :warning: Notes

- PostgreSQL may require a change inside _pg_hba.conf_ file to allow md5 authentication!
