import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/querymv")   // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
public class QueryServlet extends HttpServlet {

   // The doGet() runs once per HTTP GET request to this servlet.
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException {
      // Set the MIME type for the response message
      response.setContentType("text/html");
      // Get a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();
      // Print an HTML page as the output of the query
      out.println("<html>");
      out.println("<head><title>Query Response</title></head>");
      out.println("<body>");

      try (
         // Step 1: Allocate a database 'Connection' object
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/eflowershop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "myuser", "xxxx");   // For MySQL
               // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

         // Step 2: Allocate a 'Statement' object in the Connection
             Statement stmt = conn.createStatement();
      ) {
            // Step 3: Execute a SQL SELECT query
         String[] name = request.getParameterValues("flowers"); 
         String[] country = request.getParameterValues("country");
         String price = request.getParameter("price");
          // Returns an array of Strings
         String sqlStr = "SELECT * FROM flowers WHERE name IN (";
         for (int i = 0; i < name.length; ++i) {
            if (i < name.length - 1) {
               sqlStr += "'" + name[i] + "', ";  // need a commas
            } else {
               sqlStr += "'" + name[i] + "'";    // no commas
            }
         }
         sqlStr += ")AND price <= " + price + " AND country IN (";
         for (int i = 0; i < country.length; ++i) {
            if (i < country.length - 1) {
               sqlStr += "'" + country[i] + "', ";  // need a commas
            } else {
               sqlStr += "'" + country[i] + "'";    // no commas
            }
         }
         sqlStr += ")";
         out.println("<style>body {background-color:  #c2c7da ;}h3 { color: #ed3acc;   font-family: verdana;   letter-spacing: 2px;   text-shadow: 2px 1px black;}</style>");
         out.println("<div align ='center'>");        
         out.println("<h3>Thank you for your query.</h3>");
         out.println("</div>"); // Echo for debugging

         /*out.println("<p>Your SQL statement is: " + sqlStr + "</p>");*/ // Echo for debugging
         ResultSet rset = stmt.executeQuery(sqlStr);  // Send the query to the server

         // Step 4: Process the query result set
         out.println("<form method='get' action='eshoporder'>");
         int count = 0;
       
         while(rset.next()) {
            // Print a paragraph <p>...</p> for each record
           int qty = rset.getInt("qty");
           out.println("<div align ='center'>"); 
               out.println("<p><input type='checkbox' name = 'id' value='" 
                  + rset.getString("id") + "' />"
                  + rset.getString("name")
                  + ", " + rset.getString("country")
                  + ", $" + rset.getFloat("price") + " Qty left: " + rset.getInt("qty"));
               if (qty != 0){
               out.println(" </p> <p> <b> Select qty of flowers</b>: <input type='text' name ='qty' pattern ='[0-9]{1,}'/>" 
                  + "</p>" + " <br />");
               } else {
                  out.println("<p> Sorry, we have run out of these flowers. </p> ");
               }
            
            count++;
         }
         if (count == 0){
            out.println("<p> There are no such flowers available. Sorry. Do key in your details to be updated on a restock! </p>");
           }
         out.println(" <p>Enter your Name: <input type='text' name='cust_name' /></p>"
           + "<p>Enter your Email: <input type='email' name='cust_email' /></p>" 
           +"<p>Enter your Phone Number: <input type='text' name='cust_phone'pattern = [0-9]{8} /></p>");    
         out.println("<p><input type='submit' value='ORDER' />");
         out.println("</form>");
         out.println("<p>==== " + count + " records found =====</p>");
         out.println("</div>"); // Echo for debugging

      } 
               catch(Exception ex) {
         out.println("<p>Error: " + ex.getMessage() + "</p>");
         out.println("<p>Check Tomcat console for details.</p>");
         ex.printStackTrace();
      }  // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)
 
      out.println("</body></html>");
      out.close();
   }
}