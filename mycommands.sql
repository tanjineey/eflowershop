SELECT books.title, books.price, books.qty,authors.name, authors.email
   FROM books, books_authors, authors
   WHERE books.isbn = books_authors.isbn
      AND authors.authorID = books_authors.authorID
      AND books.title like 'Java%';

/Users/jeraltan/Downloads/mysql-connector-java-8.0.19/mysql-connector-java-8.0.19.jar

java -cp .:$HOME/myWebProject/mysql-connector-java-8.0.19/mysql-connector-java-8.0.19.jar JdbcSelectTest