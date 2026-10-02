<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.volleyball.dvstat.model.MatchInfo" %>
<%@ page import="com.volleyball.dvstat.model.MatchSetup" %>
<%@ page import="com.volleyball.dvstat.model.MatchStatistics" %>
<%@ page import="com.volleyball.dvstat.model.TeamStatistics" %>
<%@ page import="com.volleyball.dvstat.model.PlayerStatistics" %>
<%@ page import="com.volleyball.dvstat.model.PlayerInfo" %>
<%@ page import="com.volleyball.dvstat.model.StatisticEntry" %>

<%
MatchSetup matchSetup = (MatchSetup) request.getAttribute("matchSetup");
MatchStatistics matchStatistics = (MatchStatistics) request.getAttribute("matchStatistics");
MatchInfo matchInfo = matchSetup == null ? null : matchSetup.getMatchInfo();
String error = (String) request.getAttribute("error");
%>
<%!
private String formatSetScore(String rawScore) {
    if (rawScore == null || rawScore.trim().isEmpty()) {
        return "";
    }

    String[] parts = rawScore.split(";");

    if (parts.length < 2) {
        return rawScore;
    }

    for (int i = parts.length - 1; i >= 0; i--) {
        String value = parts[i].trim();

        if (value.matches("\\d+\\s*-\\s*\\d+")) {
            return value.replaceAll("\\s+", "");
        }
    }

    return rawScore;
}
%>
<!DOCTYPE html>

<html>
<head>
<meta charset="UTF-8">
<title>VolleyballStats</title>

<style>
* {
    box-sizing: border-box;
}

html, body {
    width: 100%;
    height: 100%;
    margin: 0;
    overflow: hidden;
    font-family: Arial, sans-serif;
    background: #eef1f4;
    color: #20252b;
}

.page {
    width: 100%;
    height: 100vh;
    display: flex;
    flex-direction: column;
}

.header {
    flex-shrink: 0;
    background: #18232d;
    color: white;
    padding: 8px 12px;
}

.header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 20px;
}

.title {
    font-size: 20px;
    font-weight: bold;
}

.match-title {
    font-size: 16px;
    font-weight: bold;
    margin-top: 2px;
}

.match-meta {
    text-align: right;
    font-size: 11px;
    color: #d5dce2;
    line-height: 1.5;
}

.scores {
    display: flex;
    gap: 5px;
    margin-top: 7px;
    flex-wrap: wrap;
}

.score {
    background: white;
    color: #18232d;
    border-radius: 3px;
    padding: 3px 7px;
    font-size: 10px;
    font-weight: bold;
}

.content {
    flex: 1;
    min-height: 0;
    overflow: auto;
    padding: 8px;
}

.error {
    background: #ffe0e0;
    border: 1px solid #e0a0a0;
    color: #9a0000;
    padding: 12px;
    border-radius: 5px;
}

.teams {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 8px;
    min-width: 1200px;
}

.team {
    background: white;
    border: 1px solid #d4d9de;
    border-radius: 5px;
    overflow: hidden;
}

