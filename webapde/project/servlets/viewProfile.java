package webapde.project.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import webapde.project.beans.*;

import com.mysql.jdbc.Statement;
/**
 * Servlet implementation class viewProfile
 */
@WebServlet("/viewProfile")
public class viewProfile extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public viewProfile() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		// JDBC driver name and database URL
	    final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	    final String DB_URL="jdbc:mysql://localhost:3306/webapdedb";
	    //  Database credentials
	    final String USER = "root";
	    final String PASS = "1234";
		int photoOwnerNum = Integer.parseInt(request.getParameter("ownerId"));
		System.out.println(photoOwnerNum);
		try {
	         // Register JDBC driver
	         Class.forName(JDBC_DRIVER);

	         // Open a connection
	         Connection db = DriverManager.getConnection(DB_URL, USER, PASS);
	         
	         //SQL STATEMENT
	         Statement getPhotos = (Statement)db.createStatement();
	         String sql = "SELECT a.*, b.*\r\n" + 
		         		"FROM photos a, users b\r\n" + 
		         		"WHERE a.owner_id = " + photoOwnerNum + " and a.owner_id = b.user_id";
	         ResultSet resultPhotos = getPhotos.executeQuery(sql);
	         ArrayList<Photo> photos = new ArrayList();
	         String ownerName = "", description = "", ownerId = "";
	         
	         while(resultPhotos.next()) {
        		 Photo newPhoto = new Photo();
        		 newPhoto.setId(resultPhotos.getInt("photo_id"));
        		 newPhoto.setDescription(resultPhotos.getString("photo_description"));
        		 newPhoto.setFormat(resultPhotos.getString("photo_format"));
        		 newPhoto.setPrivacy(resultPhotos.getString("photo_privacy").equals("private"));
        		 newPhoto.setTitle(resultPhotos.getString("photo_title"));
        		 newPhoto.setURL(resultPhotos.getString("photo_path"));
        		 newPhoto.setOwner(resultPhotos.getString("user_name"));
        		 newPhoto.setOwnerId(resultPhotos.getInt("owner_id"));
        		 
        		 /*ownerName = resultPhotos.getString("user_name");
        		 description = resultPhotos.getString("user_description");
        		 ownerId = resultPhotos.getString("owner_id");*/
        		 
        		 String tagSql = "SELECT a.*\r\n" + 
        		 		"FROM tags a left join photos b\r\n" + 
        		 		"ON a.photo_id = b.photo_id and b.photo_privacy = \"public\"";
        		 
        		 Statement getTags = (Statement)db.createStatement();
        		 ResultSet resultTags = getTags.executeQuery(tagSql);
    	         ArrayList<String> tags = new ArrayList();
        		 while(resultTags.next())
        			 tags.add(resultTags.getString("photo_tag"));
        		 newPhoto.setTags(tags);
        		 
        		 photos.add(newPhoto);
        		 
        		 getTags.close();
        		 resultTags.close();
	         }
	         //SQL STATEMENT
	         Statement getAllUsers = (Statement)db.createStatement();
	         sql = "SELECT user_id, user_name, user_description FROM users";
	         ResultSet users = getAllUsers.executeQuery(sql);
	         
	         //LOOPING THROUGH THE RESULT
	         while(users.next()) {
	        	 //CHECKING IF USER IS IN DATABASE
	        	 String userId = users.getString("user_id");
	        	 if(photoOwnerNum == Integer.parseInt(userId)){
	        		 ownerName = users.getString("user_name");
	        		 description = users.getString("user_description");
	        		 ownerId = users.getString("user_id");
	     			break;
	        	 }
	         }
	         Collections.reverse((List)photos);
	         
	         request.setAttribute("photos", photos);
	         request.setAttribute("owner", ownerName);
	         request.setAttribute("description", description);
	         request.setAttribute("userId",ownerId);
	         request.getSession().setAttribute("profilePhotos", photos);
	         request.getRequestDispatcher("profile.jsp").forward(request, response);
	         
	         getAllUsers.close();
	         users.close();
	         getPhotos.close();
	         resultPhotos.close();
	         db.close();
		}catch(Exception e) {System.out.println(e);}
	    finally{} 
	}

}
