/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import javax.servlet.ServletException;

/**
 *
 * @author aditi
 */

@WebServlet(name = "EmployeeServlet", urlPatterns = "/employeeServlet")
public class JSONServlet extends HttpServlet {

    private final Gson gson = new Gson();

    static final String JDBC_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    static final String DB_URL = "jdbc:derby://localhost:1527/SmartShopping";

    //  Database credentials
    static final String USER = "prathee13";
    static final String PASS = "prathee13";

    String mode;
    String id;
    String location;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

//        // JDBC driver name and database URL
        Connection conn = null;
        Statement stmt = null;

        try {
            //Register JDBC driver
            Class.forName("org.apache.derby.jdbc.ClientDriver");

            //Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //STEP 4a: Execute a query
//      System.out.println("Inserting values...");
//      stmt = conn.createStatement();
//      String sql;
//      sql = "insert into PRATHEE13.Cart values(1, 1)";
//      System.out.println(stmt.executeUpdate(sql));
//      
            //STEP 4b: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();

//       RequestDispatcher dispatcher = request.getRequestDispatcher("/JSONServlet?id=1&location=1&mode=1");   
            mode = request.getParameter("mode");
            id = request.getParameter("id");

            if (mode.equals("1")) {
                location = request.getParameter("location");
                //Update Cart in Database
                stmt = conn.createStatement();
                String sql = "update PRATHEE13.Cart set location=" + location + " where id=" + id;
                if (stmt.executeUpdate(sql) == 1) {
                    System.out.println("OHHHHHHHHHHHHHHHHHHHHH");
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
                return;
            } else {
                int cartId;
                String cartLocation;
                Cart cart;
                String result = "{}";
                String sql = "SELECT * FROM PRATHEE13.Cart where id=" + id;
                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    //Retrieve by column name
                    System.out.println("The cart is present in the location:");
                    cartId = rs.getInt("id");
                    cartLocation = rs.getString("location");
                    cart = new Cart(cartId, cartLocation);
                    result = this.gson.toJson(cart);
                    //Display values
                    System.out.print("ID: " + id);
                    System.out.print(", Location: " + location);
                    PrintWriter out = response.getWriter();
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    out.print(result);
                    out.flush();
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
                rs.close();
            }
            //Clean-up environment
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