.team-header {
    background: #263746;
    color: white;
    padding: 6px 9px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.team-name {
    font-size: 16px;
    font-weight: bold;
}

.team-side {
    font-size: 9px;
    background: rgba(255, 255, 255, .15);
    padding: 3px 6px;
    border-radius: 3px;
}

.section {
    padding: 6px;
    border-bottom: 1px solid #e0e3e6;
}

.section-title {
    font-size: 11px;
    font-weight: bold;
    color: #3b4650;
    margin-bottom: 5px;
}

.table-wrap {
    width: 100%;
    overflow-x: auto;
}

table {
    width: 100%;
    border-collapse: collapse;
    font-size: 9px;
    white-space: nowrap;
}

th {
    background: #e8edf1;
    border: 1px solid #d1d7dc;
    padding: 3px 4px;
    text-align: center;
}

td {
    border: 1px solid #e0e3e6;
    padding: 3px 4px;
    text-align: center;
}

td.name {
    text-align: left;
    font-weight: bold;
}

.all-skills td {
    background: #edf5ff;
    font-weight: bold;
}

.player {
    border: 1px solid #d8dde1;
    border-radius: 4px;
    margin-bottom: 5px;
    overflow: hidden;
}

.player-header {
    background: #f0f3f6;
    padding: 4px 6px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.player-name {
    font-size: 11px;
    font-weight: bold;
}

.player-info {
    font-size: 9px;
    color: #68727c;
}

details summary {
    cursor: pointer;
    padding: 4px 6px;
    background: #f8f9fa;
    border-top: 1px solid #dfe3e6;
    font-size: 10px;
    font-weight: bold;
    color: #404a53;
}

details[open] summary {
    margin-bottom: 4px;
}

@media (max-width: 1000px) {
    .teams {
        grid-template-columns: 1fr;
    }

    .header-row {
        align-items: flex-start;
        flex-direction: column;
        gap: 4px;
    }

    .match-meta {
        text-align: left;
    }
}
</style>

</head>

<body>

<div class="page">

<div class="header">

    <div class="header-row">

        <div>
            <div class="title">VolleyballStats</div>

            <% if (matchInfo != null) { %>

                <div class="match-title">
                    <%= matchInfo.getHomeTeamName() %> vs <%= matchInfo.getAwayTeamName() %>
                </div>

            <% } %>
        </div>

        <% if (matchInfo != null) { %>

            <div class="match-meta">

                <div>
                    <%= matchInfo.getDate() == null ? "" : matchInfo.getDate() %>
                    <%= matchInfo.getTime() == null ? "" : matchInfo.getTime() %>
                </div>

                <div>
                    <%= matchInfo.getCompetition() == null ? "" : matchInfo.getCompetition() %>
                </div>

                <div>
                    <%= matchInfo.getRound() == null ? "" : matchInfo.getRound() %>
                </div>

                <div>
                    <%= matchInfo.getVenue() == null ? "" : matchInfo.getVenue() %>
                </div>

            </div>

        <% } %>

    </div>

	<% if (matchStatistics != null && matchStatistics.getSetScores() != null) { %>
        <div class="scores">

            <% int setNumber = 1; %>

            <% for (String score : matchStatistics.getSetScores()) { %>

				<div class="score">
				    SET <%= setNumber++ %>: <%= formatSetScore(score) %>
				</div>

            <% } %>

        </div>

    <% } %>

</div>

<div class="content">

    <% if (error != null) { %>

        <div class="error">
            <%= error %>
        </div>

    <% } else if (matchSetup != null && matchStatistics != null && matchInfo != null) { %>

        <div class="teams">

            <%
            TeamStatistics[] statisticsTeams = {
                matchStatistics.getHomeTeam(),
                matchStatistics.getAwayTeam()
            };

            int teamIndex = 0;

            for (TeamStatistics teamStatistics : statisticsTeams) {

                int teamId = teamIndex;
                String teamName = teamIndex == 0 ? matchInfo.getHomeTeamName() : matchInfo.getAwayTeamName();
                teamIndex++;
            %>

                <div class="team">

                    <div class="team-header">

                        <div class="team-name">
                            <%= teamName %>
                        </div>

						<div class="team-side">
						    <%= teamId == 0 ? "HOME" : "AWAY" %>
						</div>

                    </div>

                    <div class="section">

                        <div class="section-title">
                            TEAM MATCH STATISTICS
                        </div>

                        <div class="table-wrap">

                            <table>

                                <thead>

                                    <tr>
                                        <th>Skill</th>
                                        <th>Pts</th>
                                        <th>Err</th>
                                        <th>Events</th>
                                        <th>Rec</th>
                                        <th>Rec%</th>
                                        <th>Att</th>
                                        <th>Att%</th>
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
                                    </tr>

                                </thead>

                                <tbody>

                                    <% for (StatisticEntry entry : teamStatistics.getMatchStatistics()) { %>

                                        <tr class="<%= entry.getSkill() == 0 ? "all-skills" : "" %>">

                                            <td class="name">
                                                <%= entry.getSkillName() %>
                                            </td>

                                            <td><%= entry.getStatistics().getPoints() %></td>
                                            <td><%= entry.getStatistics().getErrors() %></td>
                                            <td><%= entry.getStatistics().getTotalEvents() %></td>
                                            <td><%= entry.getStatistics().getReceptionTotal() %></td>
                                            <td><%= entry.getStatistics().getReceptionPositive() %></td>
                                            <td><%= entry.getStatistics().getAttackTotal() %></td>
                                            <td><%= entry.getStatistics().getAttackPercentage() %></td>
                                            <td><%= entry.getStatistics().getMm() %></td>
                                            <td><%= entry.getStatistics().getMmp() %></td>
                                            <td><%= entry.getStatistics().getMmc() %></td>
                                            <td><%= entry.getStatistics().getM() %></td>
                                            <td><%= entry.getStatistics().getS() %></td>
                                            <td><%= entry.getStatistics().getSp() %></td>
                                            <td><%= entry.getStatistics().getSc() %></td>
                                            <td><%= entry.getStatistics().getP() %></td>
                                            <td><%= entry.getStatistics().getD() %></td>
                                            <td><%= entry.getStatistics().getDp() %></td>
                                            <td><%= entry.getStatistics().getDc() %></td>
                                            <td><%= entry.getStatistics().getE() %></td>

                                        </tr>

                                    <% } %>

                                </tbody>

                            </table>

                        </div>

                    </div>

                    <div class="section">

                        <div class="section-title">
                            PLAYERS
                        </div>

                        <%
                        for (PlayerInfo playerInfo : matchInfo.getPlayers()) {

                            if (playerInfo.getTeam() != teamId) {
                                continue;
                            }

                            PlayerStatistics playerStatistics = null;

                            for (PlayerStatistics candidate : teamStatistics.getPlayers()) {

                                if (candidate.getJerseyNumber() == playerInfo.getJerseyNumber()) {
                                    playerStatistics = candidate;
                                    break;
                                }

                            }

                            if (playerStatistics == null) {
                                continue;
                            }
                        %>

                            <div class="player">

                                <div class="player-header">

                                    <div class="player-name">
                                        #<%= playerInfo.getJerseyNumber() %>
                                        <%= playerInfo.getFullName() %>
                                    </div>

                                    <div class="player-info">
                                        Position: <%= playerInfo.getPosition() %>
                                        |
                                        <%= playerInfo.isStarter() ? "Starter" : "Substitute" %>
                                    </div>

                                </div>

                                <div class="table-wrap">

                                    <table>

                                        <thead>

                                            <tr>
                                                <th>Skill</th>
                                                <th>Pts</th>
                                                <th>Err</th>
                                                <th>Events</th>
                                                <th>Rec</th>
                                                <th>Rec%</th>
                                                <th>Att</th>
                                                <th>Att%</th>
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
                                            </tr>

                                        </thead>

                                        <tbody>

                                            <% for (StatisticEntry entry : playerStatistics.getMatchStatistics()) { %>

                                                <tr class="<%= entry.getSkill() == 0 ? "all-skills" : "" %>">

                                                    <td class="name">
                                                        <%= entry.getSkillName() %>
                                                    </td>

                                                    <td><%= entry.getStatistics().getPoints() %></td>
                                                    <td><%= entry.getStatistics().getErrors() %></td>
                                                    <td><%= entry.getStatistics().getTotalEvents() %></td>
                                                    <td><%= entry.getStatistics().getReceptionTotal() %></td>
                                                    <td><%= entry.getStatistics().getReceptionPositive() %></td>
                                                    <td><%= entry.getStatistics().getAttackTotal() %></td>
                                                    <td><%= entry.getStatistics().getAttackPercentage() %></td>
                                                    <td><%= entry.getStatistics().getMm() %></td>
                                                    <td><%= entry.getStatistics().getMmp() %></td>
                                                    <td><%= entry.getStatistics().getMmc() %></td>
                                                    <td><%= entry.getStatistics().getM() %></td>
                                                    <td><%= entry.getStatistics().getS() %></td>
                                                    <td><%= entry.getStatistics().getSp() %></td>
                                                    <td><%= entry.getStatistics().getSc() %></td>
                                                    <td><%= entry.getStatistics().getP() %></td>
                                                    <td><%= entry.getStatistics().getD() %></td>
                                                    <td><%= entry.getStatistics().getDp() %></td>
                                                    <td><%= entry.getStatistics().getDc() %></td>
                                                    <td><%= entry.getStatistics().getE() %></td>

                                                </tr>

                                            <% } %>

                                        </tbody>

                                    </table>

                                </div>

                                <details>

                                    <summary>
                                        SET STATISTICS
                                    </summary>

                                    <div class="table-wrap">

                                        <table>

                                            <thead>

                                                <tr>
                                                    <th>Set</th>
                                                    <th>Skill</th>
                                                    <th>Pts</th>
                                                    <th>Err</th>
                                                    <th>Events</th>
                                                    <th>Rec</th>
                                                    <th>Rec%</th>
                                                    <th>Att</th>
                                                    <th>Att%</th>
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
                                                </tr>

                                            </thead>

                                            <tbody>

                                                <% for (StatisticEntry entry : playerStatistics.getSetStatistics()) { %>

                                                    <tr class="<%= entry.getSkill() == 0 ? "all-skills" : "" %>">

                                                        <td><%= entry.getSetNumber() %></td>

                                                        <td class="name">
                                                            <%= entry.getSkillName() %>
                                                        </td>

                                                        <td><%= entry.getStatistics().getPoints() %></td>
                                                        <td><%= entry.getStatistics().getErrors() %></td>
                                                        <td><%= entry.getStatistics().getTotalEvents() %></td>
                                                        <td><%= entry.getStatistics().getReceptionTotal() %></td>
                                                        <td><%= entry.getStatistics().getReceptionPositive() %></td>
                                                        <td><%= entry.getStatistics().getAttackTotal() %></td>
                                                        <td><%= entry.getStatistics().getAttackPercentage() %></td>
                                                        <td><%= entry.getStatistics().getMm() %></td>
                                                        <td><%= entry.getStatistics().getMmp() %></td>
                                                        <td><%= entry.getStatistics().getMmc() %></td>
                                                        <td><%= entry.getStatistics().getM() %></td>
                                                        <td><%= entry.getStatistics().getS() %></td>
                                                        <td><%= entry.getStatistics().getSp() %></td>
                                                        <td><%= entry.getStatistics().getSc() %></td>
                                                        <td><%= entry.getStatistics().getP() %></td>
                                                        <td><%= entry.getStatistics().getD() %></td>
                                                        <td><%= entry.getStatistics().getDp() %></td>
                                                        <td><%= entry.getStatistics().getDc() %></td>
                                                        <td><%= entry.getStatistics().getE() %></td>

                                                    </tr>

                                                <% } %>

                                            </tbody>

                                        </table>

                                    </div>

                                </details>

                            </div>

                        <% } %>

                    </div>

                </div>

            <% } %>

        </div>

    <% } %>

</div>

</div>

<script>
let lastMatchUpdate = <%= System.currentTimeMillis() %>;

setInterval(function() {
    fetch('<%= request.getContextPath() %>/match-status', {
        cache: 'no-store'
    })
    .then(response => response.text())
    .then(timestamp => {
        const currentMatchUpdate = Number(timestamp);

        if (currentMatchUpdate > 0 && currentMatchUpdate > lastMatchUpdate) {
            window.location.reload();
        }
    })
    .catch(function() {
    });
}, 2000);
</script>

</body>
</html>
