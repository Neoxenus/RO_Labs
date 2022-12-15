package my.com.web;

import my.com.controller.Controller;
import my.com.db.DBException;

import java.io.*;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/")
public class MyServlet extends HttpServlet {
    Controller controller = new Controller();

    @Override
    public void init() throws ServletException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();

        try {
            switch (action) {
                case "/new-department" -> controller.showNewDepartmentForm(request, response);
                case "/insert-department" -> controller.insertDepartment(request, response);
                case "/delete-department" -> controller.deleteDepartment(request, response);
                case "/edit-department" -> controller.showEditDepartmentForm(request, response);
                case "/update-department" -> controller.updateDepartment(request, response);

                case "/list-worker" ->  controller.listWorkers(request, response);

                case "/new-worker" ->  controller.showNewWorkerForm(request, response);
                case "/insert-worker" ->  controller.insertWorker(request, response);
                case "/delete-worker" ->  controller.deleteWorker(request, response);
                case "/edit-worker" ->  controller.showEditWorkerForm(request, response);
                case "/update-worker" ->  controller.updateWorker(request, response);
                default -> controller.listDepartments(request, response);
            }
        } catch (DBException | SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void destroy() {
        // do nothing.
    }
}