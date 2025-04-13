package ssau.nosql_1.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ssau.nosql_1.dao.util.DaoFactory;
import ssau.nosql_1.model.Artist;

import java.io.IOException;
import java.util.List;

@WebServlet("/artists")
public class ArtistServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        int artistId;
        switch (action != null ? action : "") {
            case "" -> {
                List<Artist> artists = DaoFactory.getArtistDao().getAllArtists();
                request.setAttribute("artists", artists);
                List<Object[]> artistsWithAlbums = DaoFactory.getArtistDao().getArtistWithAlbums();
                request.setAttribute("artistsWithAlbums", artistsWithAlbums);
                getServletContext().getRequestDispatcher("/artists.jsp").forward(request, response);
            }
            case "edit" -> {
                if (request.getParameter("id") == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Don't' exists parameter id");
                    return;
                }
                artistId = Integer.parseInt(request.getParameter("id"));
                Artist artist = DaoFactory.getArtistDao().getArtistById(artistId);
                request.setAttribute("artist", artist);
                getServletContext().getRequestDispatcher("/editArtist.jsp").forward(request, response);
            }
            case "delete" -> {
                if (request.getParameter("id") == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Don't' exists parameter id");
                    return;
                }
                artistId = Integer.parseInt(request.getParameter("id"));
                DaoFactory.getArtistDao().deleteArtist(artistId);
                response.setStatus(HttpServletResponse.SC_OK);
            }
            case "showAlbums" -> {
                if (request.getParameter("artistId") == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Don't' exists parameter id");
                    return;
                }
                artistId = Integer.parseInt(request.getParameter("artistId"));
                response.sendRedirect("albums?id=" + artistId);
            }
            default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action parameter");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "edit" -> {
                String name = request.getParameter("name");
                String id = request.getParameter("id");
                if (id != null && !id.isEmpty()) {
                    int artistId = Integer.parseInt(id);
                    DaoFactory.getArtistDao().updateArtist(artistId, name);
                }
                response.sendRedirect("artists");
            }
            case "save" -> {
                String name = request.getParameter("name");
                DaoFactory.getArtistDao().createArtist(name);
            }
            default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action parameter");
        }
    }
}