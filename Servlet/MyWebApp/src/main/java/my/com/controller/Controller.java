package my.com.controller;

import my.com.db.DBException;
import my.com.db.DBManager;
import my.com.db.entity.Department;
import my.com.db.entity.Worker;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Controller {
    DBManager dbManager;

    public Controller() {
        dbManager = DBManager.getInstance();
    }

    public void updateWorker(HttpServletRequest request, HttpServletResponse response) throws DBException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        int departmentId = Integer.parseInt(request.getParameter("department"));

        Worker book = new Worker(id, name, departmentId);
        dbManager.updateWorker(book);
        response.sendRedirect("list-worker");
    }

    public void showEditWorkerForm(HttpServletRequest request, HttpServletResponse response) throws DBException, ServletException, IOException {
        List<Department> departmentList = dbManager.findAllDepartments();
        request.setAttribute("listDepartment", departmentList);


        int id = Integer.parseInt(request.getParameter("id"));
        Worker existingUser = dbManager.getWorker(id);

        RequestDispatcher dispatcher = request.getRequestDispatcher("worker-form.jsp");
        request.setAttribute("worker", existingUser);

        dispatcher.forward(request, response);
    }

    public void deleteWorker(HttpServletRequest request, HttpServletResponse response) throws DBException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        //System.out.println(id);
        dbManager.deleteWorkers(dbManager.getWorker(id));
        response.sendRedirect("list-worker");
    }

    public void insertWorker(HttpServletRequest request, HttpServletResponse response) throws DBException, IOException {
        String name = request.getParameter("name");
        //System.out.println(request.getParameter("department"));
        int departmentId = Integer.parseInt(request.getParameter("department"));
        //Department newUser = new Department(name);
        dbManager.insertWorker(Worker.createWorker(name, departmentId));
        response.sendRedirect("list-worker");
    }

    public void showNewWorkerForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DBException {
        List<Department> departmentList = dbManager.findAllDepartments();
        request.setAttribute("listDepartment", departmentList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("worker-form.jsp");
        dispatcher.forward(request, response);
    }

    public void listWorkers(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, DBException {
        List<Worker> listWorkers = dbManager.findAllWorkers();
        request.setAttribute("listWorkers", listWorkers);
        List<Department> departmentList = dbManager.findAllDepartments();

        Map<Integer, String> departmentMap = departmentList.stream().collect(Collectors.toMap(Department::getId, Department::getName));
        request.setAttribute("listDepartments", departmentMap);
        RequestDispatcher dispatcher = request.getRequestDispatcher("worker-list.jsp");
        dispatcher.forward(request, response);
    }
    //##############################################################################################################
    public void listDepartments(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, DBException {
        List<Department> listUser = dbManager.findAllDepartments();
        request.setAttribute("listDepartment", listUser);
        RequestDispatcher dispatcher = request.getRequestDispatcher("department-list.jsp");
        dispatcher.forward(request, response);
    }
    public void showNewDepartmentForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("department-form.jsp");
        dispatcher.forward(request, response);
    }

    public void showEditDepartmentForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, DBException {
        int id = Integer.parseInt(request.getParameter("id"));
        Department existingUser = dbManager.getDepartment(id);
        RequestDispatcher dispatcher = request.getRequestDispatcher("department-form.jsp");
        request.setAttribute("department", existingUser);
        dispatcher.forward(request, response);

    }

    public void insertDepartment(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, DBException {
        String name = request.getParameter("name");
        //Department newUser = new Department(name);
        dbManager.insertDepartment(Department.createDepartment(name));
        response.sendRedirect("list");
    }

    public void updateDepartment(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, DBException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");

        Department book = new Department(id, name);
        dbManager.updateDepartment(book);
        response.sendRedirect("list");
    }

    public void deleteDepartment(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, DBException {
        int id = Integer.parseInt(request.getParameter("id"));
        //System.out.println(id);
        dbManager.deleteDepartments(dbManager.getDepartment(id));
        response.sendRedirect("list");

    }
}
