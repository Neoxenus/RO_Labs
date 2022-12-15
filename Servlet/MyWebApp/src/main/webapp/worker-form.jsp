<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
  <title>My Application</title>
  <link rel="stylesheet"
        href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
        href="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.14/dist/css/bootstrap-select.min.css"
        integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
        crossorigin="anonymous">

  <!-- Latest compiled and minified JavaScript -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.14/dist/js/bootstrap-select.min.js"></script>

  <!-- (Optional) Latest compiled and minified JavaScript translation files -->
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
<div class="container col-md-5">
  <div class="card">
    <div class="card-body">
      <c:if test="${worker != null}">
      <form action="update-worker" method="post">
        </c:if>
        <c:if test="${worker == null}">
        <form action="insert-worker" method="post">
          </c:if>

          <caption>
            <h2>
              <c:if test="${worker != null}">
                Edit Worker
              </c:if>
              <c:if test="${worker == null}">
                Add New Worker
              </c:if>
            </h2>
          </caption>

          <c:if test="${worker != null}">
            <input type="hidden" name="id" value="<c:out value='${worker.id}' />" />
          </c:if>

          <fieldset class="form-group">
            <label>Worker Name</label> <input type="text"
                                                  value="<c:out value='${worker.name}' />" class="form-control"
                                                  name="name" required="required">
          </fieldset>
            <fieldset class="form-group">
              <label>Department</label>
              <br>
              <select name="department" class="form-control form-select-lg"  required="required">
              <c:forEach items="${listDepartment}" var="department">
                <option value="${department.id}"
                <c:if test="${department.id == worker.departmentId}">
                selected="selected"
                  </c:if>
                >${department.name}</option>
                <br>
              </c:forEach>

            </select>
            </fieldset>

          <button type="submit" class="btn btn-success">Save</button>
        </form>
    </div>
  </div>
</div>
</body>
</html>