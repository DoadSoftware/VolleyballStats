<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

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
    <link rel="stylesheet" href="<c:url value="/css/index.css"/>">
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
		<div class="header-actions">
		    <form action="${pageContext.request.contextPath}/export-master-data" method="post">
		        <button type="submit" class="export-button">EXPORT</button>
		    </form>
		    <c:if test="${not empty exportMessage}">
		        <div class="export-message">${exportMessage}</div>
		    </c:if>
		    <c:if test="${not empty exportError}">
		        <div class="export-error">${exportError}</div>
		    </c:if>
		</div>

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

                       		String playerSide = teamId == 0 ? "HOME" : "AWAY";
                            if (!playerSide.equalsIgnoreCase(playerInfo.getTeam())) {
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
<script src="<c:url value="/js/index.js"/>"></script>
</body>
</html>
