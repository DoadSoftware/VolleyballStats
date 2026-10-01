# VolleyballStats

Spring MVC (not Spring Boot) Maven WAR application for reading Genius Sports Data Volley / Click&Scout statistics through `DVStat.dll` using JNA.

## Stack
- JDK 17
- Spring MVC 6
- Tomcat 10.1
- Maven WAR
- Java configuration only
- JNA for `DVStat.dll`

## Configuration structure
There is intentionally **no `web.xml`**, **no `applicationContext.xml`**, and **no Spring XML configuration**.

Java configuration is under `com.volleyball.config`:
- `DataSourceConfig`
- `WebMvcConfig`
- `core/SpringMvcInitializer`

This follows the Java-config pattern used in the migrated JDK 17 DOAD sports applications.

## Page flow
1. `/` opens `initialise.jsp`.
2. Browse/select `DvStat.dll` and enter the Data Volley / Click&Scout statistics folder.
3. Click **Submit & View Statistics**.
4. The DLL is uploaded to the server temp folder and the application redirects to `/index`.
5. `index.jsp` calls all five DVStat functions and displays the returned statistics.

The initial selection is Home team (`IdTeam=0`), team total (`IdPlayer=100`), all skills (`Skill=0`), whole match (`SetN=0`).

For live use, the statistics folder can be the shared DV-Share path, for example `\ScoutmanComputer\DV-Share`.

## Important
The Java process must match the DLL architecture. The included SDK DLL is x64, so run Tomcat with a 64-bit JDK 17.
