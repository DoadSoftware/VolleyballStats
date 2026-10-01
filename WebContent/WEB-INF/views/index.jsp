<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>

<html>

<head>

    <meta charset="UTF-8">

    <title>VolleyballStats - Statistics</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 30px;
            background: #f5f6f8;
        }

        .container {
            max-width: 1100px;
            margin: auto;
        }

        .card {
            background: white;
            padding: 22px;
            margin-bottom: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,.07);
        }

        h1 {
            margin-top: 0;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 9px;
            text-align: left;
        }

        th {
            background: #f0f1f3;
        }

        .error {
            background: #ffe3e3;
            color: #9b0000;
            padding: 12px;
            border-radius: 4px;
        }

        .meta {
            color: #555;
            font-size: 14px;
        }

        .back {
            display: inline-block;
            margin-bottom: 15px;
        }

    </style>

</head>

<body>

<div class="container">

    <a class="back" href="${pageContext.request.contextPath}/">← Re-initialise</a>

    <div class="card">

        <h1>VolleyballStats</h1>

        <p class="meta">
            <strong>DLL:</strong> ${dllPath}
        </p>

        <p class="meta">
            <strong>Statistics folder:</strong> ${statisticsFolder}
        </p>

        <p class="meta">
            <strong>Selection:</strong> Home team | Team total | All skills | Match
        </p>

    </div>

    <% if (request.getAttribute("stats") != null
            && ((com.volleyball.dvstat.service.VolleyballStats) request.getAttribute("stats")).getErrorMessage() != null) { %>

        <div class="card error">

            <strong>DVStat error:</strong>

            <%= ((com.volleyball.dvstat.service.VolleyballStats) request.getAttribute("stats")).getErrorMessage() %>

        </div>

    <% } else { %>

        <div class="card">

            <h2>Points / Errors</h2>

            <table>

                <tr>
                    <th>Points</th>
                    <th>Errors</th>
                    <th>Total Events</th>
                </tr>

                <tr>
                    <td>${stats.points}</td>
                    <td>${stats.errors}</td>
                    <td>${stats.totalEvents}</td>
                </tr>

            </table>

        </div>

        <div class="card">

            <h2>Reception</h2>

            <table>

                <tr>
                    <th>Total Actions</th>
                    <th>Positivity %</th>
                </tr>

                <tr>
                    <td>${stats.receptionTotal}</td>
                    <td>${stats.receptionPositive}</td>
                </tr>

            </table>

        </div>

        <div class="card">

            <h2>Attack</h2>

            <table>

                <tr>
                    <th>Total Actions</th>
                    <th>Kill / Attack %</th>
                </tr>

                <tr>
                    <td>${stats.attackTotal}</td>
                    <td>${stats.attackPercentage}</td>
                </tr>

            </table>

        </div>

        <div class="card">

            <h2>Symbol Statistics</h2>

            <table>

                <tr>
                    <th>MM</th>
                    <th>M</th>
                    <th>S</th>
                    <th>P</th>
                    <th>D</th>
                    <th>E</th>
                    <th>Total</th>
                </tr>

                <tr>
                    <td>${stats.mm}</td>
                    <td>${stats.m}</td>
                    <td>${stats.s}</td>
                    <td>${stats.p}</td>
                    <td>${stats.d}</td>
                    <td>${stats.e}</td>
                    <td>${stats.totalSymbols}</td>
                </tr>

            </table>

        </div>

        <div class="card">

            <h2>Extended Symbol Statistics</h2>

            <table>

                <tr>
                    <th>MM</th>
                    <th>MMP</th>
                    <th>MMC</th>
                    <th>M</th>
                    <th>S</th>
                    <th>SP</th>
                    <th>SC</th>
                    <th>P</th>
                    <th>D</th>
                    <th>DP</th>
                    <th>DC</th>
                    <th>E</th>
                    <th>Total</th>
                </tr>

				<tr>
				    <td>${stats.mm}</td>
				    <td>${stats.mmp}</td>
				    <td>${stats.mmc}</td>
				    <td>${stats.m}</td>
				    <td>${stats.s}</td>
				    <td>${stats.sp}</td>
				    <td>${stats.sc}</td>
				    <td>${stats.p}</td>
				    <td>${stats.d}</td>
				    <td>${stats.dp}</td>
				    <td>${stats.dc}</td>
				    <td>${stats.e}</td>
				    <td>${stats.totalSymbols}</td>
				</tr>

            </table>

        </div>

    <% } %>

</div>

</body>

</html>