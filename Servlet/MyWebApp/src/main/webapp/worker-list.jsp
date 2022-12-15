<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
  <title>My Application</title>
  <link rel="stylesheet"
        href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
        integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
        crossorigin="anonymous">
</head>
<body>

<header>
  <nav class="navbar navbar-expand-md navbar-dark"
       style="background-color: tomato">
    <div>
      <a href="https://www.javaguides.net" class="navbar-brand"> My App </a>
    </div>

    <ul class="navbar-nav">
      <li><a href="<%=request.getContextPath()%>/list"
             class="nav-link">Departments</a></li>
      <li><a href="<%=request.getContextPath()%>/list-worker"
             class="nav-link">Workers</a></li>
    </ul>
  </nav>
</header>
<br>

<div class="row">
  <!-- <div class="alert alert-success" *ngIf='message'>{{message}}</div> -->

  <div class="container">
    <h3 class="text-center">List of Departments</h3>
    <hr>
    <div class="container text-left">

      <a href="<%=request.getContextPath()%>/new-worker" class="btn btn-success">Add
        New Worker</a>
    </div>
    <br>
    <table class="table table-bordered">
      <thead>
      <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Department</th>
      </tr>
      </thead>
      <tbody>
      <!--   for (Todo todo: todos) {  -->
      <c:forEach var="worker" items="${listWorkers}">

        <tr>
          <td><c:out value="${worker.id}" /></td>
          <td><c:out value="${worker.name}" /></td>
          <td><c:out value="${listDepartments[worker.departmentId]}" /></td>
          <td><a href="edit-worker?id=<c:out value='${worker.id}' />">Edit</a>
            &nbsp;&nbsp;&nbsp;&nbsp; <a
                    href="delete-worker?id=<c:out value='${worker.id}' />">Delete</a></td>
        </tr>
      </c:forEach>
      <!-- } -->
      </tbody>
    </table>
  </div>
</div>
</body>
</html>