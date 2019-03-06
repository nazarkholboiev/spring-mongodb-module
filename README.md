# Introduction

I wanted to create own system which will allow doing different kinds of queries from the frontend part without the additional code on the backend. I know that there are Spring Data, JPA which is more than enough but I was interested in creating own approach

## Special project for demonstration of module of integration with mongoDB data in Java / Spring

> Author: **Kholboiev Nazar**

Technology stack:  Java, Spring, mongoDB

### What you need to run a project on a local machine:

1. The installed Java Runtime Environment (JRE) and development tools (JDKs) to run the project in an IDE - [download](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)  
2. Installed IDE for Java. I recommend IntelliJ IDEA (Community ed. Is free) - [download](https://www.jetbrains.com/idea/#chooseYourEdition)  
3. Maven. Usually comes with an IDE for Java. If you still need to install it separately - [download](https://maven.apache.org/download.cgi)  
4. Installed mongoDB database - [download](https://www.mongodb.com/download-center#community)  
5. To run an IDE project, find the Maven tab (on the right) run the script plugin/tomcat7:run  
6. take a look at localhost:8080  

========================================  
# What is the feature of this module?
This module provides the ability to implement the CRUD and the search engine collection through absolutely no resources. In the absence of time, even the front end developer will be able to add the necessary new collection or expand the model.
  
# How to use the module?
At this time, the module can only be copied to another project, but you can download this module as a library at https://mvnrepository.com/ or build it into a local repository and reuse it many times to help you establish dependency on the pom.xml file.

# What to do if the module is connected to connect the base, create a new collection and the necessary functionality for it
* (one time) You need to connect the database.  
To do this, you need to implement the interface **IMorphiaProvider**. Be sure to note the created class with the annotation **Component**, with the help of *Dependency injection* the module was able to access the database.  
The interface has all 2 methods: 

1. getMorphia() - provides an instance of the library datas to work with the database. (The instance itself can be created in many ways)
2. getDatastore() - provide an object that gives access to the collections we work with in the program.  
    Here, under the shell, you can implement a cache, optimization of access, and so on. This is at the request of the programmer.  
Standard implementation does not require any effort:

```java
@Component
public class MorphiaProvider implements IMorphiaProvider {
    private final Morphia morphia = new Morphia().mapPackage("co.kholboievnazar.lib.mongodbmodule");

    private final Datastore datastore =
            morphia.createDatastore(new MongoClient(new MongoClientURI("mongodb://localhost:27017")), "mongodbmodule");

    public MorphiaProvider() {

    }

    public Morphia getMorphia() {
        return morphia;
    }

    public Datastore getDatastore() {
        return datastore;
    }
}

```  

* Create a new model:  

1. Inherit model from class **AbstractMongoModel**
2. Add a class annotation **Entity("collection_name")**
3. Fill model with fields. It is desirable to create fields with the access modifier *private* and generate getters/sets for help *Alt + Insert*  
3.1. Fields can be of the following types:  
3.1.1. Field itself - simple types, not marked with annotation  
3.1.2. The embedded object is marked with the annotation **Embedded** (thus the internal embedded object will be stored in the database, the level of embedding is not limited)  
3.1.3. A reference to an object from a collection (this one is another) - is marked with the **Reference** annotation (so we can save on expended money, but we will not be able to search for the intrinsic fields of such an object when searching from these collections, but we will not have to worry about updating the model, and therefore do not need to write triggers) 


**Attention**: In the Java environment, the programmer has access to all internal fields of the field with a *Reference* annotation, without additional queries, which is very convenient.  
Example:

```java
@Entity("plants")
public class Plant extends AbstractMongoModel {
    private String name;
    private String plantCode;
    @Embedded
    private Location location; /* inner object like { "Latitude" : "14°01′18″ N", "Longitude" : "100°10′18″ E" } */
    @Reference
    private List<Car> cars; /* an array of links to cars that make a plant (it would be very hard to keep the entire object) */
    @Reference
    private Account director; /* Link to the director's account */


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

```

* Create a DAO component for the model, following the template **DAO**
   Example: 

```java
@Component
public class AccountDAO extends DAO<Account> { // The imitation will give you a complete CRUD and search engine

    public AccountDAO() {
        super(Account.class);
    }

    public Account getAccountByEmail(String email) { // You can also add your own methods for quick search on this one 
        return iMorphiaProvider.getDatastore()
                .createQuery(Account.class).filter("email", email).get();
    }
}

```

* Now it's time to get the DAO component in the right context and use it
   Example:

```java
@Controller
public class AccountController {
    @Autowired
    private AccountDAO accountDAO; // initialization using dependency injection

    @RequestMapping(value = "/api/account/create", method = RequestMethod.POST)
    @ResponseBody
    public Account createUser(@RequestBody Account user, HttpServletResponse response) {
        user.setEmail(user.getEmail().toLowerCase().trim());
        if (accountDAO.getAccountByEmail(user.getEmail()) != null) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        }
        accountDAO.add(user);
        return user;
    }

    @RequestMapping(value = "/api/account/search", method = RequestMethod.POST)
    @ResponseBody
    public List<Account> searchAccount(@RequestBody FilterDTO body,
                                       @RequestParam(name = "last", required = false) boolean last,
                                       @RequestParam(name = "limit", required = false) Integer limit) {
        return accountDAO.search(body, limit == null ? 10 : limit, last);
    }
    
    @RequestMapping(value = "/api/account/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Account getAccountById(@PathVariable String id) {
        return accountDAO.getByMongoId(id);
    }

    @RequestMapping(value = "/api/account/{id}/update", method = RequestMethod.POST)
    @ResponseBody
    public Account updateAccountById(@PathVariable String id, @RequestBody Map<String, Object> body, HttpServletResponse response) {
        Account account = accountDAO.getByMongoId(id);
        if (account == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        accountDAO.update(account, body);
        return accountDAO.getByMongoId(account._id());
    }

// ...
}
```

# Conclusion
> **A library is created that allows you to work with mongoDB in the model first style on Java (as in entity framework / C # for SQL)**

========================================  
  
Description API, which can be built very quickly using the module.
**The main plus** is a very powerful search engine that covers a very large set of possible queries.
All of them can be done through one method. 
+ Create user

```
method: POST
URI:         /api/account/create
body: <user_object> // {"email":"<->", "roles":[<role list>], ....}

response: <registered user object(with "_id" field)>
```
----------------

+ Change password request

```
method: POST
URI:         /api/account/change_password
body: {"previousPassword":"<old pass>", "password":"<new pass>", "email":"<->"}
```
----

+ Search users

```
method:   POST
URI:          /api/account/search
body: {empty filter array or search request}

search body schema:
{
     "filters" : [{"comparator":<operator>, "value":<const>}, ...more filters or filter group],
     .... more fields
}

NEW!!! if you need to get elements from the end of the selected set, you need to add parameter to the URL:  ?last=true
How to get more than 10 items. Add param &limit=100. you will get not more than 100 items

filter group example: 
{
		"filterGroup" : {
			"connector":"$or",
			"filters": [
					{
						"comparator":"startWith", 
						"value":"My name",
						"fieldName":"name"
					},
					{
						"comparator":"equal", 
						"value":"ADMIN",
						"fieldName":"roles#in"
					}
				]
			
		}
}
// there we will search for admins or guys who has name started with "My name"

response: <list of accounts> (not more then 10 items if limit was not changed)

Q: How to get the next 10 users (pagination)?
A: you need to get "_id" field from the LAST item and use it in the next request in the body:
BODY: 
{ 
     "filters":[{"comparator":"greater", "value":"58fe2872095605286400cfd2", "fieldName":"_id"}] 
}

Q: Which comparators we can use?
A: There are comparators to use: [ equal, notEqual, less, lessOrEqual, greater, greaterOrEqual, startWith ] 

Q: How I can use several filters for the one or more fields?
A: Example: Let's find users with the name which starts with "Vlad"...(it is mean that name can be "Vladimir", "Vladislav" and so on) 
with age more than 18 years and not more than 30. 
In this case, we need to use this body:
{
"filters":[
    {"comparator":"startWith ", "value":"Vlad", "fieldName":"name"},
    {"comparator":"greater", "value": 18, "fieldName":"age"},
    {"comparator":"lessOrEqual", "value":30 ,"fieldName":"age"}
 ]
}

Q: How to search for inner objects?
A: There are 2 types of inner objects (referenced and embedded) [ask backend dev which type has inner object].

If the type is "reference" (for example "plant"), it is mean that you can search only by "_id" of inner object. 
Example: Find users who work on the plant with _id = "X"
BODY: 
{ 
     "filters" :[{"comparator":"equal", "value":"X" ,"fieldName":"plant.$id"}] 
} 
// as you see I have added .$id to the end.

If type is embeded, you can search by all fields. (but this type needs more memory)
Example: find all plants which are situated with 50 x coordinate
BODY: {"location.x":[{"comparator":"equal", "value": 50}]} 
// as you see just joined path by period (".")

Q: How can I search in arrays?
A: Use postfix "#in"
Example: Find all users, who have role "ADMIN". (User can have several roles, so roles are stored in the array.)
BODY:
{
	"filters" :[{"comparator":"equal", "value":"ADMIN"  ,"fieldName": "roles#in"}]
}

Q: How to search account who has "name"="Vasia" or age greater 18 and paginate results?
A: you should use filter group:
BODY:
{
    "filters":[
    {
		"filterGroup" : {
			"connector":"$or",
			"filters": [
					{
						"comparator":"equal", 
						"value":"Vasia",
						"fieldName":"name"
					},
					{
						"comparator":"greater", 
						"value": 18,
						"fieldName":"age"
					}
				]
			
		}
	},
	{                                                // for pagination
           "comparator":"greater", 
	   "value":"<hash>",
	   "fieldName":"_id"
         }
   ]
}

Q: How to get USERs who are not ADMIN
A: use #nin
BODY:
{

			"filters": [
					{
						"comparator":"equal", 
						"value":"USER",
						"fieldName":"roles#in"
					},{
						"comparator":"equal", 
						"value":"ADMIN",
						"fieldName":"roles#nin"
					}
				]
			
}
```
------------

+ Get number for pagination of accounts

```
method:   POST
URI:          /api/account/count
body: {empty or search request} // the same as in the search request, but you will get the count of all matched items

response : {"count": <number>}
```
-----------

+ Get user by id

```
method:   POST
URI:          /api/account/{id}
```
--------

+ Update the FIRST level fields (embedded) of user

```
method:   POST
URI:          /api/account/{id}/update
body: {"field": <updated value>}
```
--------

+ Update user's plant reference

```
method: POST
URI: /api/account/{account_id}/set_plant/{plant_id}
body: {}
```
-----------------------

+ Search plants

```
method: POST
URI: /api/plant/search
body: {empty or search request} // the same logic
```
------------

+ Get one plant by Id

```
method: POST
URI: /api/plant/{id}
body: {}
```
------------

+ Update the FIRST level fields of plant

```
method:   POST
URI:         /api/plant/{id}/update
body: {"field": <updated value>}
```
--------------

+ Update car list (second level) in plant

```
method:   POST
URI:         /api/plant/{id}/update_cars
body: {
	"car_ids":["590a1d740956052284a664be","590a1d740956052284a664b7", ... ]
}
ATTENTION!!! you should add ALL cars to the array. (So you can add/remove cars by the same method) 
```
-----------------------------

+ Delete user

```
method:   POST
URI:         /api/account/{id}/delete
```
------------------

+ Delete plant

```
method:   POST
URI:         /api/plant/{id}/delete
```
-----------

+ Create plant
```
method:   POST
URI:         /api/plant/create
body: {plant object}
```
--------

+ Methods to manage cars

```
POST.
/api/cars/create
/api/cars/search
/api/cars/{id}
/api/cars/{id}/update
/api/cars/{id}/delete
```


+ How to add/remove cars fast without composing car arrays

```
POST /api/plant/{id}/add_car/{car_id}
POST /api/plant/{id}/remove_car/{car_id}
```
