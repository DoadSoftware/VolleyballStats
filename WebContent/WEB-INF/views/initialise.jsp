<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>

<html>

<head>

    <meta charset="UTF-8">

    <title>VolleyballStats - Initialise</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 40px;
            background: #f5f6f8;
        }

        .card {
            max-width: 850px;
            margin: auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,.08);
        }

        h1 {
            margin-top: 0;
        }

        label {
            display: block;
            font-weight: bold;
            margin-top: 18px;
            margin-bottom: 6px;
        }

        input[type=text],
        input[type=file] {
            width: 100%;
            box-sizing: border-box;
            padding: 10px;
        }

        button {
            margin-top: 24px;
            padding: 11px 22px;
            cursor: pointer;
        }

        .error {
            background: #ffe3e3;
            color: #9b0000;
            padding: 12px;
            margin-bottom: 18px;
            border-radius: 4px;
        }

        .hint {
            color: #666;
            font-size: 13px;
            margin-top: 6px;
        }

        .saved {
            background: #eef6ff;
            border: 1px solid #c9e0ff;
            padding: 12px;
            margin-top: 8px;
            border-radius: 4px;
            word-break: break-all;
        }

    </style>

</head>

<body>

<div class="card">

    <h1>VolleyballStats</h1>

    <h3>Initialise DVStat</h3>

    <% if (request.getAttribute("error") != null) { %>

        <div class="error">
            <%= request.getAttribute("error") %>
        </div>

    <% } %>

    <form
            method="post"
            action="${pageContext.request.contextPath}/initialize"
            enctype="multipart/form-data">

        <label for="dllFile">DVStat.dll</label>

        <input
                id="dllFile"
                name="dllFile"
                type="file"
                accept=".dll">

        <div class="hint">
            Select the Genius Sports DVStat.dll only if you want to
            upload or replace the currently saved DLL.
        </div>

        <% if (request.getAttribute("dllPath") != null
                && !request.getAttribute("dllPath").toString().isEmpty()) { %>

            <div class="saved">

                <strong>Currently saved DLL:</strong><br>

                <%= request.getAttribute("dllPath") %>

            </div>

        <% } %>

        <label for="statisticsFolder">
            Statistics Folder
        </label>

        <input
                id="statisticsFolder"
                name="statisticsFolder"
                type="text"
                value="<%= request.getAttribute("statisticsFolder") == null
                        ? ""
                        : request.getAttribute("statisticsFolder") %>"
                placeholder="C:\Sports\DVStat4"
                required>

        <div class="hint">
            For live use this can be a local DV-Share folder or a
            network path such as \\ScoutmanComputer\DV-Share.
        </div>

        <button type="submit">
            Submit &amp; View Statistics
        </button>

    </form>

</div>

</body>

</html>