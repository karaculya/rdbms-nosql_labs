package main.java.ssau.nosql_1.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Hibernate;
import ssau.nosql_1.dao.util.DaoFactory;
import ssau.nosql_1.model.Album;

import java.io.IOException;

@WebServlet(urlPatterns = {"/albums", "/albums*"})
public class AlbumServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action != null ? action : "") {
            case "" -> {
                if (request.getParameter("id") != null) {
                    int artistId = Integer.parseInt(request.getParameter("id"));
                    request.setAttribute("numAlbumsAndComp",
                            DaoFactory.getArtistDao().getArtistAlbumAndCompositionCountById(artistId));
                    request.setAttribute("albums",
                            DaoFactory.getAlbumDao().getAllAlbumsByArtistId(artistId));
                } else {
                    request.setAttribute("albums", DaoFactory.getAlbumDao().getAllAlbums());
                }
                getServletContext().getRequestDispatcher("/albums.jsp").forward(request, response);
            }
            case "delete" -> {
                if (request.getParameter("id") == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Don't' exists parameter id");
                    return;
                }
                DaoFactory.getAlbumDao().deleteAlbum(Integer.parseInt(request.getParameter("id")));
                response.setStatus(HttpServletResponse.SC_OK);
            }
            case "edit" -> {
                if (request.getParameter("id") == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Don't' exists parameter id");
                    return;
                }
                Album album = DaoFactory.getAlbumDao().getAlbumById(Integer.parseInt(request.getParameter("id")));
                Hibernate.initialize(album.getArtist());
                request.setAttribute("album", album);
                getServletContext().getRequestDispatcher("/editAlbum.jsp").forward(request, response);
            }
            case "showCompositions" -> {
                if (request.getParameter("id") == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Don't' exists parameter id");
                    return;
                }
                response.sendRedirect("compositions?id="
                        + Integer.parseInt(request.getParameter("id")));
            }
            default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action parameter");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "edit" -> {
                String id = request.getParameter("id");
                String name = request.getParameter("name");
                String genre = request.getParameter("genre");
                if (id != null && !id.isEmpty()) {
                    int albumId = Integer.parseInt(id);
                    DaoFactory.getAlbumDao().updateAlbum(albumId, name, genre);
                    response.sendRedirect("albums?id=" + Integer.parseInt(request.getParameter("id")));
                }
            }
            case "save" -> {
                int idArtist = Integer.parseInt(request.getParameter("id"));
                System.out.println(idArtist);
                String name = request.getParameter("name");
                System.out.println(name);
                String genre = request.getParameter("genre");
                System.out.println(genre);
                DaoFactory.getAlbumDao().createAlbum(idArtist, name, genre);
            }
            default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action parameter");
        }
    }
}
