# Department Library Code Sample

This mini-project is intended to showcase some of my coding skills in Java.

## Background

The CS department of a fictional university runs a small library of textbooks for graduate students to borrow. 
The "library" is in fact just a bookshelf that students can take books from and return the books to, so the department
would like to keep track of which books are there and which books are not. 
The library has had a long history of success and so the department isn't worried about trying to track who borrowed which
book and when it will be returned, since the graduate students at this university are (in this imaginary world) very good
at returning the books when they are done.

However, graduate students are very busy and so the application needs to be as fast as possible. Trying to hook it up to 
a database on the CS department server would be too slow (the department is applying for a better infrastructure budget)
so the library needs to be stored in memory instead. For maximum efficiency, splay trees are used behind the scenes to store
the books.

This project currently contains the API for this library app. I'd like to expand it by adding simple React-based single-page-app frontend,
so that users can actually use the API. 

## Tech stack and libraries

* Java 11
* Maven
* Spring Boot
* OpenCSV
* IntelliJ (project configuration checked in for consistency across users)

## Running the application

1. Clone the repository
2. Install the Maven dependencies (this also runs the tests)
```
mvnw install
```
3. Run DepartmentLibraryApplication (IntelliJ run configuration provided)
4. Hit the API at e.g. `http://localhost:8080/api/library/searchByAuthor?authorName=Thomas%20H%20Cormen`
(Inspect `base_library.tsv` for valid author names and ISBNs.)

## A note on splay trees

Naturally, there are other implementations of splay trees available and there's no need in real life to hand-write an
implementation of splay trees. However it's an opportunity to demonstrate some more algorithmic code to constrast with the
structural code of a web application.

The splay tree code is based off one of the larger programming assignments for our Data Structures 
class at Brandeis, which involved a console application backed by in-memory splay trees.

## TODOs

* Migrate the rest of BookSplayTreeServiceTest and LibraryServiceTest to Hamcrest matchers (since they're nicer than the JUnit ones
but vanilla JUnit was required for the assignment this project was based on).
* Add a React or other single-page-app frontend
* Persist the splay trees to a database when the application is shut down, and load the base library from the same database 
if no trees were persisted