import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/eshoporder")   // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
public class EflowershopOrderServlet extends HttpServlet {

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
         // Step 3 & 4: Execute a SQL SELECT query and Process the query result
         // Retrieve the flowers' id. Can order more than one flower.
         String[] ids = request.getParameterValues("id");
         String[] qty = request.getParameterValues("qty");
         String[] cust_names = request.getParameterValues("cust_name");
         String[] cust_emails = request.getParameterValues("cust_email");
         String[] cust_phones = request.getParameterValues("cust_phone");

         //out.println("INSERT INTO order_records VALUES (" + ids[0] + ", 1, '" + cust_names[0] + "', '" + cust_emails[0] + "', '" + cust_phones[0] + "')");

         if (ids != null) {
            String sqlStr;
            int count;
            // Process each of the flowers
            for (int i = 0; i < ids.length; ++i) {
               // Update the qty of the table flowers
               sqlStr = "UPDATE flowers SET qty = qty -"+ qty[i] + " WHERE id = '" + ids[i] + "'";
               out.println("<p>" + sqlStr + "</p>");  // for debugging
               count = stmt.executeUpdate(sqlStr);
               out.println("<p>" + count + " record updated.</p>");
 
               // Create a transaction record
               sqlStr = "INSERT INTO order_records VALUES ('"
                     + ids[i] + "', '" + qty[i] +"','" + cust_names[0] + "', '" + cust_emails[0] + "', '" 
                     + cust_phones[0] +  "')";
               out.println("<p>" + sqlStr + "</p>");  // for debugging
               count = stmt.executeUpdate(sqlStr);
               out.println("<p>" + count + " record inserted.</p>");
               out.println("<h3>Your order for flower id=" + ids[i]
                     + " has been confirmed.</h3>");
            }
            out.println("<h3>Thank you.<h3>");
         } else { // No book selected
            out.println("<h3>Please go back and select flowers...</h3>");
         } 
         out.println("<form method = 'get'action=' queryflowerfinal.html'>");
      out.println("<br /><br />") ;
      out.println("<input type='submit' value='Back' />") ;
      out.println("  </form>");

         } catch(Exception ex) {
         out.println("<p>Error: " + ex.getMessage() + "</p>");
         out.println("<p>Check Tomcat console for details.</p>");
         ex.printStackTrace();
      }  // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)
 
      out.println("</body></html>");
      out.close();
   }
}